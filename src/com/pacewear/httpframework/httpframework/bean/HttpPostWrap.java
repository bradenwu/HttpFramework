
package com.pacewear.httpframework.httpframework.bean;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class HttpPostWrap implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private byte[] body;
    private HashMap<String, String> mHeaders;

    public HttpPostWrap() {
        mHeaders = new HashMap<String, String>();
    }

    public HttpPostWrap(HttpPost post) {
        this();
        Header[] headers = post.getAllHeaders();
        for (Header header : headers) {
            mHeaders.put(header.getName(), header.getValue());
        }
        try {
            body = EntityUtils.toByteArray(post.getEntity());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void addHeader(String key, String val) {
        mHeaders.put(key, val);
    }

    public HashMap<String, String> getHeaders() {
        return mHeaders;
    }

    public void setBody(byte[] _body) {
        body = _body;
    }

    public byte[] getBody() {
        return body;
    }
}
