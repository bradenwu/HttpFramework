
package com.pacewear.httpframework.cmdproxy;

import android.content.Context;

import com.pacewear.httpframework.channel.BlueToothChannel;
import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.route.HttpRouter;
import com.pacewear.httpframework.route.IHttpRouter;
import com.qq.taf.jce.JceStruct;

public abstract class BaseHttpIntercept {
    protected IClientHandler mClientHandler = null;
    protected IClientBuiltInHanlder mClientBuiltInHanlder = null;
    private Context mContext = null;

    BaseHttpIntercept(Context context) {
        mContext = context;
    }

    void setClientHandler(IClientHandler handler) {
        mClientHandler = handler;
    }

    void setClientBultInHanlder(IClientBuiltInHanlder hanlder) {
        mClientBuiltInHanlder = hanlder;
    }

    boolean intercept(JceStruct data) {
        IHttpProxyChannel baseChannel = HttpRouter.get().getSelectChannel(mContext);
        if (baseChannel instanceof BlueToothChannel) {
            // 走蓝牙通道的话, 不做拦截
            return false;
        }
        if (mClientHandler != null) {
            mClientHandler.onSelfHandle(data);
            return true;
        }
        // 走http通道
        return onIntercept(data);
    }

    protected abstract boolean onIntercept(JceStruct data);
}
