
package com.pacewear.httpframework.cmdproxy;

import android.content.Context;

import com.qq.taf.jce.JceStruct;

public class CmdProxy {
    private BaseHttpIntercept mHttpIntercept = null;

    public static interface ICmdSendCallback {
        void onSendResult(int err, String msg);
    }

    public static interface ICmdRecv {
        void onReceive(byte[] data);
    }

    private CmdProxyClient mClient = null;

    /**
     * context
     */
    public void init(Context context, ICmdRecv receiver, int cmdType) {
        mClient = new CmdProxyClient(context, cmdType);
        mClient.setReceiver(receiver);
        mHttpIntercept = new HttpMsgCmdIntercept(context);
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
