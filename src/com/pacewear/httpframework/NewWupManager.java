
package com.pacewear.httpframework;

import android.content.Context;

import com.pacewear.httpframework.httpserver.wupserver.WupHttpClient;

import qrom.component.wup.QRomComponentWupManager;

public abstract class NewWupManager extends QRomComponentWupManager {
    @Override
    public synchronized void startup(Context context) {
        super.startup(context);
        setHttpClient(new WupHttpClient(context));
    }
}
