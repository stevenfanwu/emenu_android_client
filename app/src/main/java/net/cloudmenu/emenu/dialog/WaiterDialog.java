package net.cloudmenu.emenu.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.GlobalConfig;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService.Client;
import cn.com.cloudstone.menu.server.thrift.api.PermissionDenyExcpetion;
import cn.com.cloudstone.menu.server.thrift.api.UserNotLoginException;

public class WaiterDialog extends ButtonDialog implements
        android.view.View.OnClickListener {
    private Context mContext;

    public WaiterDialog(Context context) {
        super(context);
        mContext = context;
        setTitle("服务员功能");
        addButton(R.id.bt_exit_menu, "退出菜单");
        addButton(R.id.bt_clear_table, "清台");
        // addButton(R.id.bt_change_table, "转台");
        // addButton(R.id.bt_merge_table, "并桌");
        addButton(R.id.bt_cancel, "Cancel");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_exit_menu:
            cancel();
            GlobalValue.getIns().onClearTable(getContext());
            ((Activity) mContext).finish();
            break;
        case R.id.bt_clear_table:
            new EmptyTableTask(getContext()).setCallback(new Callback() {
                @Override
                public void onSuccess(HandlerMessageTask task, Object t) {
                    cancel();
                    ((Activity) mContext).finish();
                    GlobalValue.getIns().onClearTable(getContext());
                }

                @Override
                public void onFail(HandlerMessageTask task, Object t) {

                }
            }).execute();
            break;
        case R.id.bt_merge_table:
            // TODO
            break;
        }
        super.onClick(v);
    }

    private class EmptyTableTask extends TBaseTask {

        public EmptyTableTask(Context context) {
            super(context);
            setShowProgessDialog(true);
            setCodeMsg(ECode.SUCCESS, "清桌成功");
        }

        @Override
        protected TServiceClient getClient() throws TException {
            return RPCHelper.getWaiterService(mContext);
        }

        @Override
        protected Object process(TServiceClient client) throws TException,
                AException {
            IWaiterService.Client iclient = (Client) client;
            try {
                String sid = ProfileHolder.getIns().getCurrentSid(mContext);
                String tid = ProfileHolder.getIns().getCurrentTableId(mContext);
                if (!GlobalConfig.isWorkWithoutNetWork(mContext))
                    iclient.emptyTable(sid, tid);
                return ECode.SUCCESS;
            } catch (UserNotLoginException e) {
                return e;
            } catch (PermissionDenyExcpetion e) {
                return e;
            }
        }

    }
}
