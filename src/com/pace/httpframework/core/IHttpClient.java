
package com.pace.httpframework.core;

import com.pace.httpframework.bean.HttpParamWrap;
import com.pace.httpframework.bean.HttpPostWrap;

public interface IHttpClient<T> {
    public T execute(HttpParamWrap param, HttpPostWrap post);
}
