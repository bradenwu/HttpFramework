
package com.pacewear.httpframework.channel;

import android.content.Context;
import android.util.Log;

import com.pacewear.httpframework.core.IHttpRequestAction;
import com.tencent.tws.api.HttpManager;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseListener;
import com.tencent.tws.api.HttpResponseResult;

import java.util.concurrent.Semaphore;

public class BlueToothChannel<Rsp, Param, Post> implements IHttpProxyChannel<Rsp, Param, Post> {
    private IHttpRequestAction<Rsp, Param, Post> mClientAction = null;
    private Context mContext = null;
    private static final String TAG = "BlueToothChannel";

    public BlueToothChannel(Context context) {
        mContext = context;
    }

    @Override
    public Rsp transmit(Param param, Post post) {
        if (mClientAction == null) {
            return null;
        }
        HttpRequestGeneralParams request = mClientAction.prepareRequest(param, post);
        HttpResponseResult httpResponseWrap = transmitInternal(request);
        return mClientAction.parseResponse(httpResponseWrap);
    }

    @Override
    public void setClient(IHttpRequestAction<Rsp, Param, Post> action) {
        mClientAction = action;
    }

    private HttpResponseResult transmitInternal(HttpRequestGeneralParams request) {
        final SemaphoreController controller = new SemaphoreController();
        HttpManager.getInstance(mContext).postGeneralHttpRequest(request,
                new HttpResponseListener() {

                    @Override
                    public void onResponse(HttpResponseResult mResult) {
                        Log.d(TAG, "curThread:" + Thread.currentThread().getName() + ",onResponse :"
                                + mResult.mData);
                        controller.releaseResult(0, mResult);
                    }

                    @Override
                    public void onError(int statusCode, HttpResponseResult mResult) {
                        Log.d(TAG, "curThread:" + Thread.currentThread().getName() + ",onError :"
                                + mResult.mData);
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
            Log.d(TAG, "curThread:" + Thread.currentThread().getName() + "waitResult here!");
            try {
                mSemaphore.acquire();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d(TAG, "curThread:" + Thread.currentThread().getName() + "waitResult complete!");
            return mResult;
        }

        public void releaseResult(int statusCode, HttpResponseResult result) {
            mResult = result;
            mSemaphore.release();
        }
    }
}
