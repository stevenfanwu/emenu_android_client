package net.cloudmenu.emenu.dialog;

import android.content.Context;
import android.view.View;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.GlobalConfig;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService.Client;
import cn.com.cloudstone.menu.server.thrift.api.ServiceType;
import cn.com.cloudstone.menu.server.thrift.api.TableEmptyException;
import cn.com.cloudstone.menu.server.thrift.api.UserNotLoginException;

public class ServiceDialog extends ButtonDialog {

    public ServiceDialog(Context context) {
        super(context);
        setTitle("餐厅服务");
        addButton(R.id.bt_waiter, "呼叫服务员");
        addButton(R.id.bt_switch, "换餐碟");
        addButton(R.id.bt_urge, "催菜");
        addButton(R.id.bt_pay, "结账");
        addButton(R.id.bt_cancel, "取消");
    }

    @Override
    public void onClick(View v) {
        ServiceType type = null;
        switch (v.getId()) {
        case R.id.bt_waiter:
            type = ServiceType.CallForWaiter;
            break;
        case R.id.bt_switch:
            type = ServiceType.RenewTableware;
            break;
        case R.id.bt_pay:
            type = ServiceType.CheckOut;
            break;
        case R.id.bt_urge:
            type = ServiceType.PromptDishes;
            break;
        }
        if (type != null) {
            new CallServiceTask(getContext(), type).setCallback(new Callback() {
                @Override
                public void onSuccess(HandlerMessageTask task, Object t) {
                    cancel();
                }

                @Override
                public void onFail(HandlerMessageTask task, Object t) {

                }
            }).execute();
        }
        super.onClick(v);
    }

    private class CallServiceTask extends TBaseTask {
        private ServiceType mType;

        public CallServiceTask(Context context, ServiceType type) {
            super(context);
            setCodeMsg(ECode.SUCCESS, "呼叫成功");
            setShowProgessDialog(true);
            mType = type;
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
                if (!GlobalConfig.isWorkWithoutNetWork(mContext)) {
                    iclient.callService(sid, tid, mType);
                }
                return ECode.SUCCESS;
            } catch (UserNotLoginException e) {
                return e;
            } catch (TableEmptyException e) {
                return e;
            }
        }

    }
}
