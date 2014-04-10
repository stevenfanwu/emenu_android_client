package net.cloudmenu.emenu.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.activity.MainTabHost;
import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.task.QueryTableInfoTask;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.GlobalConfig;
import net.cloudmenu.emenu.utils.ProfileHolder;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import java.util.ArrayList;
import java.util.List;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.buding.common.widget.MyToast;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService.Client;
import cn.com.cloudstone.menu.server.thrift.api.PermissionDenyExcpetion;
import cn.com.cloudstone.menu.server.thrift.api.TableInfo;
import cn.com.cloudstone.menu.server.thrift.api.TableOccupiedException;
import cn.com.cloudstone.menu.server.thrift.api.TableStatus;
import cn.com.cloudstone.menu.server.thrift.api.UserNotLoginException;

public class TableDialog extends AlertDialog implements OnClickListener,
        OnItemSelectedListener {
    private EditText etNumber;
    private Spinner spTable;
    private QueryTableInfoTask mTask;
    private List<TableInfo> mTableInfos = new ArrayList<TableInfo>();
    private WindowManager.LayoutParams lp;

    public TableDialog(Context context) {
        super(context);
        setTitle("Check In");
        setButton1(context.getString(android.R.string.ok), this, false);
        setButton2(context.getString(android.R.string.cancel), this);
        setView(R.layout.dialog_frame_table);
        initElements();
    }

    private boolean mFirstTime = true;

    @Override
    public void show() {
        getWindow().setAttributes(lp);
        super.show();
        if (mFirstTime) {
            initTableData();
            mFirstTime = false;
        }
    }

    private void initTableData() {
        if (mTask != null && mTask.getStatus() == Status.RUNNING)
            return;
        mTask = new QueryTableInfoTask(getContext());
        mTask.setCallback(new Callback() {
            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                mTableInfos.clear();
                List<TableInfo> results = mTask.getInfos();
                if (results != null) {
                    for (TableInfo info : results) {
                        if (info.getStatus() == TableStatus.Empty
                                || info.getStatus() == TableStatus.Ordered) {
                            mTableInfos.add(info);
                        }
                    }
                }
                initTableAdapter();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {
            }
        });
        mTask.execute();
    }

    private void initTableAdapter() {
        if (mTableInfos == null)
            return;
        List<String> tables = new ArrayList<String>();
        for (TableInfo info : mTableInfos) {
            tables.add(info.getId());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item);
        for (String s : tables)
            adapter.add(s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTable.setAdapter(adapter);
    }

    private void initElements() {
        spTable = (Spinner) findViewById(R.id.sp_table);
        spTable.setOnItemSelectedListener(this);
        etNumber = (EditText) findViewById(R.id.et_number);
        lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case R.id.bt_1:
            if (!validInput()) {
                return;
            }
            final String tableId = spTable.getSelectedItem().toString();
            int number = Integer.valueOf(etNumber.getText().toString());
            OccupyTableTask task = new OccupyTableTask(getContext(), tableId,
                    number);
            task.setCallback(new Callback() {
                @Override
                public void onSuccess(HandlerMessageTask task, Object t) {
                    ProfileHolder.getIns().setCurrentTableId(getContext(),
                            tableId);
                    getContext().startActivity(
                            new Intent(getContext(), MainTabHost.class));
                }

                @Override
                public void onFail(HandlerMessageTask task, Object t) {
                }
            });
            task.execute();
            dismiss();
            break;
        }
    }

    private boolean validInput() {
        String table = null;
        if (spTable.getSelectedItem() != null) {
            table = spTable.getSelectedItem().toString();
        }
        String number = etNumber.getText().toString();
        if (table == null) {
            MyToast.makeText(getContext(), "暂无可用餐桌").show();
            return false;
        }
        if (table.length() == 0) {
            MyToast.makeText(getContext(), "桌号不能为空").show();
            return false;
        }
        if (number.length() == 0) {
            MyToast.makeText(getContext(), "顾客数不能为空").show();
            return false;
        }
        try {
            Integer.valueOf(number);
        } catch (Exception e) {
            MyToast.makeText(getContext(), "顾客数输入有误").show();
            return false;
        }
        return true;

    }

    class OccupyTableTask extends TBaseTask {
        private String mTableId;
        private int mNum;

        public OccupyTableTask(Context context, String tableId, int number) {
            super(context);
            setShowCodeMsg(true);
            mTableId = tableId;
            mNum = number;
        }

        @Override
        protected TServiceClient getClient() throws TException {
            return RPCHelper.getWaiterService(getContext());
        }

        @Override
        protected Object process(TServiceClient client) throws TException,
                AException {
            IWaiterService.Client iclient = (Client) client;
            try {
                if (!GlobalConfig.isWorkWithoutNetWork(mContext)) {
                    String sid = ProfileHolder.getIns().getCurrentSid(mContext);
                    iclient.occupyTable(sid, mTableId, mNum);
                }
                return ECode.SUCCESS;
            } catch (UserNotLoginException e) {
                return e;
            } catch (PermissionDenyExcpetion e) {
                return e;
            } catch (TableOccupiedException e) {
                return e;
            }
        }

    }
}
