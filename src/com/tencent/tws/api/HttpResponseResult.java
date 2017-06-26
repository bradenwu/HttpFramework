
package com.tencent.tws.api;

public class HttpResponseResult {
    // this class use for application result
    public int mStatusCode;// result-status code
    private HttpResponseExtra mExtra = null;
    public static final int DirectDatas = 0;
    public static final int inDirectDatas = 1;
    public int handleType;// mark data type
    public String mData;// result data,if handleType = DirectDatas means real data,else
                        // if handleType = inDirectDatas means this response is the path of a big
                        // picture or file

    public HttpResponseResult(int mStatusCode, int handleType, String data) {
        this.handleType = handleType;
        this.mStatusCode = mStatusCode;
        this.mData = data;
    }

    public void setResponseExtra(HttpResponseExtra bean) {
        mExtra = bean;
    }

    public HttpResponseExtra getResponseExtra() {
        return mExtra;
    }
}
