
package com.pacewear.httpframework.cmdproxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.pacewear.httpframework.cmdproxy.HttpCmdProxyHandler.ICmdReceiver;
import com.tencent.tws.framework.common.MsgSender.MsgSendCallBack;

import java.util.concurrent.ConcurrentHashMap;

import qrom.component.log.QRomLog;

public class CmdProxyService extends Service implements ICmdReceiver {
    public static final String SERVICE_ACTION = "com.pacewear.httpframework.cmdproxy.action_cmdproxy";
    public static final String SERVICE_PACKAGE = "com.tencent.tws.watchside";
    public static final String TAG = "CmdProxyService";
    private ConcurrentHashMap<Integer, ICmdProxyCallback> mCallbackMap = new ConcurrentHashMap<Integer, ICmdProxyCallback>();
    private ConcurrentHashMap<Integer, ProxyClient> mClinets = new ConcurrentHashMap<Integer, ProxyClient>();

    @Override
    public void onCreate() {
        super.onCreate();
        HttpCmdProxyHandler.getInstance().setReceiver(this);
    }

    private ICmdProxyService.Stub mBinder = new ICmdProxyService.Stub() {

        @Override
        public void setCallback(int cmd, ICmdProxyCallback callback) throws RemoteException {
            HttpCmdProxyHandler.getInstance().addCmd(cmd);
            synchronized (mCallbackMap) {
                mCallbackMap.put(cmd, callback);
            }
            synchronized (mClinets) {
                ProxyClient client = null;
                if (!mClinets.containsKey(cmd)) {
                    client = new ProxyClient(cmd, callback.asBinder());
                    mClinets.put(cmd, client);
                    callback.asBinder().linkToDeath(client, 0);
                    QRomLog.d(TAG, " new client  link to death,cmd id: " + cmd);
                } else {
                    client = mClinets.get(cmd);
                    client.mToken.unlinkToDeath(client, 0);
                    client.mToken = callback.asBinder();
                    client.mToken.linkToDeath(client, 0);
                    QRomLog.d(TAG, " client relink to death,cmd id: " + cmd);
                }
            }
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
        synchronized (mCallbackMap) {
            if (mCallbackMap.containsKey(cmd)) {
                return mCallbackMap.get(cmd);
            }
        }
        return null;
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
            callback.onRecvResult(cmd, data);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(int cmd, byte[] result) {
        postRecvResult(cmd, result);
    }

    private class ProxyClient implements IBinder.DeathRecipient {
        private int mCmd;
        public IBinder mToken;

        public ProxyClient(int cmd, IBinder token) {
            mCmd = cmd;
            mToken = token;
        }

        @Override
        public void binderDied() {
            synchronized (mCallbackMap) {
                if (mCallbackMap.containsKey(mCmd)) {
                    mCallbackMap.remove(mCmd);
                }
            }
            synchronized (mClinets) {
                if (mClinets.containsKey(mCmd)) {
                    mClinets.remove(mCmd);
                }
            }
        }

    }
}
