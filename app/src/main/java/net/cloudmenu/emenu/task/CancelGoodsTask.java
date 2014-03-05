package net.cloudmenu.emenu.task;

import android.content.Context;

import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IOrderService;
import cn.com.cloudstone.menu.server.thrift.api.UserNotLoginException;

public class CancelGoodsTask extends TBaseTask {
    private int mOrderId;
    private int mGoodsId;

    public CancelGoodsTask(Context context, int orderId, int goodsId) {
        super(context);
        setShowProgessDialog(true);
        mOrderId = orderId;
        mGoodsId = goodsId;
    }

    @Override
    protected TServiceClient getClient() throws TException {
        return RPCHelper.getOrderService(mContext);
    }

    @Override
    protected Object process(TServiceClient client) throws TException,
            AException {
        String sid = ProfileHolder.getIns().getCurrentSid(mContext);
        try {
            boolean res = ((IOrderService.Client) client).cancelGoods(sid,
                    mOrderId, mGoodsId);
            return ECode.SUCCESS;
        } catch (UserNotLoginException e) {
            return e;
        }
    }

}