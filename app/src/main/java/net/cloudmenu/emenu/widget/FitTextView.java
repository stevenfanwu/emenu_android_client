package net.cloudmenu.emenu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FitTextView extends TextView {
    public FitTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed)
            setFitText();
    }

    private int mLines = 0;

    @Override
    public void setLines(int lines) {
        super.setLines(lines);
        mLines = lines;
    }

    private void setFitText() {
        if (mLines <= 0)
            return;
        float lineScale = mLines;
        if (lineScale > 1)
            lineScale -= 0.5f;
        int width = getWidth();
        String content = getText().toString();
        int breakLength = getPaint().breakText(content, true,
                width * lineScale, null);
        if (breakLength < content.length()) {
            String s = content.substring(0, breakLength) + "...";
            setText(s);
        }
    }

}
