
package com.pacewear.httpframework;

import android.content.Context;

import com.pacewear.httpframework.core.IHttpClient;
import com.pacewear.httpframework.okhttp.OkHttpClientImpl;
import com.pacewear.httpframework.okhttp.OkHttpParser;
import com.pacewear.httpframework.route.IHttpRouter;
import com.tencent.tws.api.HttpPackage;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpModule {
    private IHttpRouter mHttpRouter = null;

    public boolean invokeHttp(Context context, HttpPackage source) {
        IHttpClient<Response, OkHttpClient.Builder, Request> okHttpClient = new OkHttpClientImpl(
                context);
        OkHttpClient.Builder builder = OkHttpParser.getClientBuilderFromPacket(source);
        Request request = OkHttpParser.getRequestFromPacket(source);
        Response response = okHttpClient.execute(builder, request);
        OkHttpParser.onParseResponse(response, source);
        return true;
    }
}
