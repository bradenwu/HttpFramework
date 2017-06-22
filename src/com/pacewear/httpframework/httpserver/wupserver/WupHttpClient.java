
package com.pacewear.httpframework.httpserver.wupserver;

import com.pacewear.httpframework.apachehttp.ApacheHttpClientImpl;
import com.pacewear.httpframework.core.IHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpParams;

import qrom.component.wup.apiv2.IApacheHttpClient;

public class WupHttpClient implements IApacheHttpClient {

    @Override
    public HttpResponse execute(HttpParams arg0, HttpPost arg1) {
        // TODO Auto-generated method stub
        IHttpClient<HttpResponse, HttpParams, HttpPost> httpClient = new ApacheHttpClientImpl();
        return httpClient.execute(arg0, arg1);
    }
}
