
package com.pacewear.httpframework.okhttp;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.pacewear.httpframework.common.Constants;
import com.pacewear.httpframework.log.HttpLog;
import com.pacewear.httpframework.okhttp.bean.OkHttpClientBuilder;
import com.pacewear.httpframework.okhttp.bean.OkHttpParam;
import com.pacewear.httpframework.okhttp.bean.RequestBuilder;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpRequestGeneralParams;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpRequestConvert {
    private static final String TAG = "ParamConvert";

    public static OkHttpParam getOkHttpParamFromPacket(HttpPackage e1) {
        HttpRequestGeneralParams srcParam = HttpRequestGeneralParams
                .getOkHttpParam(e1.getHttpData());
        if (srcParam == null) {
            return null;
        }
        String srcConent = srcParam.mBodyEntity;
        return OkHttpParam.Str2Param(srcConent);
    }

    public static RequestBuilder getOkHttpRequestFromPacket(HttpPackage e1) {
        OkHttpParam srcParam = getOkHttpParamFromPacket(e1);
        if (srcParam == null) {
            return null;
        }
        return srcParam.getRequestBuilder();
    }

    public static OkHttpClientBuilder getOkHttpClientBuilderFromPacket(HttpPackage e1) {
        OkHttpParam srcParam = getOkHttpParamFromPacket(e1);
        if (srcParam == null) {
            return null;
        }
        return srcParam.getOkHttpClientBuilder();
        // return OkHttpClientBuilder.toOkHttpClient_Builder(clietntBuilder);
    }

    public static OkHttpClientBuilder getClientBuilderFromPacket(HttpPackage e1) {
        Log.d(Constants.TAG, "getClientBuilderFromPacket");
        if (e1.getType() == HttpRequestCommand.TRANSMIT_OKHTTP) {
            return getOkHttpClientBuilderFromPacket(e1);
        }
        OkHttpClientBuilder builder = new OkHttpClientBuilder();
        HttpRequestGeneralParams param = HttpRequestGeneralParams
                .StringToHttpRequestGeneralParams(e1.getHttpData());
        OkHttpRequestConvert.parseHttpParam(builder, param, param.mMaskFlag);
        return builder;
    }

    public static RequestBuilder getRequestFromPacket(HttpPackage e1) {
        Log.d(Constants.TAG, "getRequestFromPacket,type:"+e1.getType());
        if (e1.getType() == HttpRequestCommand.TRANSMIT_OKHTTP) {
            return getOkHttpRequestFromPacket(e1);
        }
        Request.Builder requestBuilder = new Request.Builder();
        OkHttpRequestConvert.parseHttpRequest(requestBuilder, e1);
        return new RequestBuilder(requestBuilder.build());
    }

    public static void parseHttpParam(OkHttpClientBuilder builder,
            HttpRequestGeneralParams _params,
            int _maskFlag) {

        if ((_maskFlag & HttpRequestGeneralParams.CONTROL_TIMEOUT) != 0) {
            builder.setConnectTimeout(_params.requestTimeOut);
            Log.d(TAG, "load requestTimeout +" + _params.requestTimeOut);
        } else {
            // default 6s timeout
            builder.setConnectTimeout(6000);
        }

        if ((_maskFlag & HttpRequestGeneralParams.CONTROL_PROXY) != 0) {
            HashMap<String, String> headers = _params.getHeader();
            String url = headers.get(HttpRequestGeneralParams.HEADER_PROXY_URL);
            String sport = headers.get(HttpRequestGeneralParams.HEADER_PROXY_PORT);
            if (!TextUtils.isEmpty(sport) && TextUtils.isDigitsOnly(sport)) {
                int port = Integer.parseInt(sport);
                builder.setProxy(url, port);
            }
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

    public static void parseHttpRequest(Request.Builder builder, HttpPackage pack) {
        HttpRequestGeneralParams _params = HttpRequestGeneralParams
                .StringToHttpRequestGeneralParams(pack.getHttpData());
        int _maskFlag = _params.mMaskFlag;
        String url = _params.URl;
        builder.url(url);
        // 0.set user-agent
        if (!TextUtils.isEmpty(_params.UserAgent)) {
            builder.removeHeader(OkHttpConstants.USER_AGENT).addHeader(OkHttpConstants.USER_AGENT,
                    _params.UserAgent);
        }
        // 1.add head
        String key = null;
        String val = null;
        if (!_params.getHeader().isEmpty()) {
            for (Map.Entry<String, String> entry : _params.getHeader()
                    .entrySet()) {
                entry.getKey();
                key = entry.getKey();
                val = entry.getValue();
                if (HttpRequestGeneralParams.HEADER_PROXY_URL.equalsIgnoreCase(key)
                        || HttpRequestGeneralParams.HEADER_PROXY_PORT.equalsIgnoreCase(key)) {
                    continue;
                }
                builder.addHeader(key, val);
                HttpLog.i(TAG, "head (" + key + "," + val
                        + ")");

            } // add header
        }
        if ((_maskFlag & HttpRequestGeneralParams.BODYMASK) != 0) {
            // if has body
            if ((_maskFlag & HttpRequestGeneralParams.BODYPART_WITHENTITY) != 0) {
                // add body entity
                if (TextUtils.isEmpty(_params.mBodyEntityStringEncoding)) {
                    // builder.post(RequestBody.create(OkHttpConstants.JSON, _params.mBodyEntity));
                    builder.post(fillBodyEntityByType(pack.getType(), _params.mBodyEntity));
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

    private static RequestBody fillBodyEntityByType(int type, String strBody) {
        RequestBody body = null;
        MediaType target = OkHttpConstants.TEXT;
        switch (type) {
            case HttpRequestCommand.GET_WITH_STREAMRETURN:
            case HttpRequestCommand.POST_WITH_STRAMRETURN:
            case HttpRequestCommand.GET_WITH_GENERAL_FILE:
            case HttpRequestCommand.GET_PNG_IMAGE:
                Log.d(Constants.TAG, "post/get stream return");
                target = OkHttpConstants.STREAM;
                body = RequestBody.create(target, Base64.decode(strBody, Base64.DEFAULT));
                break;
            case HttpRequestCommand.GET_TEXT:
            case HttpRequestCommand.POST:
            case HttpRequestCommand.GET_WITH_GENERAL_TEXT:
            case HttpRequestCommand.POST_WITH_GENERAL:
                Log.d(Constants.TAG, "get text");
                target = OkHttpConstants.JSON;
                body = RequestBody.create(target, strBody);
                break;
            default:
                body = RequestBody.create(target, strBody);
                break;
        }
        return body;
    }
}
