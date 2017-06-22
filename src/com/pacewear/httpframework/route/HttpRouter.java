
package com.pacewear.httpframework.route;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.core.IHttpClient;
import com.pacewear.httpframework.okhttp.OkHttpClientImpl;

public class HttpRouter implements IHttpRouter {

    public static IHttpRouter get() {
        // TODO Auto-generated constructor stub
        return null;
    }

    @Override
    public IHttpProxyChannel getSelectChannel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IHttpClient getSelectHttpClient() {

        return new OkHttpClientImpl();
    }

}
