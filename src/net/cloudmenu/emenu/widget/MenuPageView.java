package net.cloudmenu.emenu.widget;

import java.util.ArrayList;
import java.util.List;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.ViewUtils;
import net.cloudmenu.emenu.widget.MenuGoodsView.Callback;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;
import cn.com.cloudstone.menu.server.thrift.api.PageLayoutType;

public class MenuPageView extends LinearLayout implements OnClickListener {
    private static final String TAG = "MenuPageView";
    protected MenuPage mPage;
    protected int mMenuType ;
    protected LayoutType mLayoutType;
    protected List<MenuGoodsView> mGoodsViews;

    public MenuPageView(Context context, LayoutType type) {
        super(context);

        mLayoutType = type;
        int layoutId = mLayoutType.getLayoutRes();
        LayoutInflater inflater = (LayoutInflater) (context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        inflater.inflate(layoutId, this);
        initElements();

    }

    public void setPage(int type, MenuPage page) {
        mMenuType = type;
        mPage = page;
        initContent();
    }
    
    public MenuPage getPage(){
        return mPage;
    }

    private void initElements() {
        mGoodsViews = getGoodsViews();
    }

    private void initContent() {
        int viewCount = mGoodsViews.size();
        int goodsCount = mPage.getGoodsListSize();
        int count = Math.min(viewCount, goodsCount);
        for (int i = 0; i < count; i++) {
            MenuGoodsView view = mGoodsViews.get(i);
            view.setVisibility(View.VISIBLE);
            Goods goods = mPage.getGoodsList().get(i);
            view.setGoods(mMenuType, goods);
        }
        for (int i = count; i < viewCount; i++)
            mGoodsViews.get(i).setVisibility(View.INVISIBLE);
    }

    private List<MenuGoodsView> getGoodsViews() {
        ArrayList<View> res = new ArrayList<View>();
        ViewUtils.findViews(res, this, MenuGoodsView.class);
        ArrayList<MenuGoodsView> menuRes = new ArrayList<MenuGoodsView>();
        for (View v : res)
            menuRes.add((MenuGoodsView) v);
        return menuRes;
    }

    @Override
    public void onClick(View v) {
    }

    public void setCallback(Callback l) {
        for (MenuGoodsView view : mGoodsViews) {
            view.setCallback(l);
        }
    }

    public enum LayoutType {
        Horizontal1, Horizontal2, Horizontal3, Triangle4, Grid6, List16, Order8;
        public int getLayoutRes() {
            switch (this) {
            case Horizontal1:
                return R.layout.page_horizontal1;
            case Horizontal2:
                return R.layout.page_horizontal2;
            case Horizontal3:
                return R.layout.page_horizontal3;
            case Grid6:
                return R.layout.page_grid6;
            case Triangle4:
                return R.layout.page_grid6_vertical;
            case List16:
                return R.layout.page_list16;
            case Order8:
                return R.layout.page_order8;
            default:
                return 0;
            }
        }

        public static LayoutType getLayoutType(PageLayoutType type) {
            switch (type) {
            case Horizontal1:
                return Horizontal1;
            case Horizontal2:
                return Horizontal2;
            case Horizontal3:
                return Horizontal3;
            case Grid6:
                return Grid6;
            case Triangle4:
                return Triangle4;
            default:
                Log.w(TAG, type + " not exists");
                return null;
            }
        }

    }

}
