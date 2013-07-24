package net.cloudmenu.emenu.activity;

import greendroid.widget.PagedAdapter;

import java.util.ArrayList;
import java.util.List;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.widget.SearchView;
import net.cloudmenu.emenu.widget.SearchView.OnGoodsClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;

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
