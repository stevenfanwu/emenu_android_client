package net.cloudmenu.emenu.task;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import net.cloudmenu.emenu.dialog.ProgressDialog;
import net.cloudmenu.emenu.utils.ThriftUtils;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;

public abstract class TBaseTask extends HandlerMessageTask {
    private static final int ECODE_AEXCEPTION = 312;

    public TBaseTask(Context context) {
        super(context);
    }

    protected void showResultMessage(String codeMsg) {
        Toast.makeText(mContext, codeMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Dialog createLoadingDialog() {
        return new ProgressDialog(mContext);
    }

    @Override
    protected final Object doInBackground(Void... params) {
        Object res = null;
        TServiceClient client = null;
        try {
            client = getClient();
            res = process(client);
        } catch (TException e) {
            return ECode.FAIL;
        } finally {
            ThriftUtils.releaseClient(client);
        }
        return res;
    }

    protected abstract TServiceClient getClient() throws TException;

    protected abstract Object process(TServiceClient client) throws TException,
            AException;

}
