package net.cloudmenu.emenu.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.activity.PictureGallery;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.utils.MenuUtils;

import java.util.ArrayList;

import cn.buding.common.widget.AsyncImageView;
import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.GoodsOrder;
import cn.com.cloudstone.menu.server.thrift.api.Img;

public class MenuGoodsView extends RelativeLayout implements OnClickListener {
    protected int mMenuType;

    protected AsyncImageView aivImage;
    protected TextView tvName;
    protected TextView tvDesc;
    protected TextView tvPrice;
    protected TextView tvCount; //菜品数量
    protected Button btnAdd;
    protected Button btnSub;

    private View mSoldout;
    private StarRatingBar mPepperBar;

    protected Goods mGoods;
    protected GoodsOrder mOrder;

    public MenuGoodsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MenuGoodsView, 0, 0);
        int layoutId = a.getResourceId(R.styleable.MenuGoodsView_layout, 0);
        if (layoutId == 0)
            throw new IllegalArgumentException("Must specifiy a layout");
        LayoutInflater inflater = (LayoutInflater) (context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        inflater.inflate(layoutId, this);
        a.recycle();

        initElements();
    }

    public void setGoods(int type, Goods item) {
        mMenuType = type;
        mGoods = item;
        mOrder = GlobalValue.getIns().getGoodsOrder(mMenuType, item.getId());
        initContent();
    }

    protected void initElements() {
        aivImage = (AsyncImageView) findViewById(R.id.image);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        tvCount = (TextView) findViewById(R.id.tv_count);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnSub = (Button) findViewById(R.id.btn_sub);
        mPepperBar = (StarRatingBar) findViewById(R.id.pepper_bar);
        mSoldout = findViewById(R.id.soldout);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        aivImage.setOnClickListener(this);
        tvCount.setOnClickListener(this);

    }

    private static final String TAG_LARGE_IMG = "largeImg";

    protected void initContent() {
        if (tvName != null)
            tvName.setText(mGoods.getName());
        if (tvDesc != null)
            tvDesc.setText(mGoods.getIntroduction());
        if (tvPrice != null) {
            if (tvPrice.getBackground() != null)
                if (mGoods.isOnSales()) {
                    tvPrice.getBackground().setLevel(1);
                } else {
                    tvPrice.getBackground().setLevel(0);
                }
            tvPrice.setText("￥" + mGoods.getPrice());
        }
        String url = null;
        if (mGoods.getImgsSize() > 0) {
            url = mGoods.getImgs().get(0).getPreviewImgUrl();
            if (TAG_LARGE_IMG.equals(getTag())) {
                url = mGoods.getImgs().get(0).getImgUrl();
            }
            url = MenuUtils.getImgUrl(getContext(), url);
        }
        if (aivImage != null)
            aivImage.postLoading(url);
        onGoodsCountChanged();

        if (GlobalValue.isTypeOrdered(mMenuType)) {
            // setEnabled(false);
            btnAdd.setVisibility(View.INVISIBLE);
            btnSub.setVisibility(View.INVISIBLE);
        } else {
            // setEnabled(true);
            btnAdd.setVisibility(View.VISIBLE);
            btnSub.setVisibility(View.VISIBLE);
        }
        if (mPepperBar != null) {
            mPepperBar.setProgress(mGoods.getSpicy());
        }
        if (mSoldout != null) {
            mSoldout.setVisibility(mGoods.isSoldout() ? View.VISIBLE
                    : View.GONE);
        }
    }

    protected void onGoodsCountChanged() {
        double number = mOrder.getNumber();
        if (Math.abs(number - (int) number) < 1e-6) {
            tvCount.setText(String.format("%.0f", number));
        } else {
            tvCount.setText(String.format("%.1f", number));
        }
        if (mCallback != null)
            mCallback.onGoodsCountChanged(this, number);
    }

    @Override
    public void onClick(View v) {
        if (mGoods.isSoldout())
            return;
        if (v == btnAdd) {
            mOrder.number++;
            onGoodsCountChanged();
        } else if (v == btnSub) {
            mOrder.number--;
            if (mOrder.number < 0)
                mOrder.number = 0;
            onGoodsCountChanged();
        } else if (v == aivImage) {
            Intent intent = new Intent(getContext(), PictureGallery.class);
            ArrayList<String> urls = new ArrayList<String>();
            for (Img img : mGoods.getImgs()) {
                urls.add(img.getImgUrl());
            }
            intent.putExtra(PictureGallery.EXTRA_URLS, urls);
            getContext().startActivity(intent);
        } else if (v == tvCount) {
            if (mGoods.isNumberDecimalPermited()) {
                showCountDialog();
            }
        }
    }

    private Context getParentContenxt(Context context) {
        while (context instanceof Activity
                && ((Activity) context).getParent() != null) {
            context = ((Activity) context).getParent();
        }
        return context;
    }
    
    /**
     * 输入菜品数量对话框
     */
    private void showCountDialog() {
        Context context = getParentContenxt(getContext());
        final EditText etCount = new EditText(context);
        etCount.setInputType(EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        etCount.setText("" + mOrder.number);
        etCount.setSelectAllOnFocus(true); //默认全选
        etCount.setLines(1);
        new AlertDialog.Builder(context).setTitle("选择菜品数量")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String countStr = etCount.getText().toString();
                        double count = 0;
                        try {
                            count = Double.parseDouble(countStr);
                        } catch (Exception e) {
                        }
                        mOrder.number = count;
                        onGoodsCountChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setView(etCount).show();
    }

    protected Callback mCallback;

    public void setCallback(Callback l) {
        mCallback = l;
    }

    public interface Callback {
        public void onGoodsCountChanged(MenuGoodsView mv, double number);
    }
}
