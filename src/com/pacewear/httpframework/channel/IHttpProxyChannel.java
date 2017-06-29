
package com.pacewear.httpframework.channel;

import com.pacewear.httpframework.core.IHttpRequestAction;

public interface IHttpProxyChannel<Rsp, Param, Post> {
    public Rsp transmit(Param param, Post post);

    public void setClient(IHttpRequestAction<Rsp, Param, Post> action);

}
