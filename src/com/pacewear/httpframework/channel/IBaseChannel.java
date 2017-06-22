
package com.pacewear.httpframework.channel;

import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

public interface IBaseChannel {
    public HttpResponseResult transmit(HttpRequestGeneralParams request);
}
