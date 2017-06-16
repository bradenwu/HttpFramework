
package com.pace.httpframework.bean;

import java.io.Serializable;
import java.util.HashMap;

public class HttpResponseWrap<T> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int statusCode = 0;
    private HashMap<String, String> mHeaders = null;
    private byte[] mBody = null;
    private T mResponse = null;

    public HttpResponseWrap(T t) {
        mResponse = t;
    }

    public T get() {
        return mResponse;
    }

    public HttpResponseWrap(int code, HashMap<String, String> headers, byte[] rspBytes) {
        statusCode = code;
        mHeaders = headers;
        mBody = rspBytes;
    }

    public HashMap<String, String> getAllHeaders() {
        return mHeaders;
    }

    public byte[] getResponseData() {
        return mBody;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
