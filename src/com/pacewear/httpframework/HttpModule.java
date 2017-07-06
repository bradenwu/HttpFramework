
package com.pacewear.httpframework;

import android.content.Context;
import android.util.Log;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.channel.NetChannel;
import com.pacewear.httpframework.common.Constants;
import com.pacewear.httpframework.common.ThreadExecutors;
import com.pacewear.httpframework.core.IHttpClient;
import com.pacewear.httpframework.okhttp.OkHttpClientImpl;
import com.pacewear.httpframework.okhttp.OkHttpParser;
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
                IHttpClient<Response, OkHttpClient.Builder, Request> okHttpClient = new OkHttpClientImpl(
                        context, directInvoke);
                OkHttpClient.Builder builder = OkHttpParser.getClientBuilderFromPacket(source);
                Request request = OkHttpParser.getRequestFromPacket(source);
                Response response = okHttpClient.execute(builder, request);
                OkHttpParser.onParseResponse(response, source);
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
