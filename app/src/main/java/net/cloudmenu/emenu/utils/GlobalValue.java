package net.cloudmenu.emenu.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.GoodsOrder;
import cn.com.cloudstone.menu.server.thrift.api.Menu;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;
import cn.com.cloudstone.menu.server.thrift.api.Order;

public class GlobalValue {
    private static GlobalValue mInstance;

    public static GlobalValue getIns() {
        if (mInstance == null)
            mInstance = new GlobalValue();
        return mInstance;
    }

    public static final int TYPE_CURRENT = -1;
    private static final int TYPE_ORDERED = 1;

    public static boolean isTypeOrdered(int type) {
        return type >= TYPE_ORDERED;
    }

    public static int getOrderType(int index) {
        if (index < 0)
            throw new RuntimeException("index must > 0");
        return TYPE_ORDERED + index;
    }

    private Map<Integer, GoodsOrderMap> mGoodsOrders = new HashMap<Integer, GoodsOrderMap>();
    private List<Order> mOrder;

    private Menu mMenu;

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu m) {
        mMenu = m;
    }

    public void setOrder(List<Order> order) {
        mOrder = order;
    }

    public List<Order> getOrder() {
        return mOrder;
    }

    public GoodsOrderMap getGoodsItemMap(int type) {
        GoodsOrderMap map = mGoodsOrders.get(type);
        if (map == null) {
            map = new GoodsOrderMap();
            mGoodsOrders.put(type, map);
        }
        return map;
    }

    public GoodsOrder getGoodsOrder(int type, int goodId) {
        GoodsOrderMap map = getGoodsItemMap(type);
        Goods goods = getGoods(goodId);
        if(goods == null)
            return null;
        return map.getGoodsItem(goods);
    }
    
    public void putGoodsOrder(int type, GoodsOrder order){
        if(order == null)
            return;
        getGoodsItemMap(type).put(order.getId(), order);
    }
    
    public Goods getGoods(int id) {
        if (mMenu == null)
            return null;
        for (MenuPage page : mMenu.getPages()) {
            for (Goods good : page.getGoodsList()) {
                if (good.getId() == id)
                    return good;
            }
        }
        return null;
    }

    public void onClearTable(Context context) {
        mGoodsOrders.clear();
        mOrder = null;
        ProfileHolder.getIns().removeCurrentTable(context);
    }

    public class GoodsOrderMap extends HashMap<Integer, GoodsOrder> {
        public GoodsOrder getGoodsItem(Goods good) {
            GoodsOrder item = get(good.getId());
            if (item == null) {
                item = makeGoodsOrder(good);
                put(good.getId(), item);
            }
            return item;
        }

        private GoodsOrder makeGoodsOrder(Goods g) {
            GoodsOrder order = new GoodsOrder();
            order.id = g.getId();
            order.number = 0;
            order.price = g.price;
            order.category = g.getCategory();
            return order;
        }
    }
}
