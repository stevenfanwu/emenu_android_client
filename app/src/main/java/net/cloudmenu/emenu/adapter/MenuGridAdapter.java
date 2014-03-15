package net.cloudmenu.emenu.adapter;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.widget.MenuGoodsView;

import java.util.List;
import java.util.Map;

import cn.com.cloudstone.menu.server.thrift.api.MenuPage;

/**
 * Created by Macbook on 3/12/14.
 */
public class MenuGridAdapter implements ListAdapter {
    private Map<Integer, List<? extends MenuPage>> mPages;
    private int[] mTypes = new int[] { GlobalValue.TYPE_CURRENT };
    private Context mContext;
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public MenuGridAdapter(Context c, Map<Integer, List<? extends MenuPage>> pages) {
        mContext = c;
        mPages = pages;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEnabled (int position) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }


    public void setTypes(int[] types) {
        mTypes = types;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
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
        return 3;
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
        MenuGoodsView menuGoodsView;
        menuGoodsView = new MenuGoodsView(mContext, null);

        return menuGoodsView;
    }

}
