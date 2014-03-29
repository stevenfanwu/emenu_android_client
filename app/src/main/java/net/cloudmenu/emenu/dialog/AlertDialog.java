package net.cloudmenu.emenu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.cloudmenu.emenu.R;

public class AlertDialog extends Dialog {
    private Button bt1;
    private Button bt2;
    private TextView tvTitle;
    private TextView tvMessage;
    private ViewGroup mWidgetFrame;
    private View llMessage;

    public AlertDialog(Context context) {
        super(context, R.style.Theme_Dialog);
        setContentView(R.layout.dialog_alert);
        initElements();
        setButton2("Cancel", null);
    }

    private void initElements() {
        bt1 = (Button) findViewById(R.id.bt_1);
        bt2 = (Button) findViewById(R.id.bt_2);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        mWidgetFrame = (ViewGroup) findViewById(android.R.id.widget_frame);
        llMessage = findViewById(R.id.ll_message);
    }

    public AlertDialog setButton1(String text,
            final DialogInterface.OnClickListener l) {
        return setButton1(text, l, true);
    }

    public AlertDialog setButton1(String text,
            final DialogInterface.OnClickListener l, boolean autoDismiss) {
        setButton(bt1, text, l, autoDismiss);
        return this;
    }

    public AlertDialog setButton2(String text,
            final DialogInterface.OnClickListener l) {
        setButton(bt2, text, l, true);
        return this;
    }

    public Button getButton1() {
        return bt1;
    }

    public Button getButton2() {
        return bt2;
    }

    public void setView(int res) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setView(inflater.inflate(res, null));
    }

    public void setView(View v) {
        mWidgetFrame.addView(v);
        llMessage.setVisibility(View.GONE);
        mWidgetFrame.setVisibility(View.VISIBLE);
    }

    private void setButton(final Button bt, String text,
            final DialogInterface.OnClickListener l, final boolean autoDismiss) {
        bt.setText(text);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoDismiss)
                    dismiss();
                if (l != null)
                    l.onClick(AlertDialog.this, bt.getId());
            }
        });
    }

    public AlertDialog setTitle(String t) {
        tvTitle.setText(t);
        return this;
    }

    public AlertDialog setMessage(String t) {
        tvMessage.setText(t);
        llMessage.setVisibility(View.VISIBLE);
        mWidgetFrame.setVisibility(View.GONE);
        return this;
    }

}
