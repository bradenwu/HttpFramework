
package com.pacewear.httpframework.okhttp.bean;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

public class ResponseBodyWrap implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private byte[] mReqBody;
    private String mMediaType;

    public ResponseBodyWrap(byte[] reqBody, MediaType mediaType) {
        mReqBody = reqBody;
        mMediaType = mediaType.toString();
    }

    public ResponseBodyWrap(byte[] reqBody, String mediaType) {
        mReqBody = reqBody;
        mMediaType = mediaType;
    }

    public ResponseBodyWrap() {
    }

    // @Override
    // public int describeContents() {
    // return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    // dest.writeByteArray(mReqBody);
    // dest.writeString(mMediaType);
    // }

    // public static final Parcelable.Creator<ResponseBodyWrap> CREATOR = new
    // Parcelable.Creator<ResponseBodyWrap>() {
    //
    // @Override
    // public ResponseBodyWrap createFromParcel(Parcel source) {
    // ResponseBodyWrap bodyWrap = new ResponseBodyWrap();
    // bodyWrap.mReqBody = source.createByteArray();
    // bodyWrap.mMediaType = source.readString();
    // return bodyWrap;
    // }
    //
    // @Override
    // public ResponseBodyWrap[] newArray(int size) {
    // return new ResponseBodyWrap[size];
    // }
    // };

    public static ResponseBody toRequestBody(ResponseBodyWrap requestBodyWrap) {
        MediaType mediaType = MediaType.parse(requestBodyWrap.mMediaType);
        return ResponseBody.create(mediaType, requestBodyWrap.mReqBody);
    }
}
