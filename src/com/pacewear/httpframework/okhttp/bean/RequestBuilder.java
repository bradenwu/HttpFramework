
package com.pacewear.httpframework.okhttp.bean;

import com.pacewear.httpframework.okhttp.NewOkHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class RequestBuilder implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String url;
    private String method;
    private HeadersBuilder headers;
    private RequestBodyWrap body;

    public RequestBuilder(Request request) {
        this.url = request.url().toString();
        this.method = request.method();
        this.headers = new HeadersBuilder(request.headers());
        this.body = new RequestBodyWrap(request.body());
    }

    public RequestBuilder() {
        // TODO Auto-generated constructor stub
    }

    // Object tag;
    // @Override
    // public int describeContents() {
    // return 0;
    // }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public HeadersBuilder getHeadersBuilder() {
        return headers;
    }

    public RequestBodyWrap getRequestBodyWrap() {
        return body;
    }

    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    // dest.writeString(url);
    // dest.writeString(method);
    // dest.writeParcelable(headers, flags);
    // dest.writeParcelable(body, flags);
    // }

    // public static final Parcelable.Creator<RequestBuilder> CREATOR = new
    // Parcelable.Creator<RequestBuilder>() {
    //
    // @Override
    // public RequestBuilder createFromParcel(Parcel source) {
    // RequestBuilder requestBuilder = new RequestBuilder();
    // requestBuilder.url = source.readString();
    // requestBuilder.method = source.readString();
    // requestBuilder.headers = source.readParcelable(HeadersBuilder.class.getClassLoader());
    // requestBuilder.body = source.readParcelable(RequestBodyWrap.class.getClassLoader());
    // return requestBuilder;
    // }
    //
    // @Override
    // public RequestBuilder[] newArray(int size) {
    // return new RequestBuilder[size];
    // }
    // };

    public static Request toRequest_Builder(RequestBuilder srcBuilder) {
        Request.Builder targetBuilder = new Request.Builder();
        targetBuilder.url(srcBuilder.url);
        Headers headers = HeadersBuilder.toHeaders_Builder(srcBuilder.headers);
        targetBuilder.headers(headers);
        if ("GET".equalsIgnoreCase(srcBuilder.getMethod()) || srcBuilder.body == null
                || srcBuilder.body.getContent() == null) {
            targetBuilder.get();
        } else {
            targetBuilder.method(srcBuilder.method, RequestBodyWrap.toRequestBody(srcBuilder.body));
        }
        return targetBuilder.build();
    }
}
