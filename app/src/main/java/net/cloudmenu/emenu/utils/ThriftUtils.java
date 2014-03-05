package net.cloudmenu.emenu.utils;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.TServiceClient;

public class ThriftUtils {
    private static final String TAG = "ThriftUtils";

    public static void releaseClient(TServiceClient client) {
        if (client != null && client.getInputProtocol() != null) {
            client.getInputProtocol().getTransport().close();
        }
    }

    public static String convertToString(TBase base) {
        TSerializer t = new TSerializer();
        try {
            byte[] bytes = t.serialize(base);
            return new String(Base64.encodeBase64(bytes));
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    public static <T extends TBase> T convertTBase(Class<T> klass,
            String content) {
        TDeserializer t = new TDeserializer();
        try {
            byte[] bytes = Base64.decodeBase64(content.getBytes());
            T res = klass.newInstance();
            t.deserialize(res, bytes);
            return res;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

}
