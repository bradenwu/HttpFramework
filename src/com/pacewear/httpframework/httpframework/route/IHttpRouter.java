
package com.pacewear.httpframework.httpframework.route;

import com.pacewear.httpframework.httpframework.channel.IBaseChannel;

public interface IHttpRouter {
    public IBaseChannel getSelectChannel();
}
