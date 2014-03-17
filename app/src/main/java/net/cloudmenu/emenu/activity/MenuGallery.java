package net.cloudmenu.emenu.activity;

import android.os.Bundle;
import android.widget.ListAdapter;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.adapter.MenuListAdapter;
import net.cloudmenu.emenu.utils.GlobalValue;

import cn.com.cloudstone.menu.server.thrift.api.Goods;
import greendroid.widget.PagedAdapter;

public class MenuGallery extends MenuTabBase  {
    private static final String TAG = "MenuActivity";

    public MenuGallery(){
        this.usePagedView = false;
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
    protected ListAdapter newListAdapter(){
        MenuPageMap map = new MenuPageMap();
        map.put(GlobalValue.TYPE_CURRENT, mMenu.getPages());
        return new MenuListAdapter(this, map);
    }

    @Override
    protected int getItemPosition(Goods goods) {
        return ((MenuListAdapter) this.listAdapter).getItemPosition(goods);
    }

    @Override
    protected int getFirstItemPositionInPage(int pageNumber) {
        return ((MenuListAdapter) this.listAdapter).getFirstItemPositionInPage(pageNumber);
    }

}
