package net.cloudmenu.emenu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.dialog.LoginDialog;
import net.cloudmenu.emenu.dialog.LogoutDialog;
import net.cloudmenu.emenu.dialog.ManageDialog;
import net.cloudmenu.emenu.dialog.TableDialog;
import net.cloudmenu.emenu.dialog.TableInfoDialog;
import net.cloudmenu.emenu.utils.ProfileHolder;

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
                LogoutDialog logoutDialog = new LogoutDialog(this);
                logoutDialog.show();
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

    private void showLoginDialog() {
        LoginDialog lDialog = new LoginDialog(this);
        lDialog.show();
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


}