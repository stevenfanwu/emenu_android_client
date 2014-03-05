package net.cloudmenu.emenu.task;

import android.content.Context;

import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import java.util.List;

import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IOrderService;
import cn.com.cloudstone.menu.server.thrift.api.IOrderService.Client;
import cn.com.cloudstone.menu.server.thrift.api.Order;
import cn.com.cloudstone.menu.server.thrift.api.TableEmptyException;
import cn.com.cloudstone.menu.server.thrift.api.UserNotLoginException;

public class QueryOrderTask extends TBaseTask {
    private String mTableId;
    private List<Order> mOrder;

    public QueryOrderTask(Context context, String tableId) {
        super(context);
        mTableId = tableId;
    }

    public List<Order> getOrder() {
        return mOrder;
    }

    @Override
    protected TServiceClient getClient() throws TException {
        return RPCHelper.getOrderService(mContext);
    }

    @Override
    protected Object process(TServiceClient client) throws TException,
            AException {
        IOrderService.Client iclient = (Client) client;
        String sid = ProfileHolder.getIns().getCurrentSid(mContext);
        try {
            mOrder = iclient.queryOrder(sid, mTableId);
            return ECode.SUCCESS;
        } catch (UserNotLoginException e) {
            return e;
        } catch (TableEmptyException e) {
            return e;
        }
    }

}
