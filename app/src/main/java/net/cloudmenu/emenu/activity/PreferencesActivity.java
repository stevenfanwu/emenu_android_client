package net.cloudmenu.emenu.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.utils.MenuUtils;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.buding.common.file.ImageBuffer;

public class PreferencesActivity extends PreferenceActivity implements
        OnPreferenceClickListener, OnPreferenceChangeListener {
    private Preference mPreClearImgCache;
    private EditTextPreference mPreSetIp;
    private Preference mPreImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mPreClearImgCache = findPreference(getString(R.string.pre_key_clear_image_cache));
        mPreClearImgCache.setOnPreferenceClickListener(this);

        mPreSetIp = (EditTextPreference) findPreference(getString(R.string.pre_key_custom_api_addr));
        mPreSetIp.setOnPreferenceChangeListener(this);

        mPreImei = findPreference(getString(R.string.pre_key_setting_imei));
        mPreImei.setSummary(MenuUtils.getCustomIMEI(this));

        loadBufferSize();
        initIpSetting();
    }

    private void initIpSetting() {
        if (mPreSetIp.getText() == null)
            mPreSetIp.setText(RPCHelper.DEFAULT_HOST_IP);
        mPreSetIp.setSummary(mPreSetIp.getText());
        mPreSetIp.getEditText().setText(mPreSetIp.getText());
    }

    private void loadBufferSize() {
        mPreClearImgCache.setTitle("缓存使用大小：计算中");
        new Thread() {
            public void run() {
                long size = ImageBuffer.getInstance().getAllBufferFileSize();
                final float sizef = size / 1024f / 1024f;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPreClearImgCache.setTitle("缓存使用大小："
                                + String.format("%.2fMB", sizef));
                    }
                });
            };
        }.start();
    }

    private void clearImageBuffer() {
        new ClearImageBufferTask(this).setCallback(new Callback() {
            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                loadBufferSize();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {
            }
        }).execute();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mPreClearImgCache) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("清除缓存会删除所有本地保存的图片，确认要清除缓存吗？");
            builder.setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearImageBuffer();
                        }
                    });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
            return true;
        }
        return false;
    }

    class ClearImageBufferTask extends HandlerMessageTask {

        public ClearImageBufferTask(Context context) {
            super(context);
            setShowProgessDialog(true);
            setLoadingMessage("删除缓存ing");
            setCodeMsg(ECode.SUCCESS, "删除完毕");
        }

        @Override
        protected Object doInBackground(Void... params) {
            ImageBuffer.getInstance().deleteAllFile();
            return ECode.SUCCESS;
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPreSetIp) {
            mPreSetIp.setSummary(newValue.toString());
            mPreSetIp.getEditText().setText(newValue.toString());
            return true;
        }
        return false;
    }

}
