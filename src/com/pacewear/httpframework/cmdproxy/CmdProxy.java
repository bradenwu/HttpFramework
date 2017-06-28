
package com.pacewear.httpframework.cmdproxy;

import android.content.Context;

import com.qq.taf.jce.JceStruct;

public class CmdProxy {
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
    }

    public void invoke(JceStruct data, ICmdSendCallback callback) {
        if (mClient != null) {
            mClient.sendData(data, callback);
        }
    }
}
