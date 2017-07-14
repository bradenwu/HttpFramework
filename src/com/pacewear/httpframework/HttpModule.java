
package com.pacewear.httpframework;

import android.content.Context;
import android.util.Log;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.channel.NetChannel;
import com.pacewear.httpframework.common.Constants;
import com.pacewear.httpframework.common.ThreadExecutors;
import com.pacewear.httpframework.core.IHttpClient;
import com.pacewear.httpframework.okhttp.OkHttpClientImp;
import com.pacewear.httpframework.okhttp.OkHttpRequestConvert;
import com.pacewear.httpframework.okhttp.OkHttpResponseConvert;
import com.pacewear.httpframework.okhttp.bean.OkHttpClientBuilder;
import com.pacewear.httpframework.okhttp.bean.RequestBuilder;
import com.pacewear.httpframework.okhttp.bean.ResponseBuilder;
import com.pacewear.httpframework.route.HttpRouter;
import com.tencent.tws.api.HttpPackage;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpModule {
    public static interface IHttpInvokeCallback {
        void onCallback(HttpPackage resultData);
    }

    public static void invokeHttp(final Context context, final HttpPackage source,
            final boolean directInvoke,
            final IHttpInvokeCallback callback) {
        Log.d(Constants.TAG, "invoke Http Now:directInvoke:" + directInvoke);
        ThreadExecutors.background().execute(new Runnable() {

            @Override
            public void run() {
                // 走蓝牙通道时候，或者httpMananger的独立通道时，核心入口
                // TODO 抽出来 做适配
                IHttpClient<Response, OkHttpClientBuilder, RequestBuilder> okHttpClient = new OkHttpClientImp(
                        context, directInvoke);
                OkHttpClientBuilder clientSetting = OkHttpRequestConvert
                        .getClientBuilderFromPacket(source);
                RequestBuilder request = OkHttpRequestConvert.getRequestFromPacket(source);
                Response response = okHttpClient.execute(clientSetting, request);
                OkHttpResponseConvert.onParseResponse(response, source);
                callback.onCallback(source);
            }
        });
    }

    public static void invokeHttp(final Context context, final HttpPackage source,
            final IHttpInvokeCallback callback) {
        invokeHttp(context, source, false, callback);
    }

    public static boolean isNetChannelAvailble(Context context) {
        IHttpProxyChannel<Response, OkHttpClient.Builder, Request> channel = HttpRouter
                .get().<Response, OkHttpClient.Builder, Request> getSelectChannel(context);
        if (channel instanceof NetChannel) {
            return true;
        }
        return false;
    }
}
