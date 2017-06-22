
package com.pacewear.httpframework.watchproxy;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.route.IHttpRouter;
import com.qq.taf.jce.JceStruct;

public abstract class BaseHttpIntercept {
    private IHttpRouter mHttpRouter = null;
    protected IClientHandler mClientHandler = null;
    private boolean mSelfHandle = false;

    public BaseHttpIntercept(boolean selfHandle) {
        mSelfHandle = selfHandle;
    }

    public void setClientHandler(IClientHandler handler) {
        mClientHandler = handler;
    }

    public boolean intercept(JceStruct data) {
        IHttpProxyChannel baseChannel = mHttpRouter.getSelectChannel();
        if (baseChannel != null || mClientHandler == null) {
            // 走蓝牙通道的话, 不做拦截
            return false;
        }
        if (mSelfHandle) {
            mClientHandler.onSelfHandle(data);
            return true;
        }
        // 走http通道
        return onIntercept(data);
    }

    protected abstract boolean onIntercept(JceStruct data);
}
