package net.cloudmenu.emenu.activity;

import greendroid.widget.PagedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cloudmenu.emenu.R;
import net.cloudmenu.emenu.dialog.AlertDialog;
import net.cloudmenu.emenu.dialog.CheckDialog;
import net.cloudmenu.emenu.dialog.RadioDialog;
import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.task.QueryOrderTask;
import net.cloudmenu.emenu.task.TBaseTask;
import net.cloudmenu.emenu.utils.GlobalValue;
import net.cloudmenu.emenu.utils.GlobalValue.GoodsOrderMap;
import net.cloudmenu.emenu.utils.GlobalConfig;
import net.cloudmenu.emenu.utils.MenuUtils;
import net.cloudmenu.emenu.utils.ProfileHolder;
import net.cloudmenu.emenu.widget.MenuGoodsView;
import net.cloudmenu.emenu.widget.MenuPageView;
import net.cloudmenu.emenu.widget.MenuPageView.LayoutType;
import net.cloudmenu.emenu.widget.OrderGoodsView;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.buding.common.asynctask.HandlerMessageTask;
import cn.buding.common.asynctask.HandlerMessageTask.Callback;
import cn.buding.common.exception.ECode;
import cn.buding.common.widget.MyToast;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.GoodState;
import cn.com.cloudstone.menu.server.thrift.api.Goods;
import cn.com.cloudstone.menu.server.thrift.api.GoodsOrder;
import cn.com.cloudstone.menu.server.thrift.api.HasInvalidGoodsException;
import cn.com.cloudstone.menu.server.thrift.api.IOrderService;
import cn.com.cloudstone.menu.server.thrift.api.IOrderService.Client;
import cn.com.cloudstone.menu.server.thrift.api.MenuPage;
import cn.com.cloudstone.menu.server.thrift.api.Order;
import cn.com.cloudstone.menu.server.thrift.api.TableEmptyException;
import cn.com.cloudstone.menu.server.thrift.api.UnderMinChargeException;
import cn.com.cloudstone.menu.server.thrift.api.UserNotLoginException;

public class OrderActivity extends MenuBase implements
        net.cloudmenu.emenu.widget.OrderGoodsView.Callback {
    private Map<Integer, ArrayList<GoodsOrder>> mItems = new HashMap<Integer, ArrayList<GoodsOrder>>();
    private MenuPageMap mPages = new MenuPageMap();

    private View tvRemark;
//    private View tvStatus;

    private Button btnConfirm;
//    private Button btnRefresh;
    private TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected void initContent() {
        super.initContent();
        refreshData();
    }

    private void refreshData() {
//        btnRefresh.setEnabled(GlobalValue.getIns().getOrder() != null);
        initCurrentOrderData();
        makePages();
        refreshPage();
        refreshTotalPrice();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private CheckDialog mCheckDialog;
//    private RadioDialog mRadioDialog;
    private AlertDialog mConfirmDialog;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == tvRemark) {
            if (mCheckDialog == null) {
                mCheckDialog = new CheckDialog(this);
                mCheckDialog.setButton1(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                List<String> texts = new ArrayList<String>();
                                texts.addAll(mCheckDialog.getCheckedText());
                                List<GoodsOrder> items = mItems
                                        .get(GlobalValue.TYPE_CURRENT);
                                for (GoodsOrder item : items) {
                                    item.setRemarks(texts);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
            }
            mCheckDialog.show();
//        } else if (v == tvStatus) {
//            if (mRadioDialog == null) {
//                mRadioDialog = new RadioDialog(this);
//                mRadioDialog.setButton1(getString(android.R.string.ok),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                    int which) {
//                                String text = mRadioDialog.getCheckedText();
//                                List<GoodsOrder> items = mItems
//                                        .get(GlobalValue.TYPE_CURRENT);
//                                GoodState state = MenuUtils.getState(text);
//                                for (GoodsOrder item : items) {
//                                    item.setGoodstate(state);
//                                }
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        });
//            }
//            mRadioDialog.show();
        } else if (v == btnConfirm) {
            List<GoodsOrder> items = mItems.get(GlobalValue.TYPE_CURRENT);
            if (items.size() == 0) {
                MyToast.makeText(this, getString(R.string.activity_order_no_order)).show();
                return;
            }
            mConfirmDialog = createConfirmDialog();
            mConfirmDialog.show();
//        } else if (v == btnRefresh) {
//            List<Order> order = GlobalValue.getIns().getOrder();
//            if (order == null || order.size() == 0)
//                return;
//            final QueryOrderTask orderTask = new QueryOrderTask(this, order
//                    .get(0).getTableId());
//            orderTask.setCallback(new Callback() {
//                @Override
//                public void onSuccess(HandlerMessageTask task, Object t) {
//                    GlobalValue.getIns().setOrder(orderTask.getOrder());
//                    initData();
//                }
//
//                @Override
//                public void onFail(HandlerMessageTask task, Object t) {
//
//                }
//            });
//            orderTask.execute();
        }
    }

    private AlertDialog createConfirmDialog() {
        boolean isWorkWithoutNetWork = GlobalConfig.isWorkWithoutNetWork(this);
        if (mConfirmDialog == null) {
            mConfirmDialog = new AlertDialog(this);
            if (!isWorkWithoutNetWork) {
                mConfirmDialog.setButton1(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                submitOrder();
                            }
                        });
                mConfirmDialog.setButton2(getString(android.R.string.cancel), null);
                mConfirmDialog.setTitle(getString(R.string.activity_order_submit_title));
            } else {
                mConfirmDialog.setButton1(getString(android.R.string.ok), null);
                mConfirmDialog.setTitle(getString(R.string.activity_order_complete));
            }
        }
        if (!isWorkWithoutNetWork) {
            double price = getTotalPrice();
            String msg = String.format("Your total order is $%.1f, do you want to submit your order？", price);
            mConfirmDialog.setMessage(msg);
        } else {
            mConfirmDialog.setMessage(getString(R.string.activity_order_incomplete_need_assistance));
        }
        return mConfirmDialog;
    }

    private void submitOrder() {
        List<GoodsOrder> items = mItems.get(GlobalValue.TYPE_CURRENT);
        SubmitOrderTask task = new SubmitOrderTask(this, items);
        task.setCallback(new Callback() {
            @Override
            public void onSuccess(HandlerMessageTask task, Object t) {
                GlobalValue.getIns().onClearTable(OrderActivity.this);
                finish();
            }

            @Override
            public void onFail(HandlerMessageTask task, Object t) {

            }
        });
        task.execute();
    }

    private void refreshTotalPrice() {
        tvPrice.setText(getString(R.string.activity_order_total_price) + getTotalPrice());
    }

    private double getTotalPrice() {
        List<GoodsOrder> items = mItems.get(GlobalValue.TYPE_CURRENT);
        double total = 0;
        if (items != null)
            for (GoodsOrder item : items) {
                total += item.getPrice() * item.getNumber();
            }
        return total;
    }

    @Override
    protected void initElements() {
        super.initElements();
        tvRemark = findViewById(R.id.tv_remark);
//        tvStatus = findViewById(R.id.tv_status);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
//        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvPrice = (TextView) findViewById(R.id.tv_price);
//        btnRefresh.setOnClickListener(this);
        tvRemark.setOnClickListener(this);
//        tvStatus.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_order;
    }

    private void initCurrentOrderData() {
        List<Integer> types = new ArrayList<Integer>();
        mItems.clear();
        // init current items
        types.add(GlobalValue.TYPE_CURRENT);
        ArrayList<GoodsOrder> curItems = new ArrayList<GoodsOrder>();
        mItems.put(GlobalValue.TYPE_CURRENT, curItems);
        GoodsOrderMap map = GlobalValue.getIns().getGoodsItemMap(
                GlobalValue.TYPE_CURRENT);
        for (GoodsOrder item : map.values()) {
            Goods good = GlobalValue.getIns().getGoods(item.getId());
            if (item.getNumber() > 0 && good != null && !good.isSoldout()) {
                curItems.add(item);
            }
        }

        // init ordered items
        List<Order> orders = GlobalValue.getIns().getOrder();
        if (orders == null || GlobalValue.getIns().getMenu() == null) {
            return;
        }
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            ArrayList<GoodsOrder> orderedItems = new ArrayList<GoodsOrder>();
            int type = GlobalValue.getOrderType(i);
            types.add(type);
            mItems.put(type, orderedItems);
            for (GoodsOrder goodOrder : order.getGoods()) {
                Goods good = GlobalValue.getIns().getGoods(goodOrder.getId());
                if (good != null) {
                    GlobalValue.getIns().putGoodsOrder(type, goodOrder);
                    orderedItems.add(goodOrder);
                }

            }
        }

        int[] typeInts = new int[types.size()];
        for (int i = 0; i < types.size(); i++) {
            typeInts[i] = types.get(i);
        }
        ((MenuPageApdapter) mAdapter).setTypes(typeInts);
    }

    @Override
    protected PagedAdapter makeAdapter() {
        MenuPageApdapter adapter = new MenuPageApdapter(mPages);
        return adapter;
    }

    private void makePages() {
        mPages.clear();
        for (Integer type : mItems.keySet()) {
            List<GoodsOrder> items = mItems.get(type);
            ArrayList<MenuPage> pages = new ArrayList<MenuPage>();
            MenuOrderPage page = null;
            for (GoodsOrder item : items) {
                Goods goods = GlobalValue.getIns().getGoods(item.getId());
                if (page == null || !page.addGoods(goods)) {
                    page = new MenuOrderPage();
                    pages.add(page);
                    page.addGoods(goods);
                }
            }
            mPages.put(type, pages);
        }
    }

    @Override
    public void onGoodsCountChanged(MenuGoodsView mv, double number) {
        refreshTotalPrice();
    }

    @Override
    public void onOrderDelete(OrderGoodsView view) {
        refreshData();
    }

    @Override
    protected LayoutType getMenuPageLayoutType() {
        return LayoutType.Order8;
    }

    @Override
    protected void onMenuPageCreate(MenuPageView view) {
        view.setCallback(this);
    }

    class MenuOrderPage extends MenuPage {
        public static final int PAGE_COUNT = 8;

        public MenuOrderPage() {
            goodsList = new ArrayList<Goods>();
        }

        public boolean addGoods(Goods good) {
            if (goodsList.size() == PAGE_COUNT)
                return false;
            else {
                goodsList.add(good);
                return true;
            }
        }
    }

    class SubmitOrderTask extends TBaseTask {
        private List<GoodsOrder> mOrders;
        private static final int ECODE_INVALIDE_GOODS_SECOND_TIME = 102;

        public SubmitOrderTask(Context context, List<GoodsOrder> orders) {
            super(context);
            mOrders = orders;
            setShowProgessDialog(true);
            setCodeMsg(ECode.SUCCESS, getString(R.string.activity_order_complete));
            setCodeMsg(ECODE_INVALIDE_GOODS_SECOND_TIME, "订单中有商品无法售卖，请咨询相关人士。");
        }

        @Override
        protected TServiceClient getClient() throws TException {
            return RPCHelper.getOrderService(OrderActivity.this);
        }

        @Override
        protected Object process(TServiceClient client) throws TException,
                AException {
            IOrderService.Client iclient = (Client) client;
            try {
                String sid = ProfileHolder.getIns().getCurrentSid(mContext);
                String tableId = ProfileHolder.getIns().getCurrentTableId(
                        mContext);
                Order order = new Order();
                order.setTableId(tableId);
                order.setGoods(mOrders);
                iclient.submitOrder(sid, order);
                return ECode.SUCCESS;
            } catch (UserNotLoginException e) {
                return e;
            } catch (TableEmptyException e) {
                return e;
            } catch (UnderMinChargeException e) {
                return e;
            } catch (HasInvalidGoodsException e) {
                return e;
            }
        }

        @Override
        protected void processResult(Object result) {
            if (result instanceof UnderMinChargeException) {
                UnderMinChargeException e = (UnderMinChargeException) result;
                showResultMessage("The minimal order is $" + e.getMinCharge()
                        + "Please continue your order.");
            } else if (result instanceof HasInvalidGoodsException) {
                showResultMessage("菜单中有商品已售罄，需要更新菜单，请确认后重新下单");
                initData(true);
            } else {
                super.processResult(result);
            }
        }

    }
}
