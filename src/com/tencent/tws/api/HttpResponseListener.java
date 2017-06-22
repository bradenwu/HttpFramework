package com.tencent.tws.api;

public interface HttpResponseListener {
	public void onResponse(HttpResponseResult mResult);//success responce
	public void onError(int statusCode, HttpResponseResult mResult);//error process
}
