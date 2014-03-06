package net.cloudmenu.emenu.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.activity.MainTabHost;
import net.cloudmenu.emenu.task.QueryOrderTask;
import net.cloudmenu.emenu.task.QueryTableInfoTask;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.utils.ProfileHolder;

import java.util.ArrayList;
import java.util.List;

import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.com.cloudstone.menu.server.thrift.api.TableInfo;
import cn.com.cloudstone.menu.server.thrift.api.TableStatus;

public class TableInfoDialog extends AlertDialog implements OnItemClickListener {
    private List<TableInfo> mTableInfos;
    private ListView mListview;
    private QueryTableInfoTask mTask;
    private TableInfoAdapter mAdapter;

    public TableInfoDialog(Context context) {
        super(context);
        setTitle(R.string.table_status_check);
        setButton2("Cancel", null);
        getButton1().setVisibility(View.INVISIBLE);
        setView(R.layout.dialog_frame_table_info);
        mListview = (ListView) findViewById(android.R.id.list);
        mTableInfos = new ArrayList<TableInfo>();
        mAdapter = new TableInfoAdapter(mTableInfos);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        initData();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int p, long arg3) {
        final TableInfo info = mTableInfos.get(p);
        if (info != null && info.getStatus() == TableStatus.Occupied) {
            String tableId = "" + info.getId();
            final QueryOrderTask orderTask = new QueryOrderTask(getContext(),
                    tableId);
            orderTask.setCallback(new Callback() {
                @Override
                public void onSuccess(HandlerMessageTask task, Object t) {
                    cancel();
                    GlobalValue.getIns().setOrder(orderTask.getOrder());
                    ProfileHolder.getIns().setCurrentTableId(getContext(),
                            info.getId());
                    Intent intent = new Intent(getContext(), MainTabHost.class);
                    intent.putExtra(MainTabHost.EXTRA_INDEX, 1);
                    getContext().startActivity(intent);
                }

                @Override
                public void onFail(HandlerMessageTask task, Object t) {

                }
            });
            orderTask.execute();
        }
    }

    private void initData() {
        if (mTask != null && mTask.getStatus() == Status.RUNNING)
            return;
        mTask = new QueryTableInfoTask(getContext());
        mTask.setForseRefresh(true);
        mTask.setCallback(new Callback() {
            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                mTableInfos.clear();
                mTableInfos.addAll(mTask.getInfos());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {
            }
        });
        mTask.execute();
    }

    private class TableInfoAdapter extends BaseAdapter {
        private List<TableInfo> mTableInfos;

        public TableInfoAdapter(List<TableInfo> info) {
            mTableInfos = info;
        }

        @Override
        public int getCount() {
            return mTableInfos.size();
        }

        @Override
        public Object getItem(int p) {
            return mTableInfos.get(p);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int p, View view, ViewGroup arg2) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item_table_info, null);
            }
            TextView tvTable = (TextView) view.findViewById(R.id.tv_table);
            TextView tvStatus = (TextView) view.findViewById(R.id.tv_status);
            TableInfo info = mTableInfos.get(p);
            tvTable.setText(info.getId());
            TableStatus status = info.getStatus();
            String stat = "";
            if (status != null) {
                switch (status) {
                case Empty:
                    stat = "Empty";
                    break;
                case Occupied:
                    stat = "Full";
                    break;
                case Ordered:
                    stat = "Reserved";
                    break;
                }
            }
            tvStatus.setText(stat);
            return view;
        }

    }

}
