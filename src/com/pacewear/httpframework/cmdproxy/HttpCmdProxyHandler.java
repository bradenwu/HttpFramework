
package com.pacewear.httpframework.cmdproxy;

import android.util.SparseArray;

import com.qq.taf.jce.JceStruct;
import com.tencent.tws.framework.common.CommandHandler;
import com.tencent.tws.framework.common.DevMgr;
import com.tencent.tws.framework.common.Device;
import com.tencent.tws.framework.common.ICommandHandler;
import com.tencent.tws.framework.common.MsgDispatcher;
import com.tencent.tws.framework.common.MsgSender;
import com.tencent.tws.framework.common.TwsMsg;

import java.util.HashSet;

import com.tencent.tws.framework.common.MsgSender.MsgSendCallBack;

import qrom.component.log.QRomLog;

public class HttpCmdProxyHandler implements ICommandHandler {
    public static interface ICmdReceiver {
        public void onReceive(int cmd, byte[] result);
    }

    public static final String TAG = "RawDataSend";

    private static HttpCmdProxyHandler g_instance = null;
    private HashSet<Integer> mCmdSet = null;
    private ICmdReceiver mReceiver = null;

    // public static final int CMD_
    public static HttpCmdProxyHandler getInstance() {
        if (g_instance == null)
            g_instance = new HttpCmdProxyHandler();

        return g_instance;
    }

    public void setReceiver(ICmdReceiver receiver) {
        mReceiver = receiver;
    }

    private HttpCmdProxyHandler() {
        mCmdSet = new HashSet<Integer>();
    }

    public void addCmd(int cmd) {
        mCmdSet.add(cmd);
        SparseArray<CommandHandler> oArr = new SparseArray<CommandHandler>();
        oArr.put(cmd, new CommandHandler(HttpCmdProxyHandler.class.getName()));
        MsgDispatcher.getInstance().appendPluginRecvMsg(oArr);
    }

    public long sendMessageToDmSide(int cmd, JceStruct data, MsgSendCallBack callback) {
        Device oConnectedDev = DevMgr.getInstance().connectedDev();
        if (oConnectedDev == null) {
            QRomLog.e(TAG, "error dev ,should not happen");
            return -1;
        }
        return MsgSender.getInstance().sendCmd(oConnectedDev, cmd, data, callback);
    }

    @Override
    public boolean doCommand(TwsMsg oMsg, Device oDeviceFrom) {
        int cmd = oMsg.cmd();
        if (!mCmdSet.contains(cmd)) {
            return false;
        }
        if (mReceiver != null) {
            mReceiver.onReceive(cmd, oMsg.msgByte());
        }
        return true;
    }
}
