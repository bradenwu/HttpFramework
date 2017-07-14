
package com.pacewear.httpframework.cmdproxy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

import com.pacewear.httpframework.cmdproxy.CmdProxy.ICmdRecv;
import com.pacewear.httpframework.cmdproxy.CmdProxy.ICmdSendCallback;
import com.pacewear.lock.IJavaLock;
import com.pacewear.lock.JavaLockHelper;
import com.qq.taf.jce.JceStruct;
import com.tencent.tws.framework.common.TwsMsg;

import java.io.IOException;
import java.util.HashMap;

public class CmdProxyClient {
    public static final String SERVICE_ACTION = "com.pacewear.httpframework.cmdproxy.action_cmdproxy";
    public static final String SERVICE_PACKAGE = "com.tencent.tws.watchside";
    private static final String TAG = "CmdProxyClient";
    private Context mContext = null;
    private IJavaLock mJavaLock = null;
    private ICmdProxyService mService = null;
    private SparseArray<ICmdRecv> mClientRecvMap = new SparseArray<ICmdRecv>();
    private HashMap<Long, ICmdSendCallback> mSendMap = new HashMap<Long, CmdProxy.ICmdSendCallback>();
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName paramComponentName) {
            Log.d(TAG, "onServiceDisconnected Thread:" + Thread.currentThread().getName());
            mService = null;
            mJavaLock.unlock();
        }

        @Override
        public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder) {
            Log.d(TAG, "onServiceConnected Thread:" + Thread.currentThread().getName());
            mService = ICmdProxyService.Stub.asInterface(paramIBinder);
            mJavaLock.unlock();
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
        public void onRecvResult(int recvCmdType, byte[] data) throws RemoteException {
            ICmdRecv clientRecv = mClientRecvMap.get(recvCmdType);
            if (clientRecv != null) {
                TwsMsg oMsg = new TwsMsg(data);
                try {
                    oMsg.parse();
                    clientRecv.onReceive(oMsg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };

    public CmdProxyClient(Context context) {
        mContext = context;
        mJavaLock = JavaLockHelper.newLock();
    }

    public void addReceiver(ICmdRecv recv, int recvId) {
        mClientRecvMap.put(recvId, recv);
        if (isServiceReady(mContext)) {
            try {
                mService.setCallback(recvId, mCallback);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private boolean isServiceReady(Context context) {
        Log.d(TAG, "isServiceReady begin");
        if (mService != null) {
            Log.d(TAG, "isServiceReady: mService not null,direct use");
            return true;
        }
        Log.d(TAG, "isServiceReady: need bindservice sync");
        if (!bindRemoteService(context)) {
            Log.e(TAG, "isServiceReady: binde service error");
            return false;
        }
        Log.d(TAG, "isServiceReady: wait service ok");
        mJavaLock.lock(0);

        return mService != null;
    }

    public void sendData(int cmd, JceStruct data, ICmdSendCallback callback) {
        // TODO 缺陷：根本保证不了，回调的数据就是其原始的请求对应数据.
        if (!isServiceReady(mContext)) {
            postSendErrEvent(callback, -1, "service unbind");
            return;
        }
        try {
            long lReq = mService.transmit(cmd, new CmdData(data));
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
        Intent intent = new Intent(SERVICE_ACTION);
        intent.setPackage(SERVICE_PACKAGE);
        return context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
