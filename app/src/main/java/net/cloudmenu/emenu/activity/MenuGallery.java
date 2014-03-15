package net.cloudmenu.emenu.activity;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.widget.MenuGoodsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;
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

    public class MenuListAdapter implements ListAdapter {
        private Map<Integer, List<? extends MenuPage>> mPages;
        private int[] mTypes = new int[] { GlobalValue.TYPE_CURRENT };
        private final DataSetObservable mDataSetObservable = new DataSetObservable();
        private Map<Integer, List< ? extends Goods>> itemsByCategory = new HashMap<Integer, List<? extends Goods>>();
        private Context mContext;

        public MenuListAdapter(Context c, Map<Integer, List<? extends MenuPage>> pages) {
            mContext = c;
            mPages = pages;
            for (Map.Entry<Integer, List< ? extends MenuPage>> entry : mPages.entrySet()) {
                List<Goods> goodsList= new ArrayList<Goods>();
                for(MenuPage menuPage : entry.getValue()){
                    goodsList.addAll(menuPage.getGoodsList());
                }
                itemsByCategory.put(entry.getKey(), goodsList);
            }

        }

        @Override
        public boolean hasStableIds(){
            return true;
        }

        @Override
        public boolean isEmpty(){
            return mPages.isEmpty();
        }

        @Override
        public final int getItemViewType(int position) {
            return 0;
        }

        @Override
        public final int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEnabled(int position){
            return true;
        }

        @Override
        public boolean areAllItemsEnabled (){
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.registerObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
        }

        private void notifyDataSetChanged() {
            mDataSetObservable.notifyChanged();
        }

        public void setTypes(int[] types) {
            mTypes = types;
            notifyDataSetChanged();
        }

        @Override
        // return element size instead of page size
        public int getCount() {
            int count = 0;
            for (int i : mTypes) {
                count += getCount(i);
            }
            return count;
        }

        private int getCount(int type) {
            List<? extends Goods> goodsList = itemsByCategory.get(type);
            if (goodsList == null)
                return 0;
            return goodsList.size();

        }

        @Override
        // return Goods instead of MenuPage
        public Goods getItem(int position) {
            for (int i : mTypes) {
                int count = getCount(i);
                if (position < count) {
                    return itemsByCategory.get(i).get(position);
                } else {
                    position -= count;
                }
            }
            return null;
        }

        // Return the type of item given the item position
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
        // To-Do: return MenuGoodsView instead of menuPageview
        public View getView(int position, View convertView, ViewGroup parent) {
            Goods item = getItem(position);
            if (item == null) {
                return new View(MenuGallery.this);
            }

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            MenuGoodsView view = (MenuGoodsView)inflater.inflate(R.layout.page_horizontal1, null);
            view.setGoods(getType(position), item);
            view.setVisibility(View.VISIBLE);
            return view;
        }
    }


}
