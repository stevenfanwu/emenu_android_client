package net.cloudmenu.emenu.activity;

import android.content.Intent;
import android.view.View;

import net.cloudmenu.emenu.R;

import cn.buding.common.activity.BaseTabHost;

public class MenuTabHost extends BaseTabHost {
    @Override
    protected int getLayout() {
        return R.layout.tab_host_menu;
    }

    @Override
    protected void initTabParams() {
        mTabCount = 2;
        mTabIntents = new Intent[mTabCount];
        mTabIntents[0] = new Intent(this, MenuGallery.class);
        mTabIntents[1] = new Intent(this, MenuList.class);

        mTabIcons = new int[mTabCount];
        mTabIcons[0] = R.drawable.ic_grid;
        mTabIcons[1] = R.drawable.ic_list;

        mTabIndicator = new String[mTabCount];
        mTabIndicator[0] = "图例";
        mTabIndicator[1] = "列表";
    }

    @Override
    protected View getTabIndicatorView(int i) {
        return getLayoutInflater().inflate(R.layout.tab_indicator_menu, null);
    }

}
