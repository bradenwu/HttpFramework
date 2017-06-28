package com.pacewear.httpframework.cmdproxy;

import com.pacewear.httpframework.cmdproxy.CmdData;

interface ICmdProxyCallback
{
	void onSendResult(int err,String msg);
    void onRecvResult(int err,in CmdData data); 
}