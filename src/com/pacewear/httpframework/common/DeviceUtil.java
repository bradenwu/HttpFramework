
package com.pacewear.httpframework.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tencent.tws.api.TwsDeviceManager;

public class DeviceUtil {
    public static boolean isNetOn(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    public static boolean isBluetoothOn(Context context) {
        return TwsDeviceManager.getInstance(context).isDeviceConnected();
    }
}
