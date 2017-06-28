package com.pacewear.httpframework.cmdproxy;

import com.pacewear.httpframework.cmdproxy.ICmdProxyCallback;
import com.pacewear.httpframework.cmdproxy.CmdData;

interface ICmdProxyService
{
	void setCallback(int cmd, ICmdProxyCallback callback);
    
    long transmit(int cmd, in CmdData data);
    
}