package net.cloudmenu.emenu.utils;

import java.util.ArrayList;
import java.util.List;

import net.cloudmenu.emenu.net.RPCHelper;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.content.Context;
import cn.buding.common.util.PackageUtils;
import cn.com.cloudstone.menu.server.thrift.api.GoodState;
import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.Menu;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;

public class MenuUtils {
    private static final String[] STATE_STRS = new String[] { "已点", "上过", "外带",
            "等叫", "已退" };

    public static boolean stateEditable(GoodState state) {
        if (state == null)
            return false;
        switch (state) {
        case Canceled:
        case Servered:
        case Ordered:
            return false;
        case Takeout:
        case Waiting:
            return true;
        }
        return false;
    }

    public static String getStateStr(GoodState state) {
        if (state != null) {
            return STATE_STRS[state.getValue()];
        }
        return "暂无";
    }

    public static GoodState getState(String s) {
        for (int i = 0; i < STATE_STRS.length; i++) {
            if (STATE_STRS[i].equals(s))
                return GoodState.findByValue(i);
        }
        return null;
    }

    public static class GoodsCategory {
        private String category;
        private int start;
        private int end;

        public GoodsCategory(String c) {
            category = c;
        }

        public void setStart(int s) {
            start = s;
        }

        public void setEnd(int e) {
            end = e;
        }

        public String getCategory() {
            return category;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public int count() {
            return end - start;
        }
    }

    public static List<GoodsCategory> getMenuGoodsCategories(Menu menu) {
        if (menu == null)
            return null;
        List<GoodsCategory> set = new ArrayList<GoodsCategory>();
        GoodsCategory lastCategory = null;
        for (int i = 0, menuLen = menu.getPagesSize(); i < menuLen; i++) {
            MenuPage page = menu.getPages().get(i);
            if (page.getGoodsListSize() == 0)
                continue;
            Goods goods = page.getGoodsList().get(0);
            if (lastCategory == null
                    || !lastCategory.getCategory().equals(goods.getCategory())) {
                if (lastCategory != null) {
                    lastCategory.end = i;
                }
                lastCategory = new GoodsCategory(goods.getCategory());
                lastCategory.start = i;
                set.add(lastCategory);
            }
        }
        if (lastCategory != null)
            lastCategory.end = menu.getPagesSize();
        return set;
    }

    public static String getImgUrl(Context context, String url) {
        if (url == null)
            return null;
        return RPCHelper.getHostIp(context) + url;
    }

    public static String getCustomIMEI(Context context) {
        String imei = PackageUtils.getIMEI(context);
        String mac = PackageUtils.getMacAddress(context);
        String id = (imei != null ? imei : "") + (mac != null ? mac : "");
        if (id.length() == 0) {
            id = PackageUtils.getCustomIMEI(context);
        }
        return md5Hex(id);
    }

    public static String md5Hex(String input) {
        return new String(Hex.encodeHex(DigestUtils.md5(input)));
    }
}
