package com.tencent.tws.api;

import com.tencent.tws.api.HttpPackage;

interface IHttpCallBack{
	void showResult(in  HttpPackage resultData);
}