
package com.pacewear.httpframework.route;

import android.content.Context;

import com.pacewear.httpframework.channel.BlueToothChannel;
import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.channel.NetChannel;
import com.pacewear.httpframework.common.DeviceUtil;
import com.pacewear.httpframework.core.IHttpClient;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.PhoneNetworkManager;

import java.util.HashMap;

public class HttpRouter implements IHttpRouter {
    private HashMap<Integer, IHttpChooser> mChooserMap = new HashMap<Integer, HttpRouter.IHttpChooser>();
    private static IHttpRouter sInstance = null;

    public HttpRouter() {
        mChooserMap.put(DEFAULT, new DefaultChooser());
        mChooserMap.put(NET_FIRST, new NetFirst());
        mChooserMap.put(BT_FIRST, new BtFirst());
        mChooserMap.put(NET_ONLY, new NetOnly());
        mChooserMap.put(BT_ONLY, new BtOnly());
    }

    public static IHttpRouter get() {
        if (sInstance == null) {
            synchronized (HttpRouter.class) {
                if (sInstance == null) {
                    sInstance = new HttpRouter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> getSelectChannel(
            Context context) {
        int channel = PhoneNetworkManager.getInstance(context).getHttpChannel();
        if (!mChooserMap.containsKey(channel)) {
            return null;
        }
        IHttpChooser chooser = mChooserMap.get(channel);
        return chooser.choose(context);
    }

    public static interface IHttpChooser {
        public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> choose(Context context);
    }

    public static class NetFirst implements IHttpChooser {

        @Override
        public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> choose(Context context) {
            if (DeviceUtil.isNetOn(context)) {
                return new NetChannel<Rsp, Param, Post>();
            }
            if (DeviceUtil.isBluetoothOn(context)) {
                return new BlueToothChannel<Rsp, Param, Post>(context);
            }
            return null;
        }
    }

    public static class NetOnly implements IHttpChooser {

        @Override
        public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> choose(Context context) {
            if (DeviceUtil.isNetOn(context)) {
                return new NetChannel<Rsp, Param, Post>();
            }
            return null;
        }

    }

    public static class BtFirst implements IHttpChooser {

        @Override
        public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> choose(Context context) {
            if (DeviceUtil.isBluetoothOn(context)) {
                return new BlueToothChannel<Rsp, Param, Post>(context);
            }
            if (DeviceUtil.isNetOn(context)) {
                return new NetChannel<Rsp, Param, Post>();
            }
            return null;
        }

    }

    public static class BtOnly implements IHttpChooser {

        @Override
        public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> choose(Context context) {
            if (DeviceUtil.isBluetoothOn(context)) {
                return new BlueToothChannel<Rsp, Param, Post>(context);
            }
            return null;
        }
    }

    public static class DefaultChooser implements IHttpChooser {

        @Override
        public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> choose(Context context) {
            if (DeviceUtil.isNetOn(context)) {
                return new NetChannel<Rsp, Param, Post>();
            }
            if (DeviceUtil.isBluetoothOn(context)) {
                return new BlueToothChannel<Rsp, Param, Post>(context);
            }
            return null;
        }

    }

}
