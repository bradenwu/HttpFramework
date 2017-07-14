
package com.pacewear.httpframework.okhttp.bean;

import android.text.TextUtils;

import com.pacewear.httpframework.okhttp.https.SSLModel;
import com.pacewear.httpframework.okhttp.https.SSLParcelParam;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientBuilder implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;
    private SSLParcelParam sslParcelParam;
    private String proxy_url;
    private int port;

    public void setWriteTimeout(long timeout) {
        writeTimeOut = timeout;
    }

    public void setReadTimeout(long timeout) {
        readTimeOut = timeout;
    }

    public void setConnectTimeout(long timeout) {
        connTimeOut = timeout;
    }

    public void setSslParcelParam(SSLParcelParam sslParcelParam) {
        this.sslParcelParam = sslParcelParam;
    }

    public void setProxy(String url, int port) {
        proxy_url = url;
        this.port = port;
    }
    // @Override
    // public int describeContents() {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    // // TODO Auto-generated method stub
    //
    // }

    // 反序列化
    public static OkHttpClient.Builder toOkHttpClient_Builder(OkHttpClientBuilder srClientBuilder) {
        OkHttpClient.Builder targetBuilder = new OkHttpClient.Builder();
        if (srClientBuilder.connTimeOut > 0) {
            targetBuilder.connectTimeout(srClientBuilder.connTimeOut, TimeUnit.MILLISECONDS);
        }
        if (srClientBuilder.readTimeOut > 0) {
            targetBuilder.readTimeout(srClientBuilder.readTimeOut, TimeUnit.MILLISECONDS);
        }
        if (srClientBuilder.writeTimeOut > 0) {
            targetBuilder.writeTimeout(srClientBuilder.writeTimeOut, TimeUnit.MILLISECONDS);
        }
        SSLParcelParam _sslParcelParam = srClientBuilder.sslParcelParam;
        if (_sslParcelParam != null) {
            SSLModel.SSLParams sslParam = SSLModel.getSslSocketFactory(_sslParcelParam);
            targetBuilder.sslSocketFactory(sslParam.sSLSocketFactory, sslParam.trustManager);
        }
        if (!TextUtils.isEmpty(srClientBuilder.proxy_url) && srClientBuilder.port > 0) {
            targetBuilder.proxy(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(srClientBuilder.proxy_url, srClientBuilder.port)));
        }
        return targetBuilder;
    }
}
