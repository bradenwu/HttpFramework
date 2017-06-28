
package com.pacewear.httpframework.cmdproxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class CmdProxyService extends Service {
    public static final String SERVICE_ACTION = "com.pacewear.httpframework.cmdproxy.action_cmdproxy";
    public static final String SERVICE_PACKAGE = "com.tencent.tws.watchside";
    private ICmdProxyService.Stub mBinder = new ICmdProxyService.Stub() {

        @Override
        public void transmit(CmdData data) throws RemoteException {
            // TODO Auto-generated method stub

        }

        @Override
        public void init(int cmd, ICmdProxyCallback callback) throws RemoteException {
            // TODO Auto-generated method stub

        }

        @Override
        public void deinit(int cmd, ICmdProxyCallback callback) throws RemoteException {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public IBinder onBind(Intent paramIntent) {
        return mBinder;
    }

}
