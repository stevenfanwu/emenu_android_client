package net.cloudmenu.emenu.utils;

import android.content.Context;

import net.cloudmenu.emenu.net.RPCHelper;

import org.apache.thrift.TException;

import cn.buding.common.util.PreferenceHelper;
import cn.com.cloudstone.menu.server.thrift.api.IMEINotAllowedException;
import cn.com.cloudstone.menu.server.thrift.api.IProfileService;
import cn.com.cloudstone.menu.server.thrift.api.Login;
import cn.com.cloudstone.menu.server.thrift.api.UserType;
import cn.com.cloudstone.menu.server.thrift.api.WrongUsernameOrPasswordException;

public class ProfileHolder {
    private static ProfileHolder mInstance;

    public static ProfileHolder getIns() {
        if (mInstance == null)
            mInstance = new ProfileHolder();
        return mInstance;
    }

    private static final String PRE_KEY_PASSWORD = "pre_key_password";
    private static final String PRE_KEY_CURRENT_LOGIN = "pre_key_current_sid";
    private static final String PRE_KEY_CURRENT_TABLEID = "pre_key_current_table_id";

    public boolean isLogined(Context context) {
        Login user = getCurrentLogin(context);
        return user != null;
    }

    public boolean isTableOccupied(Context context) {
        return getCurrentTableId(context) != null;
    }

    public Login login(Context context, String username, String pwd)
            throws WrongUsernameOrPasswordException, TException,
            IMEINotAllowedException {
        Login login = null;
        if (GlobalConfig.isWorkWithoutNetWork(context)) {
            login = new Login();
            login.username = username;
            login.sessionId = "custom";
            login.userType = UserType.Waiter;
        } else {
            IProfileService.Client client = RPCHelper
                    .getProfileService(context);
            String imei = MenuUtils.getCustomIMEI(context);
            login = client.loginUser(username, pwd, imei);
        }

        String loginStr = ThriftUtils.convertToString(login);
        PreferenceHelper helper = PreferenceHelper.getHelper(context);
        helper.writePreference(PRE_KEY_PASSWORD + username, pwd);
        helper.writePreference(PRE_KEY_CURRENT_LOGIN, loginStr);
        return login;
    }

    public String getCachedPwd(Context context, String username) {
        return PreferenceHelper.getHelper(context).readPreference(
                PRE_KEY_PASSWORD + username);
    }

    public Login getCurrentLogin(Context context) {
        String str = PreferenceHelper.getHelper(context).readPreference(
                PRE_KEY_CURRENT_LOGIN);
        if (str != null)
            return ThriftUtils.convertTBase(Login.class, str);
        return null;
    }

    public String getCurrentUser(Context context) {
        Login login = getCurrentLogin(context);
        return login != null ? login.getUsername() : null;
    }

    public String getCurrentSid(Context context) {
        Login login = getCurrentLogin(context);
        return login != null ? login.getSessionId() : null;
    }

    public String getCurrentTableId(Context context) {
        return PreferenceHelper.getHelper(context).readPreference(
                PRE_KEY_CURRENT_TABLEID);
    }

    public void setCurrentTableId(Context context, String tableId) {
        PreferenceHelper.getHelper(context).writePreference(
                PRE_KEY_CURRENT_TABLEID, tableId);
    }

    public void removeCurrentTable(Context context) {
        PreferenceHelper.getHelper(context).removePreference(
                PRE_KEY_CURRENT_TABLEID);
    }

    public void logout(Context context) {
        PreferenceHelper.getHelper(context).removePreference(
                PRE_KEY_CURRENT_LOGIN);
        PreferenceHelper.getHelper(context).removePreference(
                PRE_KEY_CURRENT_TABLEID);
    }
}
