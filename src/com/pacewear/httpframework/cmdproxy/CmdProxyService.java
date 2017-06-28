
package com.pacewear.httpframework.cmdproxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.pacewear.httpframework.cmdproxy.HttpCmdProxyHandler.ICmdReceiver;
import com.tencent.tws.framework.common.MsgSender.MsgSendCallBack;

import java.util.HashMap;

public class CmdProxyService extends Service implements ICmdReceiver {
    public static final String SERVICE_ACTION = "com.pacewear.httpframework.cmdproxy.action_cmdproxy";
    public static final String SERVICE_PACKAGE = "com.tencent.tws.watchside";
    private HashMap<Integer, ICmdProxyCallback> mCallbackMap = new HashMap<Integer, ICmdProxyCallback>();

    @Override
    public void onCreate() {
        super.onCreate();
        HttpCmdProxyHandler.getInstance().setReceiver(this);
    }

    private ICmdProxyService.Stub mBinder = new ICmdProxyService.Stub() {

        @Override
        public void setCallback(int cmd, ICmdProxyCallback callback) throws RemoteException {
            mCallbackMap.put(cmd, callback);
        }

        @Override
        public long transmit(final int cmd, CmdData data) throws RemoteException {
            long sendRet = HttpCmdProxyHandler.getInstance().sendMessageToDmSide(cmd,
                    data.getData(),
                    new MsgSendCallBack() {

                @Override
                public void onSendResult(boolean paramBoolean, long paramLong) {
                    postSendResult(cmd, paramLong, paramBoolean ? 0 : -1, "");
                }

                @Override
                public void onLost(int paramInt, long paramLong) {
                    postSendResult(cmd, paramLong, -1, "reason:" + paramInt);
                }

            });
            return sendRet;
        }
    };

    @Override
    public IBinder onBind(Intent paramIntent) {
        return mBinder;
    }

    private ICmdProxyCallback getCallbackById(int cmd) {
        if (!mCallbackMap.containsKey(cmd)) {
            return null;
        }
        return mCallbackMap.get(cmd);
    }

    private void postSendResult(int cmd, long reqId, int err, String msg) {
        ICmdProxyCallback callback = getCallbackById(cmd);
        if (callback == null) {
            return;
        }
        try {
            callback.onSendResult(reqId, err, msg);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void postRecvResult(int cmd, byte[] data) {
        ICmdProxyCallback callback = getCallbackById(cmd);
        if (callback == null) {
            return;
        }
        try {
            callback.onRecvResult(data);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(int cmd, byte[] result) {
        postRecvResult(cmd, result);
    }
}
