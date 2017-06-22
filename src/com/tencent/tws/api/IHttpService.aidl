package com.tencent.tws.api;

import com.tencent.tws.api.IHttpCallBack;
import com.tencent.tws.api.HttpPackage;

interface IHttpService
{  
    void getRequest(in HttpPackage mPackage);

    void registerCallback(String name,IHttpCallBack cb);
    
    void unregisterCallback(String name,IHttpCallBack cb);

     
}    