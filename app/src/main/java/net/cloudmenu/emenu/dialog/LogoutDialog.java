package net.cloudmenu.emenu.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.WindowManager;

import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.GlobalConfig;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.IProfileService;


public class LogoutDialog extends AlertDialog{
    private WindowManager.LayoutParams lp;

    public LogoutDialog(final Context context) {
        super(context);
        setTitle("Warning: Logout?");
        setButton1(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogoutTask task = new LogoutTask(context);
                task.setCallback(new HandlerMessageTask.Callback() {
                    @Override
                    public void onSuccess(HandlerMessageTask task, Object t) {

                    }

                    @Override
                    public void onFail(HandlerMessageTask task, Object t) {

                    }
                });
                task.execute();
            }
        });
        initElements();
    }

    @Override
    public void show() {
        getWindow().setAttributes(lp);
        super.show();
    }

    private void initElements() {
        lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    class LogoutTask extends TBaseTask {
        private Context context;

        public LogoutTask(Context context) {
            super(context);
            this.context = context;
            setShowProgessDialog(true);
        }

        @Override
        protected TServiceClient getClient() throws TException {
            return RPCHelper.getProfileService(context);
        }

        @Override
        protected Object process(TServiceClient client) throws TException {
            IProfileService.Client iclient = (IProfileService.Client) client;
            try {
                if (!GlobalConfig.isWorkWithoutNetWork(mContext)) {
                    String sid = ProfileHolder.getIns().getCurrentSid(mContext);
                    iclient.logout(sid);
                }
            } catch (Throwable e) {
                Log.e("Logout", "failed", e);
            }
            ProfileHolder.getIns().logout(mContext);
            return ECode.SUCCESS;
        }

    }

}
