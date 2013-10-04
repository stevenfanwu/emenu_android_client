package net.cloudmenu.emenu;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.utils.MenuUtils;
import net.cloudmenu.emenu.utils.ProfileHolder;
import net.cloudmenu.emenu.utils.ThriftUtils;

import org.apache.thrift.TException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import cn.buding.common.file.FileUtil;
import cn.buding.common.file.ImageBuffer;
import cn.buding.common.net.BaseHttpsManager;
import cn.com.cloudstone.menu.server.thrift.api.IPadInfoService;
import cn.com.cloudstone.menu.server.thrift.api.PadInfo;

public class Application extends android.app.Application {
    private static final String TAG = "Application";
    public static final String ROOT_PATH = ".emenu";
    public static final String IMAGE_BUFFER_PATH = ROOT_PATH + "/image2";

    @Override
    public void onCreate() {
        super.onCreate();
        onAppStart();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                String log = Log.getStackTraceString(ex);
                if (log != null) {
                    File f = FileUtil.getFile(Application.this, ROOT_PATH,
                            "crash.log");
                    FileUtil.writeFile(f, log);
                }
            }
        });
    }

    private static final int MAX_IMAGE_BUFFER_SIZE = 128 * 1024 * 1024;

    protected void onAppStart() {
        BaseHttpsManager.init(this);
        ImageBuffer.init(this, IMAGE_BUFFER_PATH, MAX_IMAGE_BUFFER_SIZE);
        ImageBuffer.getInstance().setMaxMemSize(8 * 1024 * 1024);
        new SendPadInfoThread(this).start();
        Log.i(TAG, "onAppStart");
    }

    private static class SendPadInfoThread extends Thread {
        private static final String TAG = "SendPadInfoThread";
        private Context mContext;
        private int mBatteryLevel = -1;
        private static final long SLEEP_INTERVAL = 60 * 1000;

        public SendPadInfoThread(Context context) {
            mContext = context;
            listenBatteryLevel();
        }

        private void listenBatteryLevel() {
            BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    mBatteryLevel = intent.getIntExtra("level", -1);
                }
            };
            IntentFilter batteryLevelFilter = new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED);
            mContext.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        }

        @Override
        public void run() {
            while (true) {
                IPadInfoService.Client client = null;
                try {
                    client = RPCHelper.getPadInfoService(mContext);
                    PadInfo info = new PadInfo();
                    info.setBatteryLevel(mBatteryLevel);
                    info.setIMEI(MenuUtils.getCustomIMEI(mContext));
                    String sid = ProfileHolder.getIns().getCurrentSid(mContext);
                    info.setSessionId(sid);
                    client.submitPadInfo(info);
                } catch (TException e) {
                    Log.w(TAG, "Send padinfo error");
                } finally {
                    ThriftUtils.releaseClient(client);
                }
                try {
                    Thread.sleep(SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                }
            }

        }
    }

}
