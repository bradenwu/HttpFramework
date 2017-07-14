
package com.pacewear.httpframework.okhttp.bean;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Protocol;

public class ProtocolBuilder implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String protocolStr = "http/1.0";

    public ProtocolBuilder(Protocol protocol) {
        protocolStr = protocol.toString();
    }

    public Protocol toProtocol() {
        try {
            return Protocol.get(protocolStr);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
