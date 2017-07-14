
package com.pacewear.httpframework.cmdproxy;

import android.content.Context;
import android.util.Log;

import com.qq.taf.jce.JceStruct;
import com.tencent.tws.framework.common.TwsMsg;

public class CmdProxy {
    private BaseHttpIntercept mHttpIntercept = null;
    private static CmdProxy sInstance = null;
    public static final String TAG = "CmdProxy";

    public static interface ICmdSendCallback {
        void onSendResult(int err, String msg);
    }

    public static interface ICmdRecv {
        void onReceive(TwsMsg oMsg);
    }

    private CmdProxyClient mClient = null;
    private Context mContext = null;

    public static CmdProxy getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CmdProxy.class) {
                if (sInstance == null) {
                    sInstance = new CmdProxy(context);
                }
            }
        }
        return sInstance;
    }

    private CmdProxy(Context context) {
        mContext = context.getApplicationContext();
    }

    public void regist(ICmdRecv receiver, int cmdType) {
        mClient = new CmdProxyClient(mContext);
        mClient.addReceiver(receiver, cmdType);
        mHttpIntercept = new HttpMsgCmdIntercept(mContext);
    }

    public void setClientHandler(IClientHandler handler) {
        mHttpIntercept.setClientHandler(handler);
    }

    public void setClientBultInHanlder(IClientBuiltInHanlder hanlder) {
        mHttpIntercept.setClientBultInHanlder(hanlder);
    }

    public void invoke(int cmd, JceStruct data, ICmdSendCallback callback) {
        if (mHttpIntercept.intercept(data)) {
            Log.d(TAG, "do not intercept");
            return;
        }
        if (mClient != null) {
            mClient.sendData(cmd, data, callback);
        }
    }
}
