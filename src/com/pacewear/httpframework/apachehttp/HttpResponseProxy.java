
package com.pacewear.httpframework.apachehttp;

import android.text.TextUtils;
import android.util.Base64;

import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpResponseResult;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponseProxy implements HttpResponse {
    Header[] mHeaders = null;
    HashMap<String, Header> mTargeMap = new HashMap<String, Header>();
    HttpEntity mHttpEntity = null;
    int mStatusCode = 0;

    public HttpResponseProxy(HttpResponseResult rspResult) {
        init(rspResult);
    }

    private void init(HttpResponseResult _result) {
        mStatusCode = (HttpRequestCommand.NORMAL_STATUS == _result.mStatusCode) ? 200
                : _result.mStatusCode;
        List<Header> headerList = new ArrayList<Header>();
        String extra = _result.getResponseExtra();
        if (!TextUtils.isEmpty(extra)) {
            try {
                JSONObject headers = new JSONObject(extra);
                if (headers != null) {
                    Iterator<String> keys = headers.keys();
                    while (keys.hasNext()) {
                        final String key = (String) keys.next();
                        final String val = headers.optString(key);
                        Header _header = new Header() {

                            @Override
                            public String getValue() {
                                return val;
                            }

                            @Override
                            public String getName() {
                                return key;
                            }

                            @Override
                            public HeaderElement[] getElements() throws ParseException {
                                return null;
                            }
                        };
                        headerList.add(_header);
                    }
                    mHeaders = (Header[]) headerList.toArray();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Map<String, String> map = extra.getHeaders();
        // TODO decode需要重点测试
        byte[] bsContent = Base64.decode(_result.mData, Base64.DEFAULT);
        mHttpEntity = new ByteArrayEntity(bsContent);
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsHeader(String paramString) {
        return mTargeMap.containsKey(paramString);
    }

    @Override
    public Header[] getHeaders(String paramString) {
        return new Header[] {
                mTargeMap.get(paramString)
        };
    }

    @Override
    public Header getFirstHeader(String paramString) {
        return mTargeMap.get(paramString);
    }

    @Override
    public Header getLastHeader(String paramString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Header[] getAllHeaders() {
        return mHeaders;
    }

    @Override
    public void addHeader(Header paramHeader) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addHeader(String paramString1, String paramString2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHeader(Header paramHeader) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHeader(String paramString1, String paramString2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHeaders(Header[] paramArrayOfHeader) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeHeader(Header paramHeader) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeHeaders(String paramString) {
        // TODO Auto-generated method stub

    }

    @Override
    public HeaderIterator headerIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HeaderIterator headerIterator(String paramString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HttpParams getParams() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setParams(HttpParams paramHttpParams) {
        // TODO Auto-generated method stub

    }

    @Override
    public StatusLine getStatusLine() {
        // TODO Auto-generated method stub
        return new StatusLine() {

            @Override
            public int getStatusCode() {
                return mStatusCode;
            }

            @Override
            public String getReasonPhrase() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    @Override
    public void setStatusLine(StatusLine paramStatusLine) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatusLine(ProtocolVersion paramProtocolVersion, int paramInt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatusLine(ProtocolVersion paramProtocolVersion, int paramInt,
            String paramString) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatusCode(int paramInt) throws IllegalStateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setReasonPhrase(String paramString) throws IllegalStateException {
        // TODO Auto-generated method stub

    }

    @Override
    public HttpEntity getEntity() {
        return mHttpEntity;
    }

    @Override
    public void setEntity(HttpEntity paramHttpEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLocale(Locale paramLocale) {
        // TODO Auto-generated method stub

    }

}
