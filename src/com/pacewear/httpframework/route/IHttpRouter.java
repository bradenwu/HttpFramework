
package com.pacewear.httpframework.route;

import android.content.Context;

import com.pacewear.httpframework.channel.IHttpProxyChannel;
import com.pacewear.httpframework.core.IHttpClient;
import com.tencent.tws.api.HttpPackage;

public interface IHttpRouter {
    public static final int DEFAULT = 0; // 系统自由选择
    public static final int NET_FIRST = 1; // 网络优先
    public static final int BT_FIRST = 2;// 蓝牙优先
    public static final int NET_ONLY = 3;// 只限网络
    public static final int BT_ONLY = 4;// 只限蓝牙

    public <Rsp, Param, Post> IHttpProxyChannel<Rsp, Param, Post> getSelectChannel(Context context);

}
