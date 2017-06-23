
package com.pacewear.httpframework.apachehttp;

import com.pacewear.httpframework.core.BaseHttpClient;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ApacheHttpClientImpl
        extends BaseHttpClient<HttpResponse, HttpParams, HttpPost> {

    @Override
    public HttpResponse onExecute(HttpParams _param, HttpPost _post) {
        HttpClient httpClient = new DefaultHttpClient(_param);
        try {
            return httpClient.execute(_post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected HttpRequestGeneralParams prepareRequest(HttpParams param, HttpPost request) {
        // TODO Auto-generated method stub
        HttpRequestGeneralParams target = new HttpRequestGeneralParams();
        target.addUrl(request.getURI().toString());
        int timout = HttpConnectionParams.getConnectionTimeout(param);
        if (timout > 0) {
            target.setRequestTimeOut(timout);
        }
        // int sotimeout = HttpConnectionParams.getSoTimeout(param);
        // if (sotimeout > 0) {
        // httpParamWrap.setSoTimeout(sotimeout);
        // }
        HttpHost httpHost = (HttpHost) param.getParameter(ConnRoutePNames.DEFAULT_PROXY);
        if (httpHost != null) {
            // TODO 此处需要设置代理，扩充HttpRequestGeneralParams！！！
            httpParamWrap.setHostName(httpHost.getHostName());
            httpParamWrap.setHostPort(httpHost.getPort());
        }
        return target;
    }

    @Override
    protected HttpResponse parseResponse(HttpResponseResult rsp) {
        // TODO Auto-generated method stub
        return null;
    }

    // @Override
    // protected HttpParamWrap onWrapParam(HttpParams param) {
    // HttpParamWrap httpParamWrap = new HttpParamWrap();
    // int timout = HttpConnectionParams.getConnectionTimeout(param);
    // if (timout > 0) {
    // httpParamWrap.setConnectionTimeout(timout);
    // }
    // int sotimeout = HttpConnectionParams.getSoTimeout(param);
    // if (sotimeout > 0) {
    // httpParamWrap.setSoTimeout(sotimeout);
    // }
    // HttpHost httpHost = (HttpHost) param.getParameter(ConnRoutePNames.DEFAULT_PROXY);
    // if (httpHost != null) {
    // httpParamWrap.setHostName(httpHost.getHostName());
    // httpParamWrap.setHostPort(httpHost.getPort());
    // }
    // return httpParamWrap;
    // }

    // @Override
    // protected HttpPostWrap onWrapPost(HttpPost post) {
    // String url = post.getURI().toString();
    // HttpPostWrap postWrap = new HttpPostWrap();
    // Header[] headers = post.getAllHeaders();
    // for (Header header : headers) {
    // postWrap.addHeader(header.getName(), header.getValue());
    // }
    // try {
    // byte[] body = EntityUtils.toByteArray(post.getEntity());
    // postWrap.setBody(body);
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return postWrap;
    // }
    //
    // @Override
    // protected HttpResponse onUnWrapRsp(HttpResponseWrap responseWrap) {
    // return new HttpResponseProxy(responseWrap);
    // }

}
