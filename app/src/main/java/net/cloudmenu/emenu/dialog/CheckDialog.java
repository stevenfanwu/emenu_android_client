package net.cloudmenu.emenu.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import java.util.ArrayList;
import java.util.List;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IMenuService;
import cn.com.cloudstone.menu.server.thrift.api.IMenuService.Client;

public class CheckDialog extends AlertDialog {
    private List<CheckBox> mViews = new ArrayList<CheckBox>();;
    private List<String> mNotes = new ArrayList<String>();
    private List<String> mCheckNotes = new ArrayList<String>();

    public CheckDialog(Context context) {
        super(context);
        setButton1("确认", null);
        setButton2("取消", null);
        setTitle("修改备注");
        initData();
    }

    private LoadNotesTask mTask;

    private void initData() {
        mTask = new LoadNotesTask(getContext());
        mTask.setCallback(new Callback() {
            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                mNotes.clear();
                mNotes.addAll(mTask.getResult());
                initViews();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {
            }
        });
        mTask.execute();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup view = (ViewGroup) inflater.inflate(
                R.layout.dialog_frame_check, null);
        ViewGroup viewContent = (ViewGroup) view
                .findViewById(android.R.id.content);
        for (String s : mNotes) {
            CheckBox box = new CheckBox(getContext());
            final String note = s;
            box.setText(note);
            box.setChecked(mCheckNotes.contains(note));
            viewContent.addView(box);
            mViews.add(box);
            box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    if (isChecked) {
                        if (!mCheckNotes.contains(note))
                            mCheckNotes.add(note);
                    } else {
                        mCheckNotes.remove(note);
                    }
                }
            });
        }
        setView(view);
    }

    private void refreshCheckedState() {
        for (int i = 0; i < mViews.size(); i++) {
            CheckBox view = mViews.get(i);
            String note = mNotes.get(i);
            view.setChecked(mCheckNotes.contains(note));
        }
    }

    public void setChecked(List<String> texts) {
        mCheckNotes.clear();
        if (texts != null)
            mCheckNotes.addAll(texts);
        refreshCheckedState();
    }

    public List<String> getCheckedText() {
        return mCheckNotes;
    }

    public static class LoadNotesTask extends TBaseTask {
        private List<String> mResult;
        private boolean mForseRefresh = false;

        public LoadNotesTask(Context context) {
            super(context);
            setShowProgessDialog(true);
        }

        public void setForseRefresh(boolean b) {
            mForseRefresh = b;
        }

        public List<String> getResult() {
            return mResult;
        }

        @Override
        protected TServiceClient getClient() throws TException {
            long cacheTime = RPCHelper.CACHE_TIME_LONG;
            if (mForseRefresh)
                cacheTime = RPCHelper.CACHE_TIME_REFRESH;
            return RPCHelper.getCachedMenuService(mContext, cacheTime);
        }

        @Override
        protected Object process(TServiceClient client) throws TException,
                AException {
            IMenuService.Client iclient = (Client) client;
            mResult = iclient.getAllNotes(ProfileHolder.getIns().getCurrentSid(mContext));
            return ECode.SUCCESS;
        }

    }

}
