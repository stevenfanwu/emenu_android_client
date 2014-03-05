package net.cloudmenu.emenu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;

import net.cloudmenu.emenu.R;

public class ProgressDialog extends Dialog {
    private ProgressBar mProgress;

    public ProgressDialog(Context context) {
        super(context, R.style.Theme_Dialog_Progress);
        setCanceledOnTouchOutside(false);
        mProgress = new ProgressBar(context);
        setContentView(mProgress);
    }

}
