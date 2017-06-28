package com.tencent.tws.api;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class PhoneNetworkManager {
	private static final String TAG = "PhoneNetworkManager";
	/**
	 * 网络类型无，没有连接任何网络
	 */
	public static final int NET_TYPE_NONE = 0;
	/**
	 * 网络类型wifi
	 */
	public static final int NET_TYPE_WIFI = 1;
	/**
	 * 网络类型移动网络
	 */
	public static final int NET_TYPE_MOBILE = 2;
	
	private static PhoneNetworkManager mInstance;
	private final Context mContext;
	private PhoneNetworkStatusChangedListener mPhoneNetworkStatusChangedListener;
	private ContentObserver mPhoneNetObserver;

	public static synchronized PhoneNetworkManager getInstance(Context context) {
		if (context == null)
			throw new NullPointerException("context is null");

		if (context.getApplicationContext() == null)
			throw new NullPointerException("application context is null");

		if (mInstance == null)
			mInstance = new PhoneNetworkManager(context);

		return mInstance;
	}

	private PhoneNetworkManager(Context context) {
		mContext = context;
	}


	/**
	 * 判断当前手机是否联网的接口
	 * @return true 如果手机有可用的网络连接
	 */
	public boolean isPhoneOnline() {
		int netType = getPhoneNetworkType();
		return netType == 0 ? false : true;
	}

	/**
	 * 获取当前手机手机连接的网络类型
	 * @return 返回当前手机连接的网络类型 
	 *         {@link #NET_TYPE_NONE} 
	 *         {@link #NET_TYPE_WIFI}
	 *         {@link #NET_TYPE_MOBILE}
	 */
	public int getPhoneNetworkType() {
		Bundle data = null;
		try {
			data = mContext.getContentResolver().call(Uri.parse("content://com.tencent.tws.api.phone"),
					PhoneNetworkProvider.METHOD_GET_PHONE_NETWORK_TYPE, "", null);
			Log.e(TAG, "data is null ? "+ (data==null));
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("isPhoneOnline", e.getMessage());
		}
		return data != null ? data.getInt(PhoneNetworkProvider.EXTRA_PHONE_NETWORK_TYPE, 0) : 0;
	}

    public int getHttpChannel() {
        Bundle data = null;
        try {
            data = mContext.getContentResolver().call(
                    Uri.parse("content://com.tencent.tws.api.phone"),
                    PhoneNetworkProvider.METHOD_GET_HTTP_CHANNEL, mContext.getPackageName(), null);
            Log.e(TAG, "data is null ? " + (data == null));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("getHttpChannel", e.getMessage());
        }
        return data != null ? data.getInt(PhoneNetworkProvider.EXTRA_HTTP_CHANNEL, 0) : 0;
    }

    public void setHttpChannel(int channel) {
        try {
            Bundle data = null;
            data.putInt(PhoneNetworkProvider.EXTRA_HTTP_CHANNEL, channel);
            mContext.getContentResolver().call(
                    Uri.parse("content://com.tencent.tws.api.phone"),
                    PhoneNetworkProvider.METHOD_SET_HTTP_CHANNEL, mContext.getPackageName(), data);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setHttpChannel", e.getMessage());
        }
    }

	/** 注册手机网络状态变化的监听
	 * @param listener 当前手机网络状态变化时，返回的监听接口
	 */
	public void setOnPhoneNetworkStatusChangedListener(PhoneNetworkStatusChangedListener listener) {
		if (listener == null) {
			this.mPhoneNetworkStatusChangedListener = null;
			if (mPhoneNetObserver != null) {
				mContext.getContentResolver().unregisterContentObserver(mPhoneNetObserver);
				mPhoneNetObserver = null;
			}
			return;
		}

		this.mPhoneNetworkStatusChangedListener = listener;

		if (mPhoneNetObserver == null) {
			mPhoneNetObserver = new ContentObserver(new Handler()) {
				@Override
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);
					int networkType = getPhoneNetworkType();
					if (mPhoneNetworkStatusChangedListener != null) {
						boolean isOnline = (networkType == 0 ? false : true);
						mPhoneNetworkStatusChangedListener.onPhoneNetworkStatusChanged(isOnline, networkType);
					}
				}
			};
			mContext.getContentResolver().registerContentObserver(PhoneNetworkProvider.PHONE_NETWORK_URI, true,
					mPhoneNetObserver);
		}
	}

	public interface PhoneNetworkStatusChangedListener {
		
		/**
		 * @param isOnline  返回当前手机是否有网络可用
		 * @param networkType  返回当前手机的网络类型   {@link #NET_TYPE_NONE}  {@link #NET_TYPE_WIFI}  {@link #NET_TYPE_MOBILE}
		 */
		public abstract void onPhoneNetworkStatusChanged(boolean isOnline, int networkType);
	}

}
