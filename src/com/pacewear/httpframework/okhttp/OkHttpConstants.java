
package com.pacewear.httpframework.okhttp;

import okhttp3.MediaType;

public class OkHttpConstants {
    public static final String USER_AGENT = "User-Agent";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    public static final MediaType STREAM = MediaType.parse("application/octet-stream");
}
