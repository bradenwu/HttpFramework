
package com.pacewear.httpframework.cmdproxy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class CmdProxyClient {
    private Context mContext = null;
    private ICmdProxyService mService = null;
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
                    mService.init(0, mCallback);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };
    private ICmdProxyCallback.Stub mCallback = new ICmdProxyCallback.Stub() {

        @Override
        public void onSendResult(int err, String msg) throws RemoteException {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRecvResult(int err, CmdData data) throws RemoteException {
            // TODO Auto-generated method stub

        }

    };

    public CmdProxyClient(Context context, int cmd) {

    }

    private boolean bindRemoteService(Context context) {
        Intent intent = new Intent(CmdProxyService.SERVICE_ACTION);
        intent.setPackage(CmdProxyService.SERVICE_PACKAGE);
        return context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
