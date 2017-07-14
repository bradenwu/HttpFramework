
package com.pacewear.httpframework.okhttp.bean;

import com.pacewear.httpframework.okhttp.NewOkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class RequestBodyWrap implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private byte[] mReqBody;
    private String mMediaType;

    public RequestBodyWrap(byte[] reqBody, MediaType mediaType) {
        mReqBody = reqBody;
        mMediaType = mediaType.toString();
    }

    public RequestBodyWrap(byte[] reqBody, String mediaType) {
        mReqBody = reqBody;
        mMediaType = mediaType;
    }

    public RequestBodyWrap() {
    }

    public byte[] getContent() {
        return mReqBody;
    }

    public String getContentType() {
        return mMediaType;
    }

    public RequestBodyWrap(RequestBody body) {
        mReqBody = getByteArrayFromRequestBody(body);// genRequestBodyByteArray(body);
        mMediaType = (body != null) ? body.contentType().toString() : "";
    }

    // private byte[] genRequestBodyByteArray(RequestBody body) {
    // String fileName = saveRequestBody(body);
    // return getRequestBody(fileName);
    // }
    //
    // private byte[] getRequestBody(String fileName) {
    // File tmpFile = new File(fileName);
    // if (!tmpFile.exists()) {
    // return null;
    // }
    // BufferedSource bufferedSource = null;
    // try {
    // Source source = Okio.source(tmpFile);
    // bufferedSource = Okio.buffer(source);
    // return bufferedSource.readByteArray();
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    private byte[] getByteArrayFromRequestBody(RequestBody body) {
        if (body == null) {
            return null;
        }
        OutputStream bos = new ByteArrayOutputStream();
        BufferedSink bufferedSink = null;
        Sink sink = Okio.sink(bos);
        bufferedSink = Okio.buffer(sink);
        byte[] targetByteArray = null;
        try {
            body.writeTo(bufferedSink);
            targetByteArray = bufferedSink.buffer().readByteArray();
            bufferedSink.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetByteArray;
    }

    private String saveRequestBody(RequestBody body) {
        long curTime = System.currentTimeMillis();
        File tmpFile = new File(NewOkHttpClient.getContext().getCacheDir(), curTime + ".txt");
        BufferedSink bufferedSink = null;
        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Sink sink = Okio.sink(tmpFile);
            bufferedSink = Okio.buffer(sink);
            body.writeTo(bufferedSink);
            bufferedSink.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpFile.getAbsolutePath();
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
    //
    // public static final Parcelable.Creator<RequestBodyWrap> CREATOR = new
    // Parcelable.Creator<RequestBodyWrap>() {
    //
    // @Override
    // public RequestBodyWrap createFromParcel(Parcel source) {
    // RequestBodyWrap bodyWrap = new RequestBodyWrap();
    // bodyWrap.mReqBody = source.createByteArray();
    // bodyWrap.mMediaType = source.readString();
    // return bodyWrap;
    // }
    //
    // @Override
    // public RequestBodyWrap[] newArray(int size) {
    // return new RequestBodyWrap[size];
    // }
    // };

    // 反序列化
    public static RequestBody toRequestBody(RequestBodyWrap requestBodyWrap) {
        if (requestBodyWrap.mReqBody == null) {
            return null;
        }
        MediaType mediaType = MediaType.parse(requestBodyWrap.mMediaType);
        return RequestBody.create(mediaType, requestBodyWrap.mReqBody);
    }
}
