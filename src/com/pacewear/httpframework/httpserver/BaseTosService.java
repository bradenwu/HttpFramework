
package com.pacewear.httpframework.httpserver;

import android.content.Context;

import com.pacewear.httpframework.common.SeqGenerator;
import com.pacewear.httpframework.log.HttpLog;
import com.qq.jce.wup.UniPacket;
import com.qq.taf.jce.JceStruct;

import java.lang.reflect.Field;

import qrom.component.wup.QRomWupDataBuilder;

/**
 * @author baodingzhou
 */

public abstract class BaseTosService
        implements IServerHandlerListener, ITosService, IResponseObserver {

    private static final String TAG = "TosService";

    private static SeqGenerator sSeqGenerator = SeqGenerator.getInstance();

    private long mUniqueSeq = -1;

    private int mReqID = -1;

    private IResponseObserver mResponseObserver = null;

    protected String mMouduleName = "";
    private static int mFreshTokenTimes = 0;
    private String mReqName = REQ_NAME;
    private String mRspName = RSP_NAME;
    protected boolean mNeedReqHeader = true;
    protected Context mContext = null;

    public BaseTosService(Context context, String _module, String _req, String _rsp) {
        mContext = context;
        mMouduleName = _module;
        mReqName = _req;
        mRspName = _rsp;
    }

    @Override
    public final long getUniqueSeq() {
        return mUniqueSeq;
    }

    @Override

    public boolean invoke(IResponseObserver observer) {
        boolean handled = false;

        // checking
        // if (observer == null) {
        // return handled;
        // }
        mResponseObserver = observer;

        int operType = getOperType();
        if (operType == OPERTYPE_UNKNOWN) {
            return handled;
        }
        JceStruct header = getJceHeader();
        if (header == null && mNeedReqHeader) {
            return handled;
        }
        JceStruct req = getReq(header);
        if (req == null) {
            return handled;
        }

        HttpLog.d(
                TAG,
                String.format("%s,mUniqueSeq:%d req:%s", getClass().getSimpleName(), mUniqueSeq,
                        JceStruct.toDisplaySimpleString(req)));

        UniPacket packet = QRomWupDataBuilder.createReqUnipackageV3(
                mMouduleName, getFunctionName(),
                mReqName, req);

        IServerHandler serverHandler = ServerHandler.getInstance(mContext);

        serverHandler.registerServerHandlerListener(this);
        serverHandler.setRequestEncrypt(getRequestEncrypt());
        mReqID = serverHandler.reqServer(operType, packet);

        if (mReqID >= 0) {
            handled = true;
        }

        if (!handled) {
            serverHandler.unregisterServerHandlerListener(this);
        }

        return handled;
    }

    protected boolean getRequestEncrypt() {
        return false;
    }

    private final static UniPacket decodePacket(byte[] data) {
        UniPacket packet = new UniPacket();
        packet.setEncodeName("UTF8");
        packet.decode(data);
        return packet;
    }

    @Override
    public final JceStruct parse(UniPacket packet) {
        if (packet == null) {
            return null;
        }

        JceStruct rsp = getRspObject();
        if (rsp == null) {
            return null;
        }
        return packet.getByClass(mRspName, rsp);
    }

    @Override
    public final boolean onResponseSucceed(int reqID, int operType, byte[] response) {
        if (mReqID == reqID) {
            int error = ERR_DOCODE_ERROR;
            UniPacket packet = decodePacket(response);
            JceStruct rsp = null;
            if (packet != null) {
                error = ERR_PARSE_ERROR;
                rsp = parse(packet);
            }

            if (rsp != null) {
                HttpLog.d(
                        TAG,
                        String.format("%s,mUniqueSeq:%d rsp:%s", getClass().getSimpleName(),
                                mUniqueSeq,
                                JceStruct.toDisplaySimpleString(rsp)));
                int errCode = getSubClassRspIRet(rsp);
                onResponseSucceed(mUniqueSeq, operType, rsp);
            } else {
                // Impossible if code right.
                onResponseFailed(mUniqueSeq, operType, error, "");
            }

            return true;
        }
        return false;
    }

    @Override
    public final boolean onResponseFailed(int reqID, int operType, int errorCode,
            String description) {
        if (mReqID == reqID) {
            HttpLog.d(TAG, String.format("mUniqueSeq:%d errorCode:%d description:%s", mUniqueSeq,
                    errorCode, description));
            onResponseFailed(mUniqueSeq, operType, errorCode, description);
            return true;
        }
        return false;
    }

    @Override
    public final void onResponseSucceed(long uniqueSeq, int operType, JceStruct response) {
        mFreshTokenTimes = 0;
        if (mResponseObserver != null) {
            mResponseObserver.onResponseSucceed(uniqueSeq, operType, response);
        }
    }

    @Override
    public final void onResponseFailed(long uniqueSeq, int operType, int errorCode,
            String description) {
        mFreshTokenTimes = 0;
        if (mResponseObserver != null) {
            mResponseObserver.onResponseFailed(uniqueSeq, operType, errorCode, description);
        }
    }

    private int getSubClassRspIRet(JceStruct rsp) {
        int iRet = ERR_PARSE_ERROR;
        Class jce = rsp.getClass();
        Field field = null;
        try {
            field = jce.getDeclaredField("iRet");
            field.setAccessible(true);
            iRet = (Integer) field.get(rsp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return iRet;
    }

    protected abstract JceStruct getJceHeader();

}
