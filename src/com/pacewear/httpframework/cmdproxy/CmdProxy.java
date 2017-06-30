
package com.pacewear.httpframework.cmdproxy;

import android.content.Context;

import com.qq.taf.jce.JceStruct;

public class CmdProxy {
    private BaseHttpIntercept mHttpIntercept = null;
    private static CmdProxy sInstance = null;

    public static interface ICmdSendCallback {
        void onSendResult(int err, String msg);
    }

    public static interface ICmdRecv {
        void onReceive(byte[] data);
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

    public void init(ICmdRecv receiver, int cmdType) {
        mClient = new CmdProxyClient(mContext, cmdType);
        mClient.setReceiver(receiver);
        mHttpIntercept = new HttpMsgCmdIntercept(mContext);
    }

    public void setClientHandler(IClientHandler handler) {
        mHttpIntercept.setClientHandler(handler);
    }

    public void setClientBultInHanlder(IClientBuiltInHanlder hanlder) {
        mHttpIntercept.setClientBultInHanlder(hanlder);
    }

    public void invoke(JceStruct data, ICmdSendCallback callback) {
        if (mHttpIntercept.intercept(data)) {
            return;
        }
        if (mClient != null) {
            mClient.sendData(data, callback);
        }
    }
}
