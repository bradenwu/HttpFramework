
package com.pacewear.httpframework.okhttp;

import android.content.Context;
import android.util.Log;

import com.pacewear.httpframework.common.Constants;
import com.pacewear.httpframework.core.BaseHttpClient;
import com.pacewear.httpframework.okhttp.bean.OkHttpClientBuilder;
import com.pacewear.httpframework.okhttp.bean.OkHttpParam;
import com.pacewear.httpframework.okhttp.bean.RequestBuilder;
import com.pacewear.httpframework.okhttp.bean.ResponseBuilder;
import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientImp
        extends BaseHttpClient<Response, OkHttpClientBuilder, RequestBuilder> {
    private static OkHttpClient mClient = null;
    public OkHttpClientImp(Context context) {
        super(context);
    }

    public OkHttpClientImp(Context context, boolean invokeDirect) {
        super(context);
        setInvokeDirect(invokeDirect);
    }

    @Override
    public Response parseResponse(HttpResponseResult rsp) {
        String data = rsp.mData;
        OkHttpParam param = OkHttpParam.Str2Param(data);
        if (param == null) {
            return null;
        }
        ResponseBuilder responseBuilder = param.getResponseBuilder();
        return ResponseBuilder.toResponse(responseBuilder);
    }

    @Override
    public Response onExecute(OkHttpClientBuilder _param, RequestBuilder _post) {
        Log.d(Constants.TAG, "OkHttpClientImpl execute..");
        OkHttpClient.Builder clientBuilder = OkHttpClientBuilder.toOkHttpClient_Builder(_param);
        Request request = RequestBuilder.toRequest_Builder(_post);
        Response response = null;
        try {
            mClient = clientBuilder.build();
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(Constants.TAG, "client execute error!");
            e.printStackTrace();
        }
        if(response != null){
            Log.d(Constants.TAG, "response code:" + response.code());
        }
        Log.e(Constants.TAG, "client execute return null!!!");
        return response;
    }

    @Override
    public HttpRequestGeneralParams prepareRequest(OkHttpClientBuilder param,
            RequestBuilder request) {
        OkHttpParam targeParam = new OkHttpParam();
        targeParam.setOkHttpClientBuilder(param);
        targeParam.setRequestBuilder(request);
        String paramContent = OkHttpParam.Param2Str(targeParam);
        HttpRequestGeneralParams httpRequestGeneralParams = new HttpRequestGeneralParams(
                paramContent, HttpRequestCommand.TRANSMIT_OKHTTP);
        return httpRequestGeneralParams;
    }

}
