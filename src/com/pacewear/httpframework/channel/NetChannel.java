
package com.pacewear.httpframework.channel;

import com.pacewear.httpframework.core.IHttpRequestAction;

public class NetChannel<Rsp, Param, Post> implements IHttpProxyChannel<Rsp, Param, Post> {
    private IHttpRequestAction<Rsp, Param, Post> mClientAction = null;

    @Override
    public Rsp transmit(Param param, Post post) {
        if (mClientAction == null) {
            return null;
        }
        return mClientAction.onExecute(param, post);
    }

    @Override
    public void setClient(IHttpRequestAction<Rsp, Param, Post> action) {
        mClientAction = action;
    }

}
