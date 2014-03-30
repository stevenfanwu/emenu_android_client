package net.cloudmenu.emenu.adapter;


import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.activity.MenuTabBase;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.utils.MenuUtils;
import net.cloudmenu.emenu.widget.MenuGoodsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;

public class MenuGridAdapter implements ListAdapter {
    private Map<Integer, List<? extends MenuPage>> mPages;
    private int[] mTypes = new int[] { GlobalValue.TYPE_CURRENT };
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    // The type of item: menu or order
    private Map<Integer, List< ? extends Goods>> itemByType = new HashMap<Integer, List<? extends Goods>>();
    private Map<Goods, Integer> itemPositionMap = new HashMap<Goods, Integer>();
    private Map<Integer, Integer> itemPositionToCategoryMap = new HashMap<Integer, Integer>();
    private MenuTabBase mContext;

    public MenuGridAdapter(MenuTabBase c, Map<Integer, List<? extends MenuPage>> pages,
                           List<MenuUtils.GoodsCategory> itemCategories) {
        mContext = c;
        mPages = pages;

        // Map good items to type
        for (Map.Entry<Integer, List< ? extends MenuPage>> entry : mPages.entrySet()) {
            List<Goods> goodsList= new ArrayList<Goods>();
            for(MenuPage menuPage : entry.getValue()){
                goodsList.addAll(menuPage.getGoodsList());
            }
            itemByType.put(entry.getKey(), goodsList);
        }

        // Map items to positions.
        // Map item positions to category. Note that the category ID is the same as the page number
        // that this category first starts.
        int itemPosition = 0;
        int pageNumber = 0;
        Set<Integer> itemCategoryType = new HashSet<Integer>();
        for(MenuUtils.GoodsCategory category : itemCategories){
            itemCategoryType.add(category.getStart());
        }
        for (Map.Entry<Integer, List< ? extends MenuPage>> entry : mPages.entrySet()) {
            for(MenuPage menuPage : entry.getValue()){
                boolean isFirstItemInPage = true;
                for(Goods item : menuPage.getGoodsList()){
                    itemPositionMap.put(item, itemPosition);
                    if(isFirstItemInPage && itemCategoryType.contains(pageNumber)){
                        itemPositionToCategoryMap.put(itemPosition, pageNumber);
                        isFirstItemInPage = false;
                    }
                    itemPosition++;
                }
                pageNumber++;
            }
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
        List<? extends Goods> goodsList = itemByType.get(type);
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
                return itemByType.get(i).get(position);
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

    public int getItemPosition(Goods goods){
        return itemPositionMap.get(goods);
    }

    public int getFirstItemPositionInPage(int pageNumber) {
        int itemPosition = 0;
        int currentPageNumber = 0;
        for (Map.Entry<Integer, List< ? extends MenuPage>> entry : mPages.entrySet()) {
            for(MenuPage menuPage : entry.getValue()){
                if(currentPageNumber == pageNumber){
                    return itemPosition;
                }
                currentPageNumber++;
                itemPosition += menuPage.getGoodsList().size();
            }
        }
        return itemPosition;
    }

    // Return MenuGoodsView instead of menuPageview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuGoodsView view = (MenuGoodsView) convertView;
        Goods item = getItem(position);
        if (convertView == null) {

            if (item == null) {
                return new View(mContext);
            }

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            view = (MenuGoodsView) inflater.inflate(R.layout.page_horizontal1, null);

        }
        view.setGoods(getType(position), item);
        view.setVisibility(View.VISIBLE);
            // Change the radioGroup submenu while scrolling to the first item of a category
            if (itemPositionToCategoryMap.get(position - 1) != null) {
                mContext.checkSliently(itemPositionToCategoryMap.get(position - 1));
            }


            return view;
    }
}