package net.cloudmenu.emenu.activity;

import android.os.Bundle;
import android.widget.ListAdapter;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.adapter.MenuGridAdapter;
import net.cloudmenu.emenu.utils.GlobalValue;

import cn.com.cloudstone.menu.server.thrift.api.Goods;
import greendroid.widget.PagedAdapter;

public class MenuGallery extends MenuTabBase  {
    private static final String TAG = "MenuActivity";

    public MenuGallery(){
        this.viewType = ViewType.GRIDVIEW;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_menu_gallery;
    }

    @Override
    protected PagedAdapter makeAdapter() {
        MenuPageMap map = new MenuPageMap();
        map.put(GlobalValue.TYPE_CURRENT, mMenu.getPages());
        return new MenuPageApdapter(map);
    }

    @Override
    protected ListAdapter newGridAdapter(){
        MenuPageMap map = new MenuPageMap();
        map.put(GlobalValue.TYPE_CURRENT, mMenu.getPages());
        return new MenuGridAdapter(this, map, mGoodsCategories);
    }

    @Override
    protected int getItemPosition(Goods goods) {
        return ((MenuGridAdapter) this.gridAdapter).getItemPosition(goods);
    }

    @Override
    protected int getFirstItemPositionInPage(int pageNumber) {
        return ((MenuGridAdapter) this.gridAdapter).getFirstItemPositionInPage(pageNumber);
    }

}
