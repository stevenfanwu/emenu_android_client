package net.cloudmenu.emenu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.MenuUtils;

import java.util.ArrayList;
import java.util.List;

import cn.buding.common.widget.AsyncImageView;
import cn.buding.common.widget.MGallery;

public class PictureGallery extends Activity implements OnClickListener,
        OnItemClickListener {
    public static final String EXTRA_URLS = "extra_urls";
    private MGallery mGallery;
    private ArrayList<String> mUrls;
    private ImageAdapter mAdapter;
    private View btnClose;
    private View btnLeft;
    private View btnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Translucent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_gallery);
        mUrls = getIntent().getStringArrayListExtra(EXTRA_URLS);
        initElements();
        mAdapter = new ImageAdapter(mUrls);
        mGallery.setAdapter(mAdapter);
    }

    private void initElements() {
        mGallery = (MGallery) findViewById(R.id.gallery);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnClose = findViewById(R.id.btn_close);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnLeft.setVisibility(View.INVISIBLE);
        btnRight.setVisibility(View.INVISIBLE);
        mGallery.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == btnLeft) {
            mGallery.movePrevious();
        } else if (v == btnRight) {
            mGallery.moveNext();
        } else if (v == btnClose) {
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onBackPressed();
        return super.onTouchEvent(event);
    }

    private class ImageAdapter extends BaseAdapter {
        private List<String> mUrls;

        public ImageAdapter(List<String> urls) {
            mUrls = urls;
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mUrls.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.item_picture, null);
            }
            AsyncImageView aiv = (AsyncImageView) convertView;
            String url = mUrls.get(position);
            url = MenuUtils.getImgUrl(PictureGallery.this, url);
            if (url != null && !url.equals(aiv.getImgUrl()))
                aiv.postLoading(url);
            return convertView;
        }

    }

}
