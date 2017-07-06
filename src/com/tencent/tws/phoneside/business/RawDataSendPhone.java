
package com.tencent.tws.phoneside.business;

import qrom.component.log.QRomLog;
import android.util.Log;

import com.qq.taf.jce.JceInputStream;
import com.tencent.tws.framework.common.DevMgr;
import com.tencent.tws.framework.common.Device;
import com.tencent.tws.framework.common.ICommandHandler;
import com.tencent.tws.framework.common.MsgCmdDefine;
import com.tencent.tws.framework.common.MsgSender;
import com.tencent.tws.framework.common.MsgSender.MsgSendCallBack;
import com.tencent.tws.framework.common.TwsMsg;
import com.tencent.tws.proto.RawData;

public class RawDataSendPhone implements ICommandHandler {

    public static final String TAG = "RawDataSendPhone";

    private static RawDataSendPhone g_instance = null;

    public synchronized static RawDataSendPhone getInstance() {
        if (g_instance == null)
            g_instance = new RawDataSendPhone();

        return g_instance;
    }

    private RawDataSendPhone() {

    }

    public static int sendMessageToWatchSide(String mData,
            MsgSendCallBack callback) {
        Device oConnectedDev = DevMgr.getInstance().connectedDev();
        if (oConnectedDev == null) {
            Log.e(TAG, "error dev ,should not happen");
            return -1;
        }
        RawData mRawData = new RawData(mData);
        MsgSender.getInstance().sendCmd(oConnectedDev,
                MsgCmdDefine.CMD_SEND_BYTE_WITHOUT_AUTH_WATCH, mRawData,
                callback);
        Log.d(TAG, "sucess send now");
        return 0;
    }

    /**
     * @param mData
     * @return value 0:means SUC -1:means blueteeth bad
     */
    public static int sendMessageToWatchSide(String mData) {
        Device oConnectedDev = DevMgr.getInstance().connectedDev();
        if (oConnectedDev == null) {
            Log.e(TAG, "error dev ,should not happen");

            return -1;
        }

        RawData mRawData = new RawData(mData);

        // send data now
        MsgSender.getInstance().sendCmd(oConnectedDev,
                MsgCmdDefine.CMD_SEND_BYTE_WITHOUT_AUTH_WATCH, mRawData, null);

        Log.d(TAG, "sucess send now");
        return 0;

    }

    /**
     * @param mData
     * @return value 0:means SUC -1:means blueteeth bad
     */
    public static int sendSGLOCATIONMessageToWatchSide(String mData) {
        Device oConnectedDev = DevMgr.getInstance().connectedDev();
        if (oConnectedDev == null) {
            Log.e(TAG, "error dev ,should not happen");

            return -1;
        }

        RawData mRawData = new RawData(mData);

        // send data now
        MsgSender.getInstance().sendCmd(oConnectedDev, MsgCmdDefine.CMD_SEND_BYTE_SGLOCATION_WATCH,
                mRawData, null);

        Log.d(TAG, "sucess send now");
        return 0;

    }

    @Override
    public boolean doCommand(TwsMsg oMsg, Device oDeviceFrom) {
        // TODO Auto-generated method stub
        switch (oMsg.cmd()) {

            case MsgCmdDefine.CMD_SEND_BYTE_WITHOUT_AUTH_PHONE: {
                doReplyForHttpService(oMsg, oDeviceFrom);// accept info from watchside
                Log.d(TAG, "receive message now ");
                return true;
            }

                // case MsgCmdDefine.CMD_SEND_BYTE_SGLOCATION_PHONE:{
                // RawData mData = new RawData();
                // JceInputStream oIn = new JceInputStream(oMsg.msgByte(),
                // oMsg.startPosOfContent());
                // mData.readFrom(oIn);
                // Log.d(TAG,"receive sougou location message now ");
                // SogouLocationService.getWatchSideMessage(mData.bmpPhoto);//get watch message
                //
                // Log.d(TAG,"receive SGlocation message is "+mData.getBmpPhoto());
                //
                //
                // return true;
                // }
        }
        return false;
    }

    private void doReplyForHttpService(TwsMsg oMsg, Device oDeviceFrom) {
        Log.d(TAG, "doReplyForHttpService now");
        RawData mData = new RawData();
        // JceInputStream oIn = new JceInputStream(oMsg.msgByte(), oMsg.startPosOfContent());
        JceInputStream oIn = oMsg.getInputStreamUTF8();
        mData.readFrom(oIn);
        boolean isHttpServiceReady = false;
        try {
            isHttpServiceReady = HttpService.isHttpServiceReady();
        } catch (VerifyError e) {
            QRomLog.e(TAG + ".onCreate", "xUtils-2.6.14.jar not load");
            isHttpServiceReady = false;
        }
        if (isHttpServiceReady) {
            Log.d(TAG, "doReplyForHttpService serviceReady,so getwatchmsg");
            HttpService.getWatchSideMessage(mData.getBmpPhoto());
        }

        Log.d(TAG, "receive message is " + mData.getBmpPhoto());
    }

}
