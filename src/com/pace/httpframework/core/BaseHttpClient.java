
package com.pace.httpframework.core;

import com.pace.httpframework.bean.HttpParamWrap;
import com.pace.httpframework.bean.HttpPostWrap;
import com.pace.httpframework.bean.HttpResponseWrap;
import com.pace.httpframework.channel.IBaseChannel;
import com.pace.httpframework.route.IHttpRouter;

public abstract class BaseHttpClient<Rsp, Param, Post> implements IHttpClient<Rsp, Param, Post> {
    private IHttpRouter mHttpRouter = null;

    public BaseHttpClient() {

    }

    @Override
    public Rsp execute(Param param, Post post) {
        IBaseChannel channel = (mHttpRouter == null ? null : mHttpRouter.getSelectChannel());
        if (channel != null) {
            return transmitHttpChannel(channel, param, post);
        }
        return onExecute(param, post);
    }

    private Rsp transmitHttpChannel(IBaseChannel channel, Param param, Post post) {
        HttpParamWrap paramWrap = onWrapParam(param);
        HttpPostWrap postWrap = onWrapPost(post);
        HttpResponseWrap httpResponseWrap = channel.transmit(paramWrap, postWrap);
        return onUnWrapRsp(httpResponseWrap);
    }

    protected abstract Rsp onExecute(Param param, Post post);

    protected abstract HttpParamWrap onWrapParam(Param param);

    protected abstract HttpPostWrap onWrapPost(Post post);

    protected abstract Rsp onUnWrapRsp(HttpResponseWrap responseWrap);
}
