
package com.pacewear.httpframework.core;

import android.content.Context;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.route.HttpRouter;
import com.pacewear.httpframework.route.IHttpRouter;

public abstract class BaseHttpClient<Rsp, Param, Post>
        implements IHttpClient<Rsp, Param, Post>, IHttpRequestAction<Rsp, Param, Post> {
    private Context mContext = null;

    public BaseHttpClient(Context context) {
        mContext = context;
    }

    @Override
    public Rsp execute(Param param, Post post) {
        IHttpProxyChannel<Rsp, Param, Post> channel = HttpRouter.get()
                .<Rsp, Param, Post> getSelectChannel(mContext);
        if (channel != null) {
            return channel.transmit(param, post);
        }
        return onExecute(param, post);
    }

}
