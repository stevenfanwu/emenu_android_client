package net.cloudmenu.emenu.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cloudmenu.emenu.R;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.buding.common.util.SpellConverter.Spell;
import cn.com.cloudstone.menu.server.thrift.api.Goods;

public class SearchView extends RelativeLayout implements TextWatcher,
        OnItemClickListener, OnClickListener {
    private ListView mListview;
    private EditText etKeys;
    private View btnClose;
    private LayoutInflater mInflater;
    private List<Goods> mGoodsList;
    private List<Goods> mSearchList;
    private SearchAdapter mAdapter;
    private Map<String, Spell> mSpells = new HashMap<String, Spell>();

    private OnGoodsClickListener mOnGoodsClickListener;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.view_search, this);

        btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(this);
        etKeys = (EditText) findViewById(R.id.et_keys);
        mListview = (ListView) findViewById(android.R.id.list);
        etKeys.addTextChangedListener(this);
        mSearchList = new ArrayList<Goods>();
        mGoodsList = new ArrayList<Goods>();
        mAdapter = new SearchAdapter(mSearchList);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(this);
    }

    public void setGoodsList(List<Goods> list) {
        mGoodsList.clear();
        mGoodsList.addAll(list);
        onSearch();
    }

    public void setOnGoodsClickListener(OnGoodsClickListener l) {
        mOnGoodsClickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (v == btnClose) {
            setVisibility(View.GONE);
        }
    }

    private void onSearch() {
        String key = etKeys.getText().toString().toLowerCase();
        mSearchList.clear();
        for (Goods g : mGoodsList) {
            if (matchSearchKey(g, key)) {
                mSearchList.add(g);
            }
        }
        Collections.sort(mSearchList, new Comparator<Goods>() {
            @Override
            public int compare(Goods lhs, Goods rhs) {
                return lhs.getCategory().compareTo(rhs.getCategory());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    private boolean matchSearchKey(Goods good, String key) {
        String name = good.getName().toLowerCase();
        Spell nameSpell = mSpells.get(name);
        if (nameSpell == null) {
            // nameSpell = SpellConverter.getSpells(name);
            nameSpell = getSpell(name);
            mSpells.put(name, nameSpell);
        }
        if (name.indexOf(key) >= 0
                || nameSpell.getFullSpell().indexOf(key) >= 0
                || nameSpell.getFirstSpell().indexOf(key) >= 0) {
            return true;
        }
        return false;
    }

    private static HanyuPinyinOutputFormat mPinyinFormat;
    static {
        mPinyinFormat = new HanyuPinyinOutputFormat();
        mPinyinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        mPinyinFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        mPinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    private Spell getSpell(String name) {
        char[] chars = name.toCharArray();
        StringBuilder fullSpell = new StringBuilder();
        StringBuilder firstSpell = new StringBuilder();
        try {
            for (char c : chars) {
                if (Character.isDigit(c) || Character.isLowerCase(c)) {
                    fullSpell.append(c);
                    firstSpell.append(c);
                } else {
                    String[] res = PinyinHelper.toHanyuPinyinStringArray(c,
                            mPinyinFormat);
                    if (res != null && res.length > 0 && res[0].length() > 0) {
                        fullSpell.append(res[0]);
                        firstSpell.append(res[0].charAt(0));
                    } else {
                        fullSpell.append(c);
                        firstSpell.append(c);
                    }
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
        }
        return new Spell(fullSpell.toString(), firstSpell.toString());
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        setVisibility(View.GONE);
        if (mOnGoodsClickListener != null) {
            mOnGoodsClickListener.onGoodsClick(mSearchList.get(arg2));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        onSearch();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public interface OnGoodsClickListener {
        public void onGoodsClick(Goods g);
    }

    private class SearchAdapter extends BaseAdapter {
        private List<Goods> mList;

        public SearchAdapter(List<Goods> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater
                        .inflate(R.layout.list_item_search, null);
            }
            Goods good = mList.get(position);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tvPrice = (TextView) convertView
                    .findViewById(R.id.tv_price);
//            TextView tvCategory = (TextView) convertView
//                    .findViewById(R.id.tv_category);
//            View header = convertView.findViewById(R.id.header);
            tvPrice.setText("$" + good.getPrice());
            tvName.setText(good.getName());
//            tvCategory.setText(good.getCategory());
//            if (isCategoryBegeiner(position)) {
//                tvCategory.setVisibility(View.VISIBLE);
//                header.setVisibility(View.VISIBLE);
//            } else {
//                tvCategory.setVisibility(View.GONE);
//                header.setVisibility(View.GONE);
//            }
            return convertView;
        }

//        private boolean isCategoryBegeiner(int position) {
//            if (position == 0)
//                return true;
//            if (position < mList.size() && position > 0) {
//                String c1 = mList.get(position).getCategory();
//                String c2 = mList.get(position - 1).getCategory();
//                return !c1.equals(c2);
//            }
//            return false;
//        }
    }

}
