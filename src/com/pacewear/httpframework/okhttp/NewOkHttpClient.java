
package com.pacewear.httpframework.okhttp;

import android.content.Context;

import com.pacewear.httpframework.okhttp.bean.OkHttpClientBuilder;
import com.pacewear.httpframework.okhttp.https.SSLModel;
import com.pacewear.httpframework.okhttp.https.SSLParcelParam;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.OkHttpClient.Builder;

public class NewOkHttpClient {
    private static Context mContext = null;

    public static Context getContext() {
        return mContext;
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static class Builder extends OkHttpClient.Builder {
        private SSLParcelParam mSslParcelParam = null;

        public Builder() {
            super();
        }

        public Builder(OkHttpClient okHttpClient) {
            super(okHttpClient);
        }

        public Builder sslSocketFactory(InputStream[] certInput, InputStream bksFiles, String pwd) {
            SSLModel.SSLParams sslParam = SSLModel.getSslSocketFactory(certInput, bksFiles, pwd);
            mSslParcelParam = new SSLParcelParam(certInput, bksFiles, pwd);
            sslSocketFactory(sslParam.sSLSocketFactory, sslParam.trustManager);
            return this;
        }

        @Override
        public OkHttpClient build() {
            // addInterceptor(new ParamRecordIntercept());
            return new OkHttpClientProxy(NewOkHttpClient.Builder.this, mSslParcelParam);
        }
    }

    public static class OkHttpClientProxy extends OkHttpClient {
        private SSLParcelParam mSslParcelParam = null;

        public OkHttpClientProxy(Builder builder, SSLParcelParam sslParcelParam) {
            super(builder);
            mSslParcelParam = sslParcelParam;
        }

        @Override
        public Call newCall(Request request) {
            return new NewRealCall(mContext, this, request, false /* for web socket */,mSslParcelParam);
        }
    }
}
