package net.cloudmenu.emenu.utils;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewUtils {

    public static void findViews(List<View> res, View holder,
            Class<? extends View> klass) {
        if (klass.isInstance(holder))
            res.add(holder);
        if (holder instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) holder;
            for (int i = 0; i < group.getChildCount(); i++) {
                View v = group.getChildAt(i);
                findViews(res, v, klass);
            }
        }
    }
}
