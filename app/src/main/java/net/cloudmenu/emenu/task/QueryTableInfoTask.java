package net.cloudmenu.emenu.task;

import android.content.Context;
import android.util.Log;

import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import java.util.List;

import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService.Client;
import cn.com.cloudstone.menu.server.thrift.api.PermissionDenyExcpetion;
import cn.com.cloudstone.menu.server.thrift.api.TableInfo;
import cn.com.cloudstone.menu.server.thrift.api.UserNotLoginException;

public class QueryTableInfoTask extends TBaseTask {
    private List<TableInfo> mInfos;
    private boolean mForseRefresh = false;

    public QueryTableInfoTask(Context context) {
        super(context);
        setShowProgessDialog(true);
        setShowCodeMsg(false);
    }

    public List<TableInfo> getInfos() {
        return mInfos;
    }

    public void setForseRefresh(boolean b) {
        mForseRefresh = b;
    }

    @Override
    protected TServiceClient getClient() throws TException {
        long cacheTime = RPCHelper.CACHE_TIME_LONG;
        if (mForseRefresh)
            cacheTime = RPCHelper.CACHE_TIME_REFRESH;
        return RPCHelper.getCachedWaiterService(mContext, cacheTime);
    }

    @Override
    protected Object process(TServiceClient client) throws TException,
            AException {
        IWaiterService.Client iclient = (Client) client;
        String sid = ProfileHolder.getIns().getCurrentSid(mContext);
        try {
            mInfos = iclient.queryTableInfos(sid);
            Log.d("QueryTable", "table size " + mInfos.size());
            return ECode.SUCCESS;
        } catch (UserNotLoginException e) {
            return e;
        } catch (PermissionDenyExcpetion e) {
            return e;
        }
    }

}
