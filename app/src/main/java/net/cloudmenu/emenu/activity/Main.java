package net.cloudmenu.emenu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.dialog.AlertDialog;
import net.cloudmenu.emenu.dialog.LoginDialog;
import net.cloudmenu.emenu.dialog.LoginDialog.OnLoginSuccessListener;
import net.cloudmenu.emenu.dialog.ManageDialog;
import net.cloudmenu.emenu.dialog.TableDialog;
import net.cloudmenu.emenu.dialog.TableInfoDialog;
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
import cn.com.cloudstone.menu.server.thrift.api.IProfileService;
import cn.com.cloudstone.menu.server.thrift.api.IProfileService.Client;

public class Main extends Activity implements OnClickListener,
        OnLongClickListener {
    private Button btnLock;
    private Button btnTable;
    private Button btnTableInfo;
    private Button btnManage;
//    private TextView tvLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    private void initElements() {
        btnLock = (Button) findViewById(R.id.btn_lock);
        btnTable = (Button) findViewById(R.id.btn_menu);
        btnTableInfo = (Button) findViewById(R.id.btn_table_info);
        btnManage = (Button) findViewById(R.id.btn_manage);
//        tvLogin = (TextView) findViewById(R.id.tv_login);
        btnLock.setOnClickListener(this);
        btnTable.setOnClickListener(this);
        btnTable.setOnLongClickListener(this);
        btnTableInfo.setOnClickListener(this);
        btnManage.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == btnTable) {
            startActivity(new Intent(this, MainTabHost.class));
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == btnLock) {
            if (ProfileHolder.getIns().isLogined(this)) {
                showLogoutDialog();
            } else {
                showLoginDialog();
            }
        } else if (v == btnTable) {
            if (ProfileHolder.getIns().isLogined(this)) {
                showTableDialog();
            } else {
                showLoginDialog();
            }
        } else if (v == btnTableInfo) {
            showTableInfoDialog();
        } else if (v == btnManage) {
            showManageDialog();
        }

    }

    private void refreshContent() {
        if (ProfileHolder.getIns().isLogined(this)) {
//            tvLogin.setText(getString(R.string.welcome) + ','
//                    + ProfileHolder.getIns().getCurrentUser(this));
        } else {
//            tvLogin.setText(getString(R.string.not_login));
        }
    }

    private void showLoginDialog() {
        LoginDialog lDialog = new LoginDialog(this);
        lDialog.setOnLoginSuccessListener(new OnLoginSuccessListener() {
            @Override
            public void onLoginSuccess() {
                refreshContent();
            }
        });
        lDialog.show();
    }

    private void showLogoutDialog() {
        AlertDialog aDialog = new AlertDialog(this);
        aDialog.setButton1(getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogoutTask task = new LogoutTask(Main.this);
                task.setCallback(new Callback() {
                    @Override
                    public void onSuccess(HandlerMessageTask task, Object t) {
                        refreshContent();
                    }

                    @Override
                    public void onFail(HandlerMessageTask task, Object t) {

                    }
                });
                task.execute();
            }
        });
        aDialog.setTitle("Warning");
        aDialog.setMessage(getString(R.string.confirm_logout));
        aDialog.show();
    }

    private void showTableDialog() {
        TableDialog dialog = new TableDialog(this);
        dialog.show();
    }

    private void showTableInfoDialog() {
        TableInfoDialog dialog = new TableInfoDialog(this);
        dialog.show();
    }

    private void showManageDialog() {
        ManageDialog dialog = new ManageDialog(this);
        dialog.show();
    }

    class LogoutTask extends TBaseTask {

        public LogoutTask(Context context) {
            super(context);
            setShowProgessDialog(true);
        }

        @Override
        protected TServiceClient getClient() throws TException {
            return RPCHelper.getProfileService(Main.this);
        }

        @Override
        protected Object process(TServiceClient client) throws TException,
                AException {
            IProfileService.Client iclient = (Client) client;
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