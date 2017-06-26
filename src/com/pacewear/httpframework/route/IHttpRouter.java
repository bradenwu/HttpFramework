
package com.pacewear.httpframework.route;

import android.content.Context;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.core.IHttpClient;
import com.tencent.tws.api.HttpPackage;

public interface IHttpRouter {
    public IHttpProxyChannel getSelectChannel(Context context);

    public IHttpClient getSelectHttpClient(HttpPackage e);
}
