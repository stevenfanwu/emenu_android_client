package net.cloudmenu.emenu.activity;

import android.os.Bundle;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.GlobalValue;

import greendroid.widget.PagedAdapter;

public class MenuGallery extends MenuTabBase  {
    private static final String TAG = "MenuActivity";

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

}
