package net.cloudmenu.emenu.net;

import net.cloudmenu.emenu.R;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TCachedHttpClient;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import cn.com.cloudstone.menu.server.thrift.api.IMenuService;
import cn.com.cloudstone.menu.server.thrift.api.IOrderService;
import cn.com.cloudstone.menu.server.thrift.api.IPadInfoService;
import cn.com.cloudstone.menu.server.thrift.api.IProfileService;
import cn.com.cloudstone.menu.server.thrift.api.IWaiterService;

public class RPCHelper {
    private static final String TAG = "RPCHelper";
    
    public static final String DEFAULT_HOST_IP = "192.168.0.106";

    private static final String DEFAULT_API_SUFFIX = ".thrift";

    // public static final String DEFAULT_HOST_IP = "http://110.231.175.37/";
    // public static final String DEFAULT_HOST_IP =
    // "http://anboo-tech.eicp.net/";
    // private static final String DEFAULT_API_SUFFIX = ".ashx";
    private static final int SOCKET_TIME_OUT = 20000;

    private static final int CONNECTION_TIME_OUT = 20000;

    public static final long CACHE_TIME_LONG = 500 * 24 * 3600 * 1000;

    public static final long CACHE_TIME_SHORT = 300 * 24 * 3600 * 1000;

    public static final long CACHE_TIME_REFRESH = Long.MIN_VALUE;

    private static final int MAX_READ_LENGTH = 512 * 1024;

    public static String getHostIp(Context context) {
        SharedPreferences preference = PreferenceManager
                .getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pre_key_custom_api_addr);
        return preference.getString(key, DEFAULT_HOST_IP);
    }

    private static String getApiSuffix(Context context) {
        SharedPreferences preference = PreferenceManager
                .getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pre_key_custom_api_suffix);
        return preference.getString(key, DEFAULT_API_SUFFIX);
    }

    private static HttpClient getHttpCilent() {
        HttpParams param = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(param, CONNECTION_TIME_OUT);
        HttpConnectionParams.setSoTimeout(param, SOCKET_TIME_OUT);
        return new DefaultHttpClient(param);
    }

    private static TProtocol getTProtocolWithCache(Context context, String url,
            boolean clearCacheMode, long cacheTime) throws TException {
        HttpClient client = getHttpCilent();
        TCachedHttpClient transport = new TCachedHttpClient(context,
                getHostIp(context) + url + getApiSuffix(context), client);
        transport.setClearCache(clearCacheMode);
        transport.setCacheAvailableTime(cacheTime);
        transport.open();
        return new TBinaryProtocol(transport);
    }

    private static TProtocol getTProtocol(Context context, String url)
            throws TException {
        HttpClient client = getHttpCilent();
        String finalUrl = getHostIp(context) + url + getApiSuffix(context);
        TTransport transport = new THttpClient(finalUrl, client);
        Log.i(TAG, "url = " + finalUrl);
        transport.open();
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        protocol.setReadLength(MAX_READ_LENGTH);
        return protocol;
    }

    public static IPadInfoService.Client getPadInfoService(Context context)
            throws TException {
        return new IPadInfoService.Client(getTProtocol(context,
                "padinfoservice"));
    }

    public static IMenuService.Client getMenuService(Context context)
            throws TException {
        return new IMenuService.Client(getTProtocol(context, "menuservice"));
    }

    public static IProfileService.Client getProfileService(Context context)
            throws TException {
        return new IProfileService.Client(getTProtocol(context,
                "profileservice"));
    }

    public static IProfileService.Client getCachedProfileService(Context context)
            throws TException {
        return new IProfileService.Client(getTProtocolWithCache(context,
                "profileservice", false, CACHE_TIME_SHORT));
    }

    public static IProfileService.Client getCachedProfileService(
            Context context, long cacheTime) throws TException {
        return new IProfileService.Client(getTProtocolWithCache(context,
                "profileservice", false, cacheTime));
    }

    public static IOrderService.Client getOrderService(Context context)
            throws TException {
        return new IOrderService.Client(getTProtocol(context, "orderservice"));
    }

    public static IWaiterService.Client getWaiterService(Context context)
            throws TException {
        return new IWaiterService.Client(getTProtocol(context, "waiterservice"));
    }

    public static IWaiterService.Client getCachedWaiterService(Context context)
            throws TException {
        return getCachedWaiterService(context, CACHE_TIME_LONG);
    }

    public static IWaiterService.Client getCachedWaiterService(Context context,
            long cacheTime) throws TException {
        return new IWaiterService.Client(getTProtocolWithCache(context,
                "waiterservice", false, cacheTime));
    }

    public static IMenuService.Client getCachedMenuService(Context context)
            throws TException {
        return getCachedMenuService(context, CACHE_TIME_LONG);
    }

    public static IMenuService.Client getCachedMenuService(Context context,
            long cacheTime) throws TException {
        return new IMenuService.Client(getTProtocolWithCache(context,
                "menuservice", false, cacheTime));
    }

    public static IMenuService.Client getClearCacheMenuService(Context context)
            throws TException {
        return new IMenuService.Client(getTProtocolWithCache(context,
                "menuservice", true, CACHE_TIME_LONG));
    }

    public static void releaseClient(TServiceClient client) {
        if (client != null && client.getInputProtocol() != null
                && client.getInputProtocol().getTransport() != null)
            client.getInputProtocol().getTransport().close();
    }

}
