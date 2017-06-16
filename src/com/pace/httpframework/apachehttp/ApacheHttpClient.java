
package com.pace.httpframework.apachehttp;

import com.pace.httpframework.bean.HttpParamWrap;
import com.pace.httpframework.bean.HttpPostWrap;
import com.pace.httpframework.bean.HttpResponseWrap;
import com.pace.httpframework.core.IHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

public class ApacheHttpClient implements IHttpClient<HttpResponse> {

    @Override
    public HttpResponse execute(HttpParamWrap _param, HttpPostWrap _post) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, (int) _param.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(params, (int) _param.getConnectionTimeout());
        HttpClient httpClient = new DefaultHttpClient(params);
        HttpPost post = null; // TODO
        try {
            return httpClient.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
