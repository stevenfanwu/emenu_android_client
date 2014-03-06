package net.cloudmenu.emenu.task;


import android.content.Context;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IMEINotAllowedException;
import cn.com.cloudstone.menu.server.thrift.api.WrongUsernameOrPasswordException;

public class LoginTask extends TBaseTask {
    private String mUser;
    private String mPwd;
    private static final int ECODE_WRONG_PARAMS = 1001;
    private static final int ECODE_WRONG_IMEI = 1002;

    public LoginTask(Context context, String user, String pwd) {
        super(context);
        setShowProgessDialog(true);
        setCodeMsg(ECODE_WRONG_PARAMS, context.getString(R.string.wrong_login));
        setCodeMsg(ECODE_WRONG_IMEI, context.getString(R.string.wrong_device));
        mUser = user;
        mPwd = pwd;
    }

    @Override
    protected TServiceClient getClient() throws TException {
        return null;
    }

    @Override
    protected Object process(TServiceClient client) throws TException,
            AException {
        try {
            ProfileHolder.getIns().login(mContext, mUser, mPwd);
            return ECode.SUCCESS;
        } catch (WrongUsernameOrPasswordException e) {
            return ECODE_WRONG_PARAMS;
        } catch (IMEINotAllowedException e) {
            return ECODE_WRONG_IMEI;
        }
    }

}