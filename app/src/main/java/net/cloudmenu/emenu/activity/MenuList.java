package net.cloudmenu.emenu.activity;

import android.os.Bundle;
import android.widget.ListAdapter;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.adapter.MenuListAdapter;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.utils.MenuUtils;
import net.cloudmenu.emenu.utils.MenuUtils.GoodsCategory;
import net.cloudmenu.emenu.widget.MenuPageView.LayoutType;

import java.util.ArrayList;
import java.util.List;

import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;
import greendroid.widget.PagedAdapter;

public class MenuList extends MenuTabBase {
    private List<MenuListPage> mMenuPages;

    public MenuList(){
        this.viewType = ViewType.LISTVIEW;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected PagedAdapter makeAdapter() {
        MenuPageMap map = new MenuPageMap();
        map.put(GlobalValue.TYPE_CURRENT, makePages());
        return new MenuPageApdapter(map);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_menu_list;
    }

    @Override
    protected ListAdapter newListAdapter(){
        MenuPageMap map = new MenuPageMap();
        map.put(GlobalValue.TYPE_CURRENT, mMenu.getPages());
        return new MenuListAdapter(this, map, mGoodsCategories);
    }

    @Override
    protected int getItemPosition(Goods goods) {
        return ((MenuListAdapter) this.listAdapter).getItemPosition(goods);
    }

    @Override
    protected int getFirstItemPositionInPage(int pageNumber) {
        return ((MenuListAdapter) this.listAdapter).getFirstItemPositionInPage(pageNumber);
    }


   private List<MenuListPage> makePages() {
        List<MenuListPage> pages = new ArrayList<MenuListPage>();
        MenuListPage page = null;
        List<GoodsCategory> categories = MenuUtils
                .getMenuGoodsCategories(mMenu);
        for (GoodsCategory cate : categories) {
            page = new MenuListPage(cate.getCategory());

            pages.add(page);
            for (int i = cate.getStart(); i < cate.getEnd(); i++) {
                for (Goods good : mMenu.getPages().get(i).getGoodsList()) {
                    if (!page.addGoods(good)) {
                        page = new MenuListPage(cate.getCategory());
                        pages.add(page);
                        page.addGoods(good);
                    }
                }
            }
        }
        return pages;
    }

    @Override
    protected List<GoodsCategory> makeGoodsCategory() {
        mMenuPages = new ArrayList<MenuListPage>();
        MenuListPage page = null;
        List<GoodsCategory> categories = MenuUtils
                .getMenuGoodsCategories(mMenu);
        List<GoodsCategory> newCates = new ArrayList<GoodsCategory>();
        GoodsCategory curCate = null;
        for (GoodsCategory cate : categories) {
            if (curCate != null)
                curCate.setEnd(mMenuPages.size());
            curCate = new GoodsCategory(cate.getCategory());
            newCates.add(curCate);
            curCate.setStart(mMenuPages.size());
            page = new MenuListPage(cate.getCategory());
            mMenuPages.add(page);
            for (int i = cate.getStart(); i < cate.getEnd(); i++) {
                for (Goods good : mMenu.getPages().get(i).getGoodsList()) {
                    if (!page.addGoods(good)) {
                        page = new MenuListPage(cate.getCategory());
                        mMenuPages.add(page);
                        page.addGoods(good);
                    }
                }
            }
        }
        if (curCate != null)
            curCate.setEnd(mMenuPages.size());
        return newCates;
    }

    @Override
    protected LayoutType getMenuPageLayoutType() {
        return LayoutType.List16;
    }

    class MenuListPage extends MenuPage {
        public static final int PAGE_COUNT = 16;
        private String mCategory;

        public MenuListPage(String cate) {
            goodsList = new ArrayList<Goods>();
            mCategory = cate;
        }

        public boolean addGoods(Goods good) {
            if (goodsList.size() == PAGE_COUNT)
                return false;
            else {
                goodsList.add(good);
                return true;
            }

        }

        public String getCategory() {
            return mCategory;
        }
    }
}
