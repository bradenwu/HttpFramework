
package com.pace.httpframework.bthttp;

import com.pace.httpframework.bean.HttpParamWrap;
import com.pace.httpframework.bean.HttpPostWrap;
import com.pace.httpframework.bean.HttpResponseWrap;
import com.pace.httpframework.core.IHttpClient;

import org.apache.http.HttpResponse;

public class BtHttpClient implements IHttpClient<Object> {

    @Override
    public HttpResponseWrap execute(HttpParamWrap param, HttpPostWrap post) {
        // TODO Auto-generated method stub
        HttpResponse response = null;
        return null;
    }

    @Override
    public Object getResponse() {
        // TODO Auto-generated method stub
        return null;
    }

}
