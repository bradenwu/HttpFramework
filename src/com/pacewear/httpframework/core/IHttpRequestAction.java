
package com.pacewear.httpframework.core;

import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

public interface IHttpRequestAction<Rsp, Param, Post> {
    public Rsp onExecute(Param param, Post post);

    public HttpRequestGeneralParams prepareRequest(Param param, Post request);

    public Rsp parseResponse(HttpResponseResult rsp);
}
