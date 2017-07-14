
package com.pacewear.httpframework.okhttp;

import android.content.Context;

import com.pacewear.httpframework.channel.BlueToothChannel;
import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.core.IHttpClient;
import com.pacewear.httpframework.okhttp.bean.OkHttpClientBuilder;
import com.pacewear.httpframework.okhttp.bean.RequestBuilder;
import com.pacewear.httpframework.okhttp.https.SSLParcelParam;
import com.pacewear.httpframework.route.HttpRouter;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.RealCall;
import okhttp3.Request;
import okhttp3.Response;

final class NewRealCall extends RealCall {
    private Context mContext = null;
    private Request mOriginalRequest = null;
    private SSLParcelParam mSSLParcelParam = null;
    private OkHttpClient mOriginalOkHttpClient = null;

    NewRealCall(Context context, OkHttpClient client,
            Request originalRequest,
            boolean forWebSocket, SSLParcelParam sslParcelParam) {
        super(client, originalRequest, forWebSocket);
        mOriginalOkHttpClient = client;
        mContext = context;
        mOriginalRequest = originalRequest;
        mSSLParcelParam = sslParcelParam;
    }

    private Response invokeRemoteCall() {
        RequestBuilder requestBuilder = new RequestBuilder(mOriginalRequest);
        OkHttpClientBuilder clientBuilder = new OkHttpClientBuilder(); // TODO
        clientBuilder.setConnectTimeout(mOriginalOkHttpClient.connectTimeout);
        clientBuilder.setReadTimeout(mOriginalOkHttpClient.readTimeout);
        clientBuilder.setWriteTimeout(mOriginalOkHttpClient.writeTimeout);
        if (mSSLParcelParam != null) {
            clientBuilder.setSslParcelParam(mSSLParcelParam);
        }
        IHttpClient<Response, OkHttpClientBuilder, RequestBuilder> okHttpClient = new OkHttpClientImp(
                mContext, false);
        return okHttpClient.execute(clientBuilder, requestBuilder);
    }

    private boolean isBluetoothChannelReady() {
        if (mContext != null) {
            IHttpProxyChannel channel = HttpRouter.get().getSelectChannel(mContext);
            if (channel instanceof BlueToothChannel) {
                return true;
            }
        }
        return false;
    }

    // core function
    @Override
    protected Response getResponseWithInterceptorChain() throws IOException {
        if (isBluetoothChannelReady()) {
            return invokeRemoteCall();
        }
        return super.getResponseWithInterceptorChain();
    }
}
