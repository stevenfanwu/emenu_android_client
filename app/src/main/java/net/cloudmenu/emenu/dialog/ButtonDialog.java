package net.cloudmenu.emenu.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;

import net.cloudmenu.emenu.R;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;

public class ButtonDialog extends AlertDialog implements
        android.view.View.OnClickListener {
    private LinearLayout mButtonView;
    private int mMaxButtonsOneLine = 3;

    public ButtonDialog(Context context) {
        super(context);
        getButton1().setVisibility(View.GONE);
        getButton2().setVisibility(View.GONE);

        mButtonView = new TableLayout(context);
        mButtonView.setGravity(Gravity.CENTER_VERTICAL);
        mButtonView.setLayoutParams(new FrameLayout.LayoutParams(FILL_PARENT,
                FILL_PARENT));
        setView(mButtonView);
        mButtonView.setPadding(10, 0, 10, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_cancel:
            cancel();
            break;
        }
    }

    public Button addButton(int id, String text) {
        TableRow tableRow = null;
        if (mButtonView.getChildCount() > 0) {
            tableRow = (TableRow) mButtonView.getChildAt(mButtonView
                    .getChildCount() - 1);
        }
        if (tableRow == null || tableRow.getChildCount() >= mMaxButtonsOneLine) {
            tableRow = new TableRow(getContext());
            mButtonView.addView(tableRow);
        }
        Button btn = new Button(getContext());
        btn.setId(id);
        btn.setText(text);
        tableRow.addView(btn);
        LinearLayout.LayoutParams param = (LayoutParams) btn.getLayoutParams();
        param.weight = 1;
        param.width = 0;
        btn.setOnClickListener(this);
        return btn;
    }
}
