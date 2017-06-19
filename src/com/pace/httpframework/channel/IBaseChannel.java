
package com.pace.httpframework.channel;

import com.pace.httpframework.bean.HttpParamWrap;
import com.pace.httpframework.bean.HttpPostWrap;
import com.pace.httpframework.bean.HttpResponseWrap;

public interface IBaseChannel {
    public HttpResponseWrap transmit(HttpParamWrap paramWrap, HttpPostWrap postWrap);
}
