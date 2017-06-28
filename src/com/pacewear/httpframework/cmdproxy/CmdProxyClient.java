
package com.pacewear.httpframework.cmdproxy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.pacewear.httpframework.cmdproxy.CmdProxy.ICmdRecv;
import com.pacewear.httpframework.cmdproxy.CmdProxy.ICmdSendCallback;
import com.qq.taf.jce.JceStruct;

import java.util.HashMap;

public class CmdProxyClient {
    private Context mContext = null;
    private ICmdProxyService mService = null;
    private int mClientCmd = 0;
    private ICmdRecv mClientRecv = null;
    private HashMap<Long, ICmdSendCallback> mSendMap = new HashMap<Long, CmdProxy.ICmdSendCallback>();
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName paramComponentName) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder) {
            mService = ICmdProxyService.Stub.asInterface(paramIBinder);
            if (mService != null) {
                try {
                    mService.setCallback(mClientCmd, mCallback);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };
    private ICmdProxyCallback.Stub mCallback = new ICmdProxyCallback.Stub() {

        @Override
        public void onSendResult(long reqId, int err, String msg) throws RemoteException {
            ICmdSendCallback callback = mSendMap.get(reqId);
            if (callback != null) {
                callback.onSendResult(err, msg);
            }
        }

        @Override
        public void onRecvResult(byte[] data) throws RemoteException {
            if (mClientRecv != null) {
                mClientRecv.onReceive(data);
            }
        }
    };

    public CmdProxyClient(Context context, int cmd) {
        bindRemoteService(context);
    }

    public void setReceiver(ICmdRecv recv) {
        mClientRecv = recv;
    }

    public void sendData(JceStruct data, ICmdSendCallback callback) {
        // TODO 缺陷：根本保证不了，回调的数据就是其原始的请求对应数据.
        if (mService == null) {
            postSendErrEvent(callback, -1, "service unbind");
            return;
        }
        try {
            long lReq = mService.transmit(mClientCmd, new CmdData(data));
            mSendMap.put(lReq, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void postSendErrEvent(ICmdSendCallback callback, int err, String msg) {
        if (callback != null) {
            callback.onSendResult(err, msg);
        }
    }

    private boolean bindRemoteService(Context context) {
        Intent intent = new Intent(CmdProxyService.SERVICE_ACTION);
        intent.setPackage(CmdProxyService.SERVICE_PACKAGE);
        return context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
