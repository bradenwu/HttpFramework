package com.pacewear.httpframework.cmdproxy;

import com.pacewear.httpframework.cmdproxy.ICmdProxyCallback;
import com.pacewear.httpframework.cmdproxy.CmdData;

interface ICmdProxyService
{
	void init(int cmd,ICmdProxyCallback callback);
    
    void transmit(in CmdData data);
    
    void deinit(int cmd,ICmdProxyCallback callback);
}