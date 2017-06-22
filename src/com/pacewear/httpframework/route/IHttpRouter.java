
package com.pacewear.httpframework.route;

import com.pacewear.httpframework.channel.IBaseChannel;

public interface IHttpRouter {
    public IBaseChannel getSelectChannel();
}
