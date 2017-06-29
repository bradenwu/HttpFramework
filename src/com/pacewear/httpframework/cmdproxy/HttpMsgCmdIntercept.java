
package com.pacewear.httpframework.cmdproxy;

import android.content.Context;

import com.pacewear.httpframework.httpserver.BaseTosService;
import com.pacewear.httpframework.httpserver.IResponseObserver;
import com.qq.taf.jce.JceStruct;

public class HttpMsgCmdIntercept extends BaseHttpIntercept {

    HttpMsgCmdIntercept(Context context) {
        super(context);
    }

    @Override
    protected boolean onIntercept(JceStruct data) {
        if (mClientBuiltInHanlder == null) {
            return false;
        }
        BaseTosService service = mClientBuiltInHanlder.newTosService(data);
        if (service == null) {
            return false;
        }
        final long lReq = service.getUniqueSeq();
        boolean handle = service.invoke(new IResponseObserver() {
            @Override
            public void onResponseSucceed(long uniqueSeq, int operType, JceStruct response) {
                if (lReq == uniqueSeq) {
                    mClientBuiltInHanlder.onSuccess(response);
                }
            }

            @Override
            public void onResponseFailed(long uniqueSeq, int operType, int errorCode,
                    String description) {
                if (lReq == uniqueSeq) {
                    mClientBuiltInHanlder.onFail(errorCode, description);
                }
            }
        });
        return handle;
    }
}
