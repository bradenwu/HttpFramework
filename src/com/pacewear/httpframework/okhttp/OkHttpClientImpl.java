
package com.pacewear.httpframework.okhttp;

import android.content.Context;

import com.pacewear.httpframework.core.BaseHttpClient;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

// okhttp走蓝牙通道时候的处理
public class OkHttpClientImpl extends BaseHttpClient<Response, OkHttpClient.Builder, Request> {

    public OkHttpClientImpl(Context context) {
        super(context);
    }

    @Override
    public Response onExecute(Builder param, Request post) {
        OkHttpClient client = param.build();
        try {
            return client.newCall(post).execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // http->bt
    @Override
    public HttpRequestGeneralParams prepareRequest(Builder param, Request request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response parseResponse(HttpResponseResult rsp) {
        // TODO Auto-generated method stub
        return null;
    }

}
