
package com.pacewear.httpframework.cmdproxy;

import android.content.Context;

import com.qq.taf.jce.JceStruct;

public class CmdProxy {
    public static interface ICmdCallback {
        void onSendResult(int err, String msg);

        void onRecvResult(int err, JceStruct data);
    }

    private CmdProxyClient mClient = null;

    /**
     * context
     */
    public void init(Context context, int cmdType) {
        mClient = new CmdProxyClient(context, cmdType);
    }

    public void invoke(JceStruct data, ICmdCallback callback) {

    }
}
