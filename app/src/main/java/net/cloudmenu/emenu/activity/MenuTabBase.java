package net.cloudmenu.emenu.activity;


import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.MenuUtils.GoodsCategory;
import net.cloudmenu.emenu.widget.SearchView;
import net.cloudmenu.emenu.widget.SearchView.OnGoodsClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;

public abstract class MenuTabBase extends MenuBase implements
        OnPagedViewChangeListener, OnCheckedChangeListener,
        OnGoodsClickListener {
    private static final String TAG = "MenuTabBase";
    protected List<GoodsCategory> mGoodsCategories;
    private RadioGroup rgSubmenu;

    private SearchView mSearchView;
    private Button btnSearch;

    @Override
    protected void initElements() {
        super.initElements();
        rgSubmenu = (RadioGroup) findViewById(R.id.rg_submenu);

        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.setOnGoodsClickListener(this);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btnSearch) {
            if (mSearchView.getVisibility() == View.VISIBLE)
                return;
            List<Goods> mGoods = new ArrayList<Goods>();
            for (MenuPage page : mMenu.getPages()) {
                mGoods.addAll(page.getGoodsList());
            }
            mSearchView.setGoodsList(mGoods);
            mSearchView.setVisibility(View.VISIBLE);
        }
    }

    protected void initContent() {
        initGoodsCategory();
        super.initContent();
    }

    private boolean mDisableCheckListener = false;

    public void checkSliently(int id) {
        mDisableCheckListener = true;
        rgSubmenu.check(id);
        mDisableCheckListener = false;
    }

    @Override
    public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
        super.onPageChanged(pagedView, previousPage, newPage);
        for (int i = 0; i < mGoodsCategories.size(); i++) {
            GoodsCategory cate = mGoodsCategories.get(i);
            if (newPage >= cate.getStart() && newPage < cate.getEnd()) {
                checkSliently(cate.getStart());
                break;
            }
        }
    }

    @Override
    public void onGoodsClick(Goods g) {
        if(usePagedView) {
            int page = -1;
            for (int i = 0; i < mAdapter.getCount(); i++) {
                MenuPage p = (MenuPage) mAdapter.getItem(i);
                if (p.getGoodsList().contains(g)) {
                    page = i;
                    break;
                }
            }
            if (page != -1) {
                mPagedView.scrollToPage(page);
            }
        } else {
            int position= this.getItemPosition(g);
            gridView.setSelection(position);
        }
    }

    protected  int getItemPosition(Goods goods) {
     throw new UnsupportedOperationException();
    }

    protected  int getFirstItemPositionInPage(int pageNumber) {
        throw new UnsupportedOperationException();
    }

    // Scroll to the first item that belongs to group. For example, appetizer, main dish, etc.
    // checkedId is the input page number. By default, each page contains 6 food items.
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(usePagedView){
            if (group == rgSubmenu && !mDisableCheckListener) {
                int startPage = checkedId;
                mPagedView.scrollToPage(startPage);
            }
        } else{
            if (group == rgSubmenu && !mDisableCheckListener) {
                gridView.setSelection(getFirstItemPositionInPage(checkedId));
            }
        }

    }

    private void initGoodsCategory() {
        mGoodsCategories = makeGoodsCategory();
        rgSubmenu.removeAllViews();
        for (GoodsCategory s : mGoodsCategories) {
            RadioButton rb = (RadioButton) getLayoutInflater().inflate(
                    R.layout.category_indicator, null);
            rb.setText(s.getCategory());
            rb.setId(s.getStart());
            rgSubmenu.addView(rb);
        }
        RadioButton rb = (RadioButton) getLayoutInflater().inflate(
                R.layout.category_indicator, null);
        rb.setEnabled(false);
        rgSubmenu.addView(rb);
        rb.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        rgSubmenu.setOnCheckedChangeListener(this);
        checkSliently(0);
    }
}
