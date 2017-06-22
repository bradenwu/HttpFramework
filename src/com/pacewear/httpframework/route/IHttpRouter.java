
package com.pacewear.httpframework.route;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.core.IHttpClient;

public interface IHttpRouter {
    public IHttpProxyChannel getSelectChannel();

    public IHttpClient getSelectHttpClient();
}
