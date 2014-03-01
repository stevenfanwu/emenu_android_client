package net.cloudmenu.emenu.dialog;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.activity.PreferencesActivity;
import net.cloudmenu.emenu.dialog.CheckDialog.LoadNotesTask;
import net.cloudmenu.emenu.dialog.LoginDialog.GetUsersTask;
import net.cloudmenu.emenu.task.GetMenuTask;
import net.cloudmenu.emenu.task.QueryTableInfoTask;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.GlobalValue;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;

public class ManageDialog extends ButtonDialog {
    private Button btnUpdate;
    private Button btnSetting;

    public ManageDialog(Context context) {
        super(context);
        setTitle(R.string.system_maintainence_management);
        btnUpdate = addButton(R.id.bt_update, "Menu Update");
        btnSetting = addButton(R.id.bt_setting, "Settings");
        addButton(R.id.bt_cancel, "Cancel");
    }

    @Override
    public void onClick(View v) {
        if (v == btnUpdate) {
            UpdateMenuTask task = new UpdateMenuTask(getContext());
            task.setCodeMsg(ECode.SUCCESS, R.string.system_maintainence_update_finished);
            task.setCallback(new Callback() {
                @Override
                public void onSuccess(HandlerMessageTask task, Object t) {
                    GlobalValue.getIns().setMenu(null);
                }

                @Override
                public void onFail(HandlerMessageTask task, Object t) {
                }
            });
            task.execute();
        } else if (v == btnSetting) {
            getContext().startActivity(
                    new Intent(getContext(), PreferencesActivity.class));
        }
        super.onClick(v);
    }

    private class UpdateMenuTask extends TBaseTask {

        public UpdateMenuTask(Context context) {
            super(context);
            setCodeMsg(ECode.SUCCESS, R.string.system_maintainence_update_finished);
            setShowProgessDialog(true);
        }

        @Override
        protected TServiceClient getClient() throws TException {
            return null;
        }

        @Override
        protected Object process(TServiceClient client) throws TException,
                AException {
            GetMenuTask getMenuTask = new GetMenuTask(getContext());
            getMenuTask.setForseRefresh(true);
            Object res1 = getMenuTask.runBackground();

            GetUsersTask getUserTask = new GetUsersTask(mContext);
            getUserTask.setForseRefresh(true);
            Object res2 = getUserTask.runBackground();

            LoadNotesTask loadNotesTask = new LoadNotesTask(mContext);
            loadNotesTask.setForseRefresh(true);
            Object res3 = loadNotesTask.runBackground();

            QueryTableInfoTask tableTask = new QueryTableInfoTask(mContext);
            tableTask.setForseRefresh(true);
            Object res4 = tableTask.runBackground();

            Object success = ECode.SUCCESS;
            return (success.equals(res1) && success.equals(res2) && success
                    .equals(res3)) && success.equals(res4) ? ECode.SUCCESS
                    : ECode.FAIL;
        }

    }
}
