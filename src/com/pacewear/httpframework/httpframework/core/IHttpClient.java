
package com.pacewear.httpframework.httpframework.core;

public interface IHttpClient<Rsp, Param, Post> {
    public Rsp execute(Param param, Post post);
}
