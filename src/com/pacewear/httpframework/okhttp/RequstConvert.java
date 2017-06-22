
package com.pacewear.httpframework.okhttp;

import android.text.TextUtils;
import android.util.Log;

import com.pacewear.httpframework.log.HttpLog;
import com.tencent.tws.api.HttpRequestGeneralParams;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpParams;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequstConvert {
    private static final String TAG = "ParamConvert";

    public static void parseHttpClientParam(OkHttpClient.Builder builder, HttpParams params) {
        HttpHost httpHost = (HttpHost) params.getParameter(ConnRoutePNames.DEFAULT_PROXY);
        if (httpHost != null) {
            builder.proxy(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(httpHost.getHostName(), httpHost.getPort())));
        }

    }

    public static void parseHttpParam(OkHttpClient.Builder builder,
            HttpRequestGeneralParams _params,
            int _maskFlag) {

        if ((_maskFlag & HttpRequestGeneralParams.CONTROL_TIMEOUT) != 0) {
            builder.connectTimeout(_params.requestTimeOut, TimeUnit.MILLISECONDS);
            Log.d(TAG, "load requestTimeout +" + _params.requestTimeOut);
        } else {
            // default 6s timeout
            builder.connectTimeout(6000, TimeUnit.MILLISECONDS);
        }

        // cache-timeout, not support
        // if ((_maskFlag & HttpRequestGeneralParams.CONTROL_CACHETIMEOUT) != 0) {
        // builder.readTimeout(timeout, unit)
        // mHttpUtils.configCurrentHttpCacheExpiry(mParams.cacheTimeOut);
        // Log.d(TAG, "load cacheTimeOut +" + mParams.cacheTimeOut);
        // } else {
        // mHttpUtils.configCurrentHttpCacheExpiry(0);// 0 represent not cache
        // }

        // if ((mMaskFlag & HttpRequestGeneralParams.CONTROL_USERAGENT) != 0) {
        // mHttpUtils.configUserAgent(mParams.UserAgent);
        // Log.d(TAG, "load UserAgnet +" + mParams.UserAgent);
        // }

    }

    public static void parseHttpRequest(Request.Builder builder, HttpRequestGeneralParams _params,
            int _maskFlag) {
        String url = _params.URl;
        builder.url(url);
        // 0.set user-agent
        builder.removeHeader(OkHttpConstants.USER_AGENT).addHeader(OkHttpConstants.USER_AGENT,
                _params.UserAgent);
        // 1.add head
        if (!_params.getHeader().isEmpty()) {
            for (Map.Entry<String, String> entry : _params.getHeader()
                    .entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
                HttpLog.i(TAG, "head (" + entry.getKey() + "," + entry.getValue()
                        + ")");

            } // add header
        }
        if ((_maskFlag & HttpRequestGeneralParams.BODYMASK) != 0) {
            // if has body
            if ((_maskFlag & HttpRequestGeneralParams.BODYPART_WITHENTITY) != 0) {
                // add body entity
                if (TextUtils.isEmpty(_params.mBodyEntityStringEncoding)) {
                    builder.post(RequestBody.create(OkHttpConstants.JSON, _params.mBodyEntity));
                } else {
                    // TODO 是否需要考虑带有mBodyEntityStringEncoding
                    // params.setBodyEntity(new StringEntity(
                    // _params.mBodyEntity,
                    // _params.mBodyEntityStringEncoding));
                    Log.i(TAG, "we have body entry,now give value: "
                            + _params.mBodyEntity + " , encoding: "
                            + _params.mBodyEntity);
                }
            } // if
            else {// add body key-value
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                for (Map.Entry<String, String> entry : _params
                        .getBodyParamete().entrySet()) {
                    bodyBuilder.add(entry.getKey(), entry.getValue());
                    Log.i(TAG,
                            "body (" + entry.getKey() + "," + entry.getValue()
                                    + ")");
                } // add body
                builder.post(bodyBuilder.build());
            }
        }
    }
}
