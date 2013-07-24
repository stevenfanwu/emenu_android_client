package net.cloudmenu.emenu.widget;

import net.cloudmenu.emenu.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * custom star rating bar.
 */
public class StarRatingBar extends View {
	private Drawable mStarDrawable;
	private float mStarScale = 1;
	private float mStarInterval;
	private int mStarCount;
	private static final float mMaxProgress = 3f;
	private float mProgress = 0;

	public StarRatingBar(Context context) {
		this(context, null);
	}

	public StarRatingBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StarRatingBar(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.StarRatingBar, style, 0);
		mStarDrawable = ta.getDrawable(R.styleable.StarRatingBar_starDrawable);
		mStarInterval = ta.getDimension(R.styleable.StarRatingBar_starInterval,
				5);
		mStarCount = ta.getInt(R.styleable.StarRatingBar_starCount, 5);
		mProgress = ta.getInteger(R.styleable.StarRatingBar_progress, 0);
		ta.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mStarDrawable == null) {
			setMeasuredDimension(0, 0);
			return;
		}
		int maxHeight = mStarDrawable.getIntrinsicHeight() + getPaddingTop()
				+ getPaddingBottom();
		int maxWidth = (int) (mStarDrawable.getIntrinsicWidth() * mStarCount
				+ mStarInterval * (mStarCount - 1) + getPaddingLeft() + getPaddingRight());
		int measureWidth = resolveSize(maxWidth, widthMeasureSpec);
		int measureHeight= resolveSize(maxHeight, heightMeasureSpec);
//		mStarScale = measureWidth * 1.0f / maxWidth;
		setMeasuredDimension(measureWidth, measureHeight);
	}

	public void setProgress(float progress) {
		if (progress > mMaxProgress)
			progress = mMaxProgress;
		if (progress < 0)
			progress = 0;
		mProgress = progress;
		invalidate();
	}

	public float getMaxProgress() {
		return mMaxProgress;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mStarDrawable == null)
			return;
		float leftProgress = mProgress;
		float left = 0;
		int starWidth = (int) (mStarDrawable.getIntrinsicWidth() * mStarScale);
		int starHeight = (int) (mStarDrawable.getIntrinsicHeight() * mStarScale);
		for (int i = 0; i < mStarCount; i++) {
			Drawable star = getStarByProgress(leftProgress);
			leftProgress -= mMaxProgress / mStarCount;
			star.setBounds((int) left, 0, (int) (left + starWidth),
					(int) (starHeight));
			left += starWidth + mStarInterval;
			star.draw(canvas);
		}
	}

	private Drawable getStarByProgress(float score) {
		Drawable res = mStarDrawable.mutate();
		if (score <= 0)
			res.setLevel(0);
		else
			res.setLevel(1);
		return res;
	}

}