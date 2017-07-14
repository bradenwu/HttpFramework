
package com.pacewear.httpframework.okhttp.bean;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResponseBuilder implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RequestBuilder requestBuilder;
    private int code;
    private HeadersBuilder headersBuilder;
    private ResponseBodyWrap responseBodyWrap;
    private String msg;
    long sentRequestAtMillis;
    long receivedResponseAtMillis;
    private ProtocolBuilder protocolBuilder;

    public ResponseBuilder(Response rsp) {
        requestBuilder = new RequestBuilder(rsp.request());
        code = rsp.code();
        headersBuilder = new HeadersBuilder(rsp.headers());
        ResponseBody body = rsp.body();
        try {
            responseBodyWrap = new ResponseBodyWrap(body.bytes(), body.contentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg = rsp.message();
        sentRequestAtMillis = rsp.sentRequestAtMillis();
        receivedResponseAtMillis = rsp.receivedResponseAtMillis();
        protocolBuilder = new ProtocolBuilder(rsp.protocol());
    }
    // @Override
    // public int describeContents() {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    // dest.writeParcelable(requestBuilder, flags);
    // dest.writeInt(code);
    // dest.writeParcelable(responseBodyWrap, flags);
    // dest.writeParcelable(headersBuilder, flags);
    // dest.writeString(msg);
    // dest.writeLong(sentRequestAtMillis);
    // dest.writeLong(receivedResponseAtMillis);
    // }

    // public static final Parcelable.Creator<ResponseBuilder> CREATOR = new
    // Parcelable.Creator<ResponseBuilder>() {
    //
    // @Override
    // public ResponseBuilder createFromParcel(Parcel source) {
    // ResponseBuilder rWrap = new ResponseBuilder();
    // rWrap.requestBuilder = source.readParcelable(RequestBuilder.class.getClassLoader());
    // rWrap.code = source.readInt();
    // rWrap.responseBodyWrap = source.readParcelable(ResponseBodyWrap.class.getClassLoader());
    // rWrap.msg = source.readString();
    // rWrap.sentRequestAtMillis = source.readLong();
    // rWrap.receivedResponseAtMillis = source.readLong();
    // return rWrap;
    // }
    //
    // @Override
    // public ResponseBuilder[] newArray(int size) {
    // return new ResponseBuilder[size];
    // }
    // };

    public static Response toResponse(ResponseBuilder srcBuilder) {
        Response.Builder targetBuilder = new Response.Builder();
        Request request = RequestBuilder.toRequest_Builder(srcBuilder.requestBuilder);
        targetBuilder.request(request);
        // add procto version TODO
        targetBuilder.code(srcBuilder.code);
        targetBuilder.message(srcBuilder.msg);
        targetBuilder.headers(HeadersBuilder.toHeaders_Builder(srcBuilder.headersBuilder));
        targetBuilder.body(ResponseBodyWrap.toRequestBody(srcBuilder.responseBodyWrap));
        targetBuilder.protocol(srcBuilder.protocolBuilder.toProtocol());
        return targetBuilder.build();
    }
}
