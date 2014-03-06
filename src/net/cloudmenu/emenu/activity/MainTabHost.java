package net.cloudmenu.emenu.activity;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.dialog.ServiceDialog;
import net.cloudmenu.emenu.dialog.WaiterDialog;
import net.cloudmenu.emenu.task.LoginTask;
import net.cloudmenu.emenu.utils.GlobalConfig;
import net.cloudmenu.emenu.utils.ProfileHolder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.buding.common.activity.BaseTabHost;
import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.widget.AsyncImageView;

public class MainTabHost extends BaseTabHost implements OnLongClickListener {
    public static final String EXTRA_MENU_DEFAULT_INDEX = "extra_menu_deafult_index";
    public static final String ACTION_BUTTON = "net.cloudmenu.emenu.action.button";
    public static final String EXTRA_BUTTON_ID = "extra_button_id";
//    private Button btnLeft;
//    private Button btnRight;
//    private Button btnService;
//    private Button btnContact;
    private AsyncImageView mLogo;

    private int mMenuDefaultIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMenuDefaultIndex = getIntent()
                .getIntExtra(EXTRA_MENU_DEFAULT_INDEX, 0);
        super.onCreate(savedInstanceState);
    }

    public void setLogoUrl(String url) {
        mLogo.postLoading(url);
    }

    @Override
    protected void initElements() {
        super.initElements();
//        btnLeft = (Button) findViewById(R.id.btn_left);
//        btnRight = (Button) findViewById(R.id.btn_right);
//        btnService = (Button) findViewById(R.id.btn_service);
//        btnContact = (Button) findViewById(R.id.btn_contact);
//        btnLeft.setOnClickListener(this);
//        btnRight.setOnClickListener(this);
//        btnService.setOnClickListener(this);
//        btnContact.setOnClickListener(this);
        mLogo = (AsyncImageView) findViewById(R.id.logo);
        mLogo.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == mLogo) {
            showWaiterDialog();
            return true;
        }
        return false;
    }

    private void showWaiterDialog() {
        if(GlobalConfig.isWorkWithoutNetWork(this)){
            new WaiterDialog(MainTabHost.this).show();
            return;
        }
        final EditText editPwd = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.password_required))
                .setView(editPwd)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = editPwd.getText().toString();
                        confirmWaiterPwd(pwd);
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();

    }

    private void confirmWaiterPwd(String pwd) {
        String user = ProfileHolder.getIns().getCurrentUser(this);
        LoginTask task = new LoginTask(this, user, pwd);
        task.setCallback(new Callback() {

            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                new WaiterDialog(MainTabHost.this).show();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {

            }
        });
        task.execute();
    }

//    @Override
//    public void onClick(View v) {
//        if (v == btnLeft) {
//            Intent intent = new Intent(ACTION_BUTTON);
//            intent.putExtra(EXTRA_BUTTON_ID, btnLeft.getId());
//            sendBroadcast(intent);
//        } else if (v == btnRight) {
//            Intent intent = new Intent(ACTION_BUTTON);
//            intent.putExtra(EXTRA_BUTTON_ID, btnRight.getId());
//            sendBroadcast(intent);
//        } else if (v == btnService) {
//            new ServiceDialog(this).show();
//        } else if (v == btnContact) {
//
//        }
//    }

    @Override
    protected int getLayout() {
        return R.layout.tab_host_main;
    }

    @Override
    protected void initTabParams() {
        mTabCount = 2;
        mTabIntents = new Intent[mTabCount];
        mTabIntents[0] = new Intent(this, MenuTabHost.class);
        mTabIntents[0].putExtra(MenuTabHost.EXTRA_INDEX, mMenuDefaultIndex);
        mTabIntents[1] = new Intent(this, OrderActivity.class);
        // mTabIntents[2] = new Intent(this, CommentActivity.class);

        mTabIndicator = new String[mTabCount];
        mTabIndicator[0] = "Menu";
        mTabIndicator[1] = "Your Order";
        // mTabIndicator[2] = "评    论";
    }

    @Override
    protected View getTabIndicatorView(int i) {
        return getLayoutInflater().inflate(R.layout.tab_indicator_main, null);
    }
}
