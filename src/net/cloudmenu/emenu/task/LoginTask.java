package net.cloudmenu.emenu.task;

import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import android.content.Context;
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
        setCodeMsg(ECODE_WRONG_PARAMS, "用户名或密码错误");
        setCodeMsg(ECODE_WRONG_IMEI, "您的设备不在允许列表中");
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