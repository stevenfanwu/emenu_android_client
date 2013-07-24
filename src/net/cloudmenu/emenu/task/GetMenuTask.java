package net.cloudmenu.emenu.task;

import net.cloudmenu.emenu.net.RPCHelper;
import net.cloudmenu.emenu.utils.ThriftUtils;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import android.content.Context;
import cn.buding.common.exception.ECode;
import cn.com.cloudstone.menu.server.thrift.api.AException;
import cn.com.cloudstone.menu.server.thrift.api.IMenuService;
import cn.com.cloudstone.menu.server.thrift.api.IMenuService.Client;
import cn.com.cloudstone.menu.server.thrift.api.Menu;

public class GetMenuTask extends TBaseTask {
	private Menu mMenu;
	private boolean mForseRefresh = false;

	public GetMenuTask(Context context) {
		super(context);
		setShowProgessDialog(true);
	}

	public void setForseRefresh(boolean b) {
		mForseRefresh = b;
	}

	public Menu getResult() {
		return mMenu;
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
		mMenu = iclient.getCurrentMenu();
		return ECode.SUCCESS;
	}

}