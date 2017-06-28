
package com.pacewear.httpframework.channel;

import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

public interface IHttpProxyChannel {
    public HttpResponseResult transmit(HttpRequestGeneralParams request);

    public boolean isReady();
}
