
package com.pacewear.httpframework.core;

import android.content.Context;
import android.util.Log;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.common.Constants;
import com.pacewear.httpframework.route.HttpRouter;

public abstract class BaseHttpClient<Rsp, Param, Post>
        implements IHttpClient<Rsp, Param, Post>, IHttpRequestAction<Rsp, Param, Post> {
    private Context mContext = null;
    private boolean mInvokeDirect = false;

    public BaseHttpClient(Context context) {
        mContext = context;
    }

    protected void setInvokeDirect(boolean direct) {
        this.mInvokeDirect = direct;
    }

    @Override
    public Rsp execute(Param param, Post post) {
        IHttpProxyChannel<Rsp, Param, Post> channel = HttpRouter.get()
                .<Rsp, Param, Post> getSelectChannel(mContext);
        Log.e(Constants.TAG, "execute..");
        if (channel != null && !mInvokeDirect) {
            Log.d(Constants.TAG, "channel is not null");
            channel.setClient(this);
            return channel.transmit(param, post);
        }
        return onExecute(param, post);
    }

}
