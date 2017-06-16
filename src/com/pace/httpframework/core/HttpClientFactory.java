
package com.pace.httpframework.core;

import com.pace.httpframework.apachehttp.ApacheHttpClient;
import com.pace.httpframework.bean.HttpParamWrap;
import com.pace.httpframework.bean.HttpPostWrap;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class HttpClientFactory {
    private static HttpClientFactory sInstance = null;

    public static HttpClientFactory getInstance() {
        return sInstance;
    }

    public <T> T invoke(HttpParamWrap paramWrap, HttpPostWrap postWrap) {
        IHttpClient<T> client = findHttpClient();
        return client.execute(paramWrap, postWrap);
    }

    private <T> IHttpClient<T> findHttpClient() {
        return (IHttpClient<T>) new ApacheHttpClient();
    }
}
