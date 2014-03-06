package net.cloudmenu.emenu.dialog;

import java.util.List;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.task.LoginTask;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.buding.common.util.PreferenceHelper;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IProfileService;
import cn.com.cloudstone.menu.server.thrift.api.IProfileService.Client;

public class LoginDialog extends AlertDialog implements OnClickListener,
        OnItemSelectedListener, OnCheckedChangeListener {
    private EditText etPassword;
    private Spinner spUsername;
    private CheckBox cbRemPwd;

    private OnLoginSuccessListener mOnLoginSuccessListener;

    private static final String PRE_KEY_REM_PWD_CHECKED = "pre_key_rem_pwd_checked";

    public LoginDialog(Context context) {
        super(context);
        setTitle(context.getString(R.string.login_welcome));
        setButton1(context.getString(android.R.string.ok), this);
        setButton2(context.getString(android.R.string.cancel), this);
        setView(R.layout.dialog_frame_login);
        initElements();
    }

    private boolean mFirstTime = true;

    @Override
    public void show() {
        super.show();
        if (mFirstTime) {
            initUserData();
            mFirstTime = false;
        }
    }

    private void initElements() {
        etPassword = (EditText) findViewById(R.id.et_password);
        spUsername = (Spinner) findViewById(R.id.sp_username);
        spUsername.setOnItemSelectedListener(this);
        cbRemPwd = (CheckBox) findViewById(R.id.cb_rem_pwd);
        String remPwd = PreferenceHelper.getHelper(getContext())
                .readPreference(PRE_KEY_REM_PWD_CHECKED);
        if (remPwd != null && Boolean.valueOf(remPwd)) {
            cbRemPwd.setChecked(true);
        }
        cbRemPwd.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == cbRemPwd) {
            PreferenceHelper.getHelper(getContext()).writePreference(
                    PRE_KEY_REM_PWD_CHECKED, "" + isChecked);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        String user = spUsername.getItemAtPosition(arg2).toString();
        if (cbRemPwd.isChecked()) {
            String pwd = ProfileHolder.getIns()
                    .getCachedPwd(getContext(), user);
            etPassword.setText(pwd);
        }
    }

    private void initUserAdapter(List<String> users) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item);
        for (String s : users)
            adapter.add(s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUsername.setAdapter(adapter);
    }

    private void initUserData() {
        final GetUsersTask userTask = new GetUsersTask(getContext());
        userTask.setCallback(new Callback() {
            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                initUserAdapter(userTask.getResult());
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {
            }
        });
        userTask.execute();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case R.id.bt_1:
            if (spUsername.getSelectedItem() == null) {
                break;
            }
            String pwd = etPassword.getText().toString();
            String user = spUsername.getSelectedItem().toString();
            LoginTask task = makeLoginTask(user, pwd);
            task.execute();
            break;
        case R.id.bt_2:
            cancel();
            break;
        }
    }

    private LoginTask makeLoginTask(String user, String pwd) {
        LoginTask task = new LoginTask(getContext(), user, pwd);
        task.setCallback(new Callback() {
            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                if (mOnLoginSuccessListener != null) {
                    mOnLoginSuccessListener.onLoginSuccess();
                }
                cancel();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {
                cancel();
            }
        });
        return task;
    }

    public void setOnLoginSuccessListener(OnLoginSuccessListener l) {
        mOnLoginSuccessListener = l;
    }

    public interface OnLoginSuccessListener {
        public void onLoginSuccess();
    }

    public static class GetUsersTask extends TBaseTask {
        private List<String> mResult;
        private boolean mForseRefresh = false;

        public GetUsersTask(Context context) {
            super(context);
            setShowProgessDialog(true);
            setShowCodeMsg(false);
        }

        public void setForseRefresh(boolean b) {
            mForseRefresh = b;
        }

        public List<String> getResult() {
            return mResult;
        }

        @Override
        protected TServiceClient getClient() throws TException {
            long cacheTime = RPCHelper.CACHE_TIME_LONG;
            if (mForseRefresh)
                cacheTime = RPCHelper.CACHE_TIME_REFRESH;
            return RPCHelper.getCachedProfileService(mContext, cacheTime);
        }

        @Override
        protected Object process(TServiceClient client) throws TException,
                AException {
            IProfileService.Client iclient = (Client) client;
            mResult = iclient.getAllUsers();
            return ECode.SUCCESS;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
