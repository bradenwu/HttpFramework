
package com.pace.watchproxy;

import com.pace.httpserver.BaseTosService;
import com.qq.taf.jce.JceStruct;

public interface IClientHandler {
    public BaseTosService newTosService(JceStruct source);

    public void onSuccess(JceStruct response);

    public void onFail(int err, String desc);

    public void onSelfHandle(JceStruct source);
}
