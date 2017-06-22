
package com.pacewear.httpframework.channel;

import android.content.Context;

import com.tencent.tws.api.HttpManager;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

public class BtChannel implements IHttpProxyChannel {

    @Override
    public HttpResponseResult transmit(HttpRequestGeneralParams request) {
        // TODO Auto-generated method stub
        Context context = null;
        // TODO
        HttpManager.getInstance(context).postGeneralHttpRequest(request, null);
        return null;
    }

}
