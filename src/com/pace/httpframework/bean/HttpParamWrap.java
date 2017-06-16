
package com.pace.httpframework.bean;

import java.io.Serializable;

public class HttpParamWrap implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long lConnectionTimeout = -1;
    private long lSoTimeout = -1;

    public void setConnectionTimeout(long timeout) {
        lConnectionTimeout = timeout;
    }

    public long getConnectionTimeout() {
        return lConnectionTimeout;
    }

    public void setSoTimeout(long timeout) {
        lSoTimeout = timeout;
    }

    public long getSoTimeout() {
        return lSoTimeout;
    }

}
