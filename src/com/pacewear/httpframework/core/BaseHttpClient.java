
package com.pacewear.httpframework.core;

import android.content.Context;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.route.IHttpRouter;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

public abstract class BaseHttpClient<Rsp, Param, Post> implements IHttpClient<Rsp, Param, Post> {
    private IHttpRouter mHttpRouter = null;
    private Context mContext = null;

    public BaseHttpClient(Context context) {
        mContext = context;
    }

    @Override
    public Rsp execute(Param param, Post post) {
        IHttpProxyChannel channel = (mHttpRouter == null ? null : mHttpRouter.getSelectChannel(mContext));
        if (channel != null) {
            return transmitHttpChannel(channel, param, post);
        }
        return onExecute(param, post);
    }

    private Rsp transmitHttpChannel(IHttpProxyChannel channel, Param param, Post post) {
        HttpRequestGeneralParams request = prepareRequest(param, post);
        HttpResponseResult httpResponseWrap = channel.transmit(request);
        return parseResponse(httpResponseWrap);
    }

    protected abstract Rsp onExecute(Param param, Post post);

    protected abstract HttpRequestGeneralParams prepareRequest(Param param, Post request);

    protected abstract Rsp parseResponse(HttpResponseResult rsp);
}
