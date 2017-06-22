
package com.tencent.tws.api.http.xutilwrap;

import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.http.xutilwrap.XUtilHttp.IHttpStreamHandler;
import com.tencent.tws.api.http.xutilwrap.XUtilHttp.IXUtilHttpCallback;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class StreamHandler implements IHttpStreamHandler {
    protected String TAG = "";
    public static final int CNT_RETRY_DEFAULT = 3;
    private IXUtilHttpCallback mHttpCallback = null;

    public StreamHandler() {
        TAG = getTAG();
    }

    protected abstract String getTAG();

    public boolean handle(HttpPackage e1, IXUtilHttpCallback callback) {
        mHttpCallback = callback;
        int ret = onHandle(e1);
        if (ret < 0) {
            return false;
        }
        if (ret == 0) {
            postResultCallback(e1);
        }
        return true;
    }

    protected abstract int onHandle(HttpPackage e1);

    protected final void postResultCallback(HttpPackage eHttpPackage) {
        if (mHttpCallback != null) {
            mHttpCallback.onCallback(eHttpPackage);
        }
    }

    protected HttpUtils generateHttpUtils(int mMaskFlag,
            HttpRequestGeneralParams mParams) {
        HttpUtils mHttpUtils;

        if ((mMaskFlag & HttpRequestGeneralParams.CONTROL_TIMEOUT) != 0) {
            mHttpUtils = new HttpUtils(mParams.requestTimeOut);
            Log.d(TAG, "load requestTimeout +" + mParams.requestTimeOut);
        } else {
            mHttpUtils = new HttpUtils(6000);// default 6s timeout
        }

        if ((mMaskFlag & HttpRequestGeneralParams.CONTROL_CACHETIMEOUT) != 0) {
            mHttpUtils.configCurrentHttpCacheExpiry(mParams.cacheTimeOut);
            Log.d(TAG, "load cacheTimeOut +" + mParams.cacheTimeOut);
        } else {
            mHttpUtils.configCurrentHttpCacheExpiry(0);// 0 represent not cache
        }

        if ((mMaskFlag & HttpRequestGeneralParams.CONTROL_USERAGENT) != 0) {
            mHttpUtils.configUserAgent(mParams.UserAgent);
            Log.d(TAG, "load UserAgnet +" + mParams.UserAgent);
        }

        mHttpUtils.configRequestRetryCount(CNT_RETRY_DEFAULT);
        return mHttpUtils;
    }

    protected RequestParams generateRequestParams(
            HttpRequestGeneralParams mParams, int mMaskFlag) {
        RequestParams params = new RequestParams();

        // 1.add head
        if (!mParams.getHeader().isEmpty()) {
            for (Map.Entry<String, String> entry : mParams.getHeader()
                    .entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                Log.i(TAG, "head (" + entry.getKey() + "," + entry.getValue()
                        + ")");

            } // add header
        }

        // 2.add body
        if ((mMaskFlag & HttpRequestGeneralParams.BODYMASK) != 0) {
            if ((mMaskFlag & HttpRequestGeneralParams.BODYPART_WITHENTITY) != 0) {
                try {// add body entity
                    if (TextUtils.isEmpty(mParams.mBodyEntityStringEncoding))
                        params.setBodyEntity(new StringEntity(
                                mParams.mBodyEntity));
                    else
                        params.setBodyEntity(new StringEntity(
                                mParams.mBodyEntity,
                                mParams.mBodyEntityStringEncoding));
                    Log.i(TAG, "we have body entry,now give value: "
                            + mParams.mBodyEntity + " , encoding: "
                            + mParams.mBodyEntity);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // end catch
            } // if
            else {// add body key-value
                for (Map.Entry<String, String> entry : mParams
                        .getBodyParamete().entrySet()) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                    Log.i(TAG,
                            "body (" + entry.getKey() + "," + entry.getValue()
                                    + ")");
                } // add body
            }
        }

        return params;
    }
}
