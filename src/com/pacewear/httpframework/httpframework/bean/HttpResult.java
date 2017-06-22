
package com.pacewear.httpframework.httpframework.bean;

public class HttpResult<T> {
    private int statusCode = 0;
    private T result = null;

    public int getCode() {
        return statusCode;
    }

    public T getResult() {
        return result;
    }
}
