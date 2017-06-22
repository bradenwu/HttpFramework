
package com.pacewear.httpframework.route;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.core.IHttpClient;
import com.tencent.tws.api.HttpPackage;

public interface IHttpRouter {
    public IHttpProxyChannel getSelectChannel();

    public IHttpClient getSelectHttpClient(HttpPackage e);
}
