
package com.pacewear.httpframework.httpframework.channel;

import com.pacewear.httpframework.httpframework.bean.HttpParamWrap;
import com.pacewear.httpframework.httpframework.bean.HttpPostWrap;
import com.pacewear.httpframework.httpframework.bean.HttpResponseWrap;

public interface IBaseChannel {
    public HttpResponseWrap transmit(HttpParamWrap paramWrap, HttpPostWrap postWrap);
}
