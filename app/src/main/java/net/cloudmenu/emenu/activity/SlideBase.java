package net.cloudmenu.emenu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.widget.MenuPageView;
import net.cloudmenu.emenu.widget.MenuPageView.LayoutType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.cloudstone.menu.server.thrift.api.MenuPage;
import greendroid.widget.PagedAdapter;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;

public abstract class SlideBase extends Activity implements
        OnPagedViewChangeListener, OnClickListener {
    private static final String TAG = "SlideBase";

    protected PagedView mPagedView;
    protected PagedAdapter mAdapter;

    protected GridView gridView;
    protected ListAdapter gridAdapter;

    protected ListView listView;
    protected ListAdapter listAdapter;

    protected enum ViewType {
        PAGEVIEW,
        GRIDVIEW,
        LISTVIEW
    }
    protected ViewType viewType = ViewType.PAGEVIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initElements();
        IntentFilter filter = new IntentFilter(MainTabHost.ACTION_BUTTON);
    }

    @Override
    protected void onResume() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onClick(View v) {

    }


    protected void initContent() {
        switch (viewType) {
            case PAGEVIEW:
                mAdapter = makeAdapter();
                mPagedView.setAdapter(mAdapter);
                break;
            case GRIDVIEW:
                gridAdapter = newGridAdapter();
                gridView.setAdapter(gridAdapter);
                break;
            case LISTVIEW:
                listAdapter = newListAdapter();
                listView.setAdapter(listAdapter);
                break;
            default:
                throw new IllegalStateException("Unknown View Type");
        }
    }

    protected abstract PagedAdapter makeAdapter();

    protected ListAdapter newGridAdapter() {
        return null;
    }
    protected ListAdapter newListAdapter() {
        return null;
    }

    protected abstract int getLayout();

    protected void initElements() {
        switch (viewType) {
            case PAGEVIEW:
                mPagedView = (PagedView) findViewById(R.id.paged_view);
                mPagedView.setOnPageChangeListener(this);
                break;
            case GRIDVIEW:
                gridView = (GridView) findViewById(R.id.grid_view);
                break;
            case LISTVIEW:
                listView = (ListView) findViewById(R.id.list_view);
                break;
            default:
                throw new IllegalStateException("Unknown View Type");
        }
    }

    @Override
    public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
    }

    @Override
    public void onStartTracking(PagedView pagedView) {
    }

    @Override
    public void onStopTracking(PagedView pagedView) {
    }

    public class MenuPageMap extends HashMap<Integer, List<? extends MenuPage>> {

    }

    public class MenuPageApdapter extends PagedAdapter {
        private Map<Integer, List<? extends MenuPage>> mPages;
        private PageHolder mHolder;
        private int[] mTypes = new int[] { GlobalValue.TYPE_CURRENT };

        public MenuPageApdapter(Map<Integer, List<? extends MenuPage>> pages) {
            mPages = pages;
            mHolder = new PageHolder();
        }

        public void setTypes(int[] types) {
            mTypes = types;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            int count = 0;
            for (int i : mTypes) {
                count += getCount(i);
            }
            return count;
        }

        private int getCount(int type) {
            List<? extends MenuPage> pages = mPages.get(type);
            if (pages == null)
                return 0;
            return pages.size();

        }

        @Override
        public MenuPage getItem(int position) {
            for (int i : mTypes) {
                int count = getCount(i);
                if (position < count) {
                    return mPages.get(i).get(position);
                } else {
                    position -= count;
                }
            }
            return null;
        }

        public int getType(int position) {
            for (int i : mTypes) {
                int count = getCount(i);
                if (position < count) {
                    return i;
                } else {
                    position -= count;
                }
            }
            return -1;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuPage page = getItem(position);
            if (page == null) {
                return new View(SlideBase.this);
            }
            int type = getType(position);
            LayoutType lType = getMenuPageLayoutType();
            if (lType == null)
                lType = LayoutType.getLayoutType(page.getLayoutType());

            MenuPageView view = mHolder.getView(parent.getContext(), lType);
            view.setPage(type, page);
            onMenuPageCreate(view);
            return view;
        }

        class PageHolder {
            private Map<LayoutType, ViewHolder> map;

            public PageHolder() {
                map = new HashMap<LayoutType, ViewHolder>();
            }

            public MenuPageView getView(Context context, LayoutType lType) {
                ViewHolder holder = map.get(lType);
                if (holder == null) {
                    holder = new ViewHolder(lType);
                    map.put(lType, holder);
                }
                return holder.getView(context);
            }

            class ViewHolder {
                LayoutType lType;
                List<MenuPageView> views;

                public ViewHolder(LayoutType type) {
                    lType = type;
                    views = new ArrayList<MenuPageView>();
                }

                public MenuPageView getView(Context ctx) {
                    MenuPageView res = null;
                    for (MenuPageView v : views) {
                        if (v.getParent() == null) {
                            res = v;
                            break;
                        }
                    }
                    if (res == null) {
                        res = new MenuPageView(SlideBase.this, lType);
                        views.add(res);
                        Log.i(TAG, "new view " + lType.name());
                    }
                    return res;
                }
            }
        }
    }

    protected void onMenuPageCreate(MenuPageView view) {

    }

    protected LayoutType getMenuPageLayoutType() {
        return null;
    }

    public void onBackPressed() {
    };
}