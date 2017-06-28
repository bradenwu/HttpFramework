
package com.pacewear.httpframework.route;

import android.content.Context;

import com.pacewear.httpframework.channel.BtChannel;
import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.core.IHttpClient;
import com.pacewear.httpframework.okhttp.OkHttpClientImpl;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.PhoneNetworkManager;

public class HttpRouter implements IHttpRouter {

    public static IHttpRouter get() {
        // TODO Auto-generated constructor stub
        return null;
    }

    @Override
    public IHttpClient getSelectHttpClient(HttpPackage e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IHttpProxyChannel getSelectChannel(Context context) {
        // TODO Auto-generated method stub
        int channel = PhoneNetworkManager.getInstance(context).getHttpChannel();
        if (channel > 0) {
            return new BtChannel(context);
        }
        return null;
    }

}
