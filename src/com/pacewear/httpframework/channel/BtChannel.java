
package com.pacewear.httpframework.channel;

import android.content.Context;

import com.pacewear.httpframework.common.DeviceUtil;
import com.tencent.tws.api.HttpManager;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseListener;
import com.tencent.tws.api.HttpResponseResult;

import java.util.concurrent.Semaphore;

public class BtChannel implements IHttpProxyChannel {
    private Context mContext = null;

    public BtChannel(Context context) {
        mContext = context;
    }

    @Override
    public boolean isReady() {
        return DeviceUtil.isBluetoothOn(mContext);
    }

    @Override
    public HttpResponseResult transmit(HttpRequestGeneralParams request) {
        final SemaphoreController controller = new SemaphoreController();
        HttpManager.getInstance(mContext).postGeneralHttpRequest(request,
                new HttpResponseListener() {

                    @Override
                    public void onResponse(HttpResponseResult mResult) {
                        controller.releaseResult(0, mResult);
                    }

                    @Override
                    public void onError(int statusCode, HttpResponseResult mResult) {
                        controller.releaseResult(statusCode, mResult);
                    }
                });

        return controller.waitResult();
    }

    static class SemaphoreController {
        private Semaphore mSemaphore = null;
        private HttpResponseResult mResult = null;

        public SemaphoreController() {
            mSemaphore = new Semaphore(0);
        }

        public HttpResponseResult waitResult() {
            try {
                mSemaphore.acquire();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return mResult;
        }

        public void releaseResult(int statusCode, HttpResponseResult result) {
            mResult = result;
            mSemaphore.release();
        }
    }
}
