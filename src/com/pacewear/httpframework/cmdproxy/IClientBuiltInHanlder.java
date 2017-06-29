
package com.pacewear.httpframework.cmdproxy;

import com.pacewear.httpframework.httpserver.BaseTosService;
import com.qq.taf.jce.JceStruct;

public interface IClientBuiltInHanlder {
    public BaseTosService newTosService(JceStruct source);

    public void onSuccess(JceStruct response);

    public void onFail(int err, String desc);
}
