
package com.pace.httpframework.route;

import com.pace.httpframework.channel.IBaseChannel;

public interface IHttpRouter {
    public IBaseChannel getSelectChannel();
}
