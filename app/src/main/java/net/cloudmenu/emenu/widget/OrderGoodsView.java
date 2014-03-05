package net.cloudmenu.emenu.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.dialog.CheckDialog;
import net.cloudmenu.emenu.dialog.RadioDialog;
import net.cloudmenu.emenu.task.CancelGoodsTask;
import net.cloudmenu.emenu.task.LoginTask;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.utils.MenuUtils;
import net.cloudmenu.emenu.utils.ProfileHolder;

import java.util.ArrayList;
import java.util.List;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.com.cloudstone.menu.server.thrift.api.GoodState;
import cn.com.cloudstone.menu.server.thrift.api.Login;
import cn.com.cloudstone.menu.server.thrift.api.UserType;

public class OrderGoodsView extends MenuGoodsView implements
        OnLongClickListener {

    protected TextView tvNotes;
    protected TextView tvStatus;
    protected Button btnDelete;
    protected TextView tvCategory;

    public OrderGoodsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initContent() {
        super.initContent();
        tvCategory.setText(parseCategory(mGoods.getCategory()));
        GoodState state = mOrder.getGoodstate();
        String stateStr = MenuUtils.getStateStr(state);
        // String stateStr = mItem.getOrder().getStatus();
        tvStatus.setText(stateStr);
        List<String> remarks = mOrder.getRemarks();
        String remark = parseRemarks(remarks);
        if (remark == null || remark.length() == 0)
            remark = "暂无备注";
        tvNotes.setText(remark);

        if (GlobalValue.isTypeOrdered(mMenuType)) {
            btnDelete.setVisibility(View.INVISIBLE);
            tvNotes.setEnabled(false);
            tvStatus.setEnabled(false);
        } else {
            btnDelete.setVisibility(View.VISIBLE);
            tvNotes.setEnabled(true);
            tvStatus.setEnabled(true);
        }
    }

    private String parseRemarks(List<String> remarks) {
        if (remarks == null)
            return null;
        StringBuffer sb = new StringBuffer();
        for (String s : remarks) {
            sb.append(s + ";");
        }
        return sb.toString();
    }

    private String parseCategory(String t) {
        int len = t.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            if (i != 0 && i % 2 == 0)
                sb.append("\n");
            sb.append(t.charAt(i));
        }
        return sb.toString();
    }

    @Override
    protected void initElements() {
        super.initElements();
        tvNotes = (TextView) findViewById(R.id.tv_notes);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        tvCategory = (TextView) findViewById(R.id.tv_category);
        btnDelete.setOnClickListener(this);
        tvNotes.setOnClickListener(this);
        tvStatus.setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mGoods.isSoldout())
            return true;
        if (v == this) {
            showCancelGoodsDialog();
        }
        return true;
    }

    private void showCancelGoodsDialog() {
        Login login = ProfileHolder.getIns().getCurrentLogin(getContext());
        boolean isAdmin = login != null
                && login.getUserType() == UserType.Admin;
        boolean isOrderedType = GlobalValue.isTypeOrdered(mMenuType);
        boolean isCanceled = mOrder.getGoodstate() == GoodState.Canceled;
        if (!isAdmin || !isOrderedType || isCanceled) {
            // only ordered goods could be canceled.
            return;
        }
        String msg = String.format("您确认将%d号中的[%s]退掉吗?", mOrder.getOrderid(),
                mGoods.getName());
        new AlertDialog.Builder(getContext()).setTitle("确认退菜").setMessage(msg)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCancelGoodsPwdDialog();
                    }
                }).setNegativeButton("取消", null).show();

    }

    private void showCancelGoodsPwdDialog() {
        final EditText editPwd = new EditText(getContext());
        new AlertDialog.Builder(getContext()).setTitle("请输入您的密码")
                .setView(editPwd)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = editPwd.getText().toString();
                        confirmWaiterPwd(pwd);
                    }
                }).setNegativeButton("取消", null).show();

    }

    private void confirmWaiterPwd(String pwd) {
        String user = ProfileHolder.getIns().getCurrentUser(getContext());
        LoginTask task = new LoginTask(getContext(), user, pwd);
        task.setCallback(new HandlerMessageTask.Callback() {

            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                cancelGoods();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {

            }
        });
        task.execute();
    }

    private void cancelGoods() {
        CancelGoodsTask task = new CancelGoodsTask(getContext(),
                mOrder.getOrderid(), mOrder.getId());
        task.setCallback(new HandlerMessageTask.Callback() {

            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                mOrder.setGoodstate(GoodState.Canceled);
                initContent();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {

            }
        });
        task.execute();

    }

    private CheckDialog mCheckDialog;
    private RadioDialog mRadioDialog;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btnDelete) {
            createDeleteDialog().show();
        } else if (v == tvNotes) {
            if (mCheckDialog == null) {
                mCheckDialog = new CheckDialog(getContext());
                mCheckDialog.setButton1("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                List<String> texts = new ArrayList<String>();
                                texts.addAll(mCheckDialog.getCheckedText());
                                mOrder.setRemarks(texts);
                                initContent();
                            }
                        });
            }
            mCheckDialog.setChecked(mOrder.getRemarks());
            mCheckDialog.show();
        } else if (v == tvStatus) {
            if (mRadioDialog == null) {
                mRadioDialog = new RadioDialog(getContext());
                mRadioDialog.setButton1("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                String text = mRadioDialog.getCheckedText();
                                mOrder.setGoodstate(MenuUtils.getState(text));
                                initContent();
                            }
                        });
            }
            GoodState state = mOrder.getGoodstate();
            String text = MenuUtils.getStateStr(state);
            // String text = mItem.getOrder().getStatus();
            mRadioDialog.setChecked(text);
            mRadioDialog.show();
        }
    }

    @Override
    protected void onGoodsCountChanged() {
        super.onGoodsCountChanged();
        if (mOrder.getNumber() == 0) {
            AlertDialog dialog = createDeleteDialog();
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnAdd.performClick();
                        }
                    });
            dialog.show();
        }
    }

    private AlertDialog createDeleteDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("提醒").setMessage("您确定要删除这道菜？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onOrderDelete();
                    }
                }).create();
        return dialog;
    }

    protected void onOrderDelete() {
        mOrder.setNumber(0);
        if (mCallback != null)
            ((Callback) mCallback).onOrderDelete(this);
    }

    public interface Callback extends MenuGoodsView.Callback {
        public void onOrderDelete(OrderGoodsView view);
    }
}
