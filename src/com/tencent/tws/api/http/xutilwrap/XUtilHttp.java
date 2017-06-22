
package com.tencent.tws.api.http.xutilwrap;

import com.tencent.tws.api.HttpPackage;

import java.util.ArrayList;
import java.util.List;

public class XUtilHttp {
    public static interface IXUtilHttpCallback {
        public void onCallback(HttpPackage resultData);
    }

    private static final String TAG = "XUtilHttp";

    List<IHttpStreamHandler> mHandlers = null;

    public XUtilHttp() {
        mHandlers = new ArrayList<IHttpStreamHandler>();
        mHandlers.add(new GetBinStreanHandler());
        // handlers.
    }

    public void start(HttpPackage e1, IXUtilHttpCallback listener) {
        for (IHttpStreamHandler handler : mHandlers) {
            if (handler.handle(e1, listener)) {
                // postResultCallback(listener, e1);
                break;
            }
        }
    }

    public static interface IHttpStreamHandler {
        public boolean handle(HttpPackage e1, IXUtilHttpCallback listener);
    }

}
