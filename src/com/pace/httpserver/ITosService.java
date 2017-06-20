
package com.pace.httpserver;

import com.qq.jce.wup.UniPacket;
import com.qq.taf.jce.JceStruct;

/**
 * @author baodingzhou
 */

public interface ITosService {

    public static final int ERR_DOCODE_ERROR = 10001;
    public static final int ERR_PARSE_ERROR = ERR_DOCODE_ERROR + 1;

    public static final String MODULE_NAME = "watchpay";

    public static final String MODULE_VER = "1.0";

    public static final String REQ_NAME = "req";

    public static final String RSP_NAME = "rsp";

    public static final int OPERTYPE_UNKNOWN = 2000;

    /**
     * getFunctionName
     * 
     * @return
     */
    public String getFunctionName();

    /**
     * getUniqueSeq
     * 
     * @return
     */
    public long getUniqueSeq();

    /**
     * getOperType
     * 
     * @return
     */
    public int getOperType();

    /**
     * getReq
     * 
     * @param payReqHead
     * @return
     */
    public JceStruct getReq(JceStruct payReqHead);

    /**
     * parse
     * 
     * @param packet
     * @return
     */
    public JceStruct parse(UniPacket packet);

    /**
     * getRspObject
     * 
     * @return
     */
    public JceStruct getRspObject();

    /**
     * invoke
     * 
     * @param listener
     * @return
     */
    public boolean invoke(IResponseObserver listener);
}
