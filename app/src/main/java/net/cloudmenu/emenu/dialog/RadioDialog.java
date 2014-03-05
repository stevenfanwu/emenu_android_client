package net.cloudmenu.emenu.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.utils.MenuUtils;
import net.cloudmenu.emenu.utils.ViewUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import cn.com.cloudstone.menu.server.thrift.api.GoodState;

public class RadioDialog extends AlertDialog {
    private List<RadioButton> mViews;

    public RadioDialog(Context context) {
        super(context);
        setButton1("确认", null);
        setButton2("取消", null);
        setTitle("修改状态");
        initView();
        initCheckBoxs();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup view = (ViewGroup) inflater.inflate(
                R.layout.dialog_frame_radio, null);
        ViewGroup viewContent = (ViewGroup) view.findViewById(android.R.id.content);
        for (GoodState state : EnumSet.allOf(GoodState.class)) {
            String s = MenuUtils.getStateStr(state);
            RadioButton btn = new RadioButton(getContext());
            btn.setText(s);
            viewContent.addView(btn);
            btn.setEnabled(MenuUtils.stateEditable(state));
        }
        // 暂无 radiobutton
        RadioButton btn = new RadioButton(getContext());
        btn.setText("暂无");
        viewContent.addView(btn);
        setView(view);
    }

    public void setChecked(String texts) {
        for (RadioButton box : mViews) {
            box.setChecked(texts != null
                    && texts.equals(box.getText().toString()));
        }
    }

    public String getCheckedText() {
        for (RadioButton box : mViews) {
            if (box.isChecked()) {
                return box.getText().toString();
            }
        }
        return null;
    }

    private void initCheckBoxs() {
        List<View> views = new ArrayList<View>();
        View content = findViewById(android.R.id.content);
        ViewUtils.findViews(views, content, RadioButton.class);
        mViews = new ArrayList<RadioButton>();
        for (View v : views) {
            RadioButton cb = (RadioButton) v;
            mViews.add(cb);
        }

    }

}
