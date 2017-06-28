package com.pacewear.httpframework.cmdproxy;

import com.pacewear.httpframework.cmdproxy.CmdData;

interface ICmdProxyCallback
{
	void onSendResult(long lReq, int err,String msg);
    void onRecvResult(in byte[] data);
}