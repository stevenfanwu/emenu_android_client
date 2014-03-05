package net.cloudmenu.emenu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.cloudmenu.emenu.R;

public class GlobalConfig {

    public static boolean isWorkWithoutNetWork(Context context) {
        SharedPreferences preference = PreferenceManager
                .getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pre_key_work_without_network);
        return preference.getBoolean(key, false);
    }
}
