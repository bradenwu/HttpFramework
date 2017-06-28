package com.tencent.tws.api;

import android.content.Context;
import android.content.SharedPreferences;

public class PhoneNetworkSharedPrefer {
	private static final String TAG = "PhoneNetworkSharedPrefer";
	private static final String KEY_PHONE_NET_TYPE = "phone_net_type";
	private static final String KEY_HTTP_CHANNEL_PREX = "http_channel_";
	private static final String KEY_IS_BT_CONNECTED = "is_bt_connected";
	private static PhoneNetworkSharedPrefer mInstance;
	private final Context mContext;

	public static synchronized PhoneNetworkSharedPrefer getInstance(Context context) {
		if (context == null)
			throw new NullPointerException("context is null");

		if (context.getApplicationContext() == null)
			throw new NullPointerException("application context is null");

		if (mInstance == null)
			mInstance = new PhoneNetworkSharedPrefer(context);

		return mInstance;
	}

	private PhoneNetworkSharedPrefer(Context context) {
		mContext = context;
	}

	public void setPhoneNetworkType(int networkType) {
		SharedPreferences sp = mContext.getSharedPreferences(TAG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(KEY_PHONE_NET_TYPE, networkType);
		editor.commit();
	}

	public int getPhoneNetworkType() {
		SharedPreferences sp = mContext.getSharedPreferences(TAG, Context.MODE_WORLD_WRITEABLE);
		return sp.getInt(KEY_PHONE_NET_TYPE, 0);
	}

    public void setHttpChannel(String packageName, int httpChannel) {
        SharedPreferences sp = mContext.getSharedPreferences(TAG, Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_HTTP_CHANNEL_PREX + packageName, httpChannel);
        editor.commit();
    }

    public int getHttpChannel(String packageName) {
        SharedPreferences sp = mContext.getSharedPreferences(TAG, Context.MODE_WORLD_WRITEABLE);
        return sp.getInt(KEY_HTTP_CHANNEL_PREX + packageName, 0);
    }

	public void setBtConnected(boolean isConnected) {
		SharedPreferences sp = mContext.getSharedPreferences(TAG, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(KEY_IS_BT_CONNECTED, isConnected);
		editor.commit();
	}

	public boolean isBtConnected() {
		SharedPreferences sp = mContext.getSharedPreferences(TAG, Context.MODE_WORLD_WRITEABLE);
		return sp.getBoolean(KEY_IS_BT_CONNECTED, false);
	}
}
