
package com.pacewear.httpframework.okhttp.bean;

import com.pacewear.httpframework.common.ByteUtil;

import java.io.Serializable;

public class OkHttpParam implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private OkHttpClientBuilder mClientSetting;
    private RequestBuilder mRequestBuilder;
    private ResponseBuilder mResponseBuilder;

    public OkHttpParam() {

    }

    public OkHttpParam(OkHttpClientBuilder builder, RequestBuilder requestbuilder,
            ResponseBuilder rspbuilder) {
        mClientSetting = builder;
        mRequestBuilder = requestbuilder;
        mResponseBuilder = rspbuilder;
    }

    public void setOkHttpClientBuilder(OkHttpClientBuilder builder) {
        mClientSetting = builder;
    }

    public void setRequestBuilder(RequestBuilder builder) {
        mRequestBuilder = builder;
    }

    public void setResponseBuilder(ResponseBuilder builder) {
        mResponseBuilder = builder;
    }

    public RequestBuilder getRequestBuilder() {
        return mRequestBuilder;
    }

    public OkHttpClientBuilder getOkHttpClientBuilder() {
        return mClientSetting;
    }

    public ResponseBuilder getResponseBuilder() {
        return mResponseBuilder;
    }

    public static String Param2Str(OkHttpParam param) {
        return ByteUtil.Obj2Str(param);
    }

    public static OkHttpParam Str2Param(String str) {
        Object object = ByteUtil.Str2Obj(str);
        if (object == null) {
            return null;
        }
        return (OkHttpParam) object;
    }

}
