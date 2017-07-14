
package com.pacewear.httpframework.apachehttp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.pacewear.httpframework.common.ByteUtil;
import com.pacewear.httpframework.core.BaseHttpClient;
import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ApacheHttpClientImpl
        extends BaseHttpClient<HttpResponse, HttpParams, HttpPost> {

    public ApacheHttpClientImpl(Context context) {
        super(context);
    }

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
    public HttpRequestGeneralParams prepareRequest(HttpParams param, HttpPost request) {
        // TODO Auto-generated method stub
        HttpRequestGeneralParams target = new HttpRequestGeneralParams();
        target.setRequestType(HttpRequestCommand.POST_WITH_STRAMRETURN);
        target.addUrl(request.getURI().toString());
        int timout = HttpConnectionParams.getConnectionTimeout(param);
        if (timout > 0) {
            target.setRequestTimeOut(timout);
        }
        // int sotimeout = HttpConnectionParams.getSoTimeout(param);
        // if (sotimeout > 0) {
        // httpParamWrap.setSoTimeout(sotimeout);
        // }
        // 设置代理
        HttpHost httpHost = (HttpHost) param.getParameter(ConnRoutePNames.DEFAULT_PROXY);
        if (httpHost != null) {
            // TODO 此处需要设置代理，扩充HttpRequestGeneralParams！！！
            target.setProxy(httpHost.getHostName(), httpHost.getPort());
        }
        // 设置请求头
        Header[] headers = request.getAllHeaders();
        for (Header header : headers) {
            if (TextUtils.equals("Host", header.getName())) {
                String val = header.getValue();
                int indexDot = val.lastIndexOf(":");
                String hostName = val.substring(0, indexDot);
                String port = val.substring(indexDot + 1, val.length());
                if (TextUtils.isDigitsOnly(port)) {
                    target.setProxy(hostName, Integer.parseInt(port));
                }
            } else {
                target.addHeader(header.getName(), header.getValue());
            }
        }
        target.addHeader("Connection", "close");// okhttp此次设置Connection为close
        HttpEntity entity = request.getEntity();
        try {
            if (entity instanceof ByteArrayEntity) {
                byte[] body = EntityUtils.toByteArray(entity);
                // StringEntity or ByteArrayEntitiy
                target.addBodyEntity(Base64.encodeToString(body, Base64.DEFAULT));
            } else if (entity instanceof StringEntity) {
                // TODO 这里需要解析StringEntity
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return target;
    }

    @Override
    public HttpResponse parseResponse(HttpResponseResult rsp) {
        return new HttpResponseProxy(rsp);
    }

}
