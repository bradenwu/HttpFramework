package com.tencent.tws.api;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

public class PhoneNetworkProvider extends ContentProvider {
	public static final String METHOD_GET_PHONE_NETWORK_TYPE = "method_get_phone_network_type";
	public static final String METHOD_GET_HTTP_CHANNEL = "method_get_http_channel";
	public static final String METHOD_SET_HTTP_CHANNEL = "method_set_http_channel";
	public static final String EXTRA_PHONE_NETWORK_TYPE = "extra_phone_network_type";
	public static final String EXTRA_HTTP_CHANNEL = "extra_http_channel";
	public static final Uri PHONE_NETWORK_URI = Uri.parse("content://com.tencent.tws.api.phone");
	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public Bundle call(String method, String apiName, Bundle extras) {
		if (TextUtils.equals(method, METHOD_GET_PHONE_NETWORK_TYPE)) {
			Bundle data = new Bundle();
			data.putInt(EXTRA_PHONE_NETWORK_TYPE, PhoneNetworkSharedPrefer.getInstance(getContext())
					.getPhoneNetworkType());
			return data;
        } else if (TextUtils.equals(method, METHOD_GET_HTTP_CHANNEL)) {
            Bundle data = new Bundle();
            data.putInt(EXTRA_HTTP_CHANNEL,
                    PhoneNetworkSharedPrefer.getInstance(getContext()).getHttpChannel(apiName));
            return data;
        } else if(TextUtils.equals(method, METHOD_SET_HTTP_CHANNEL)) {
            int httpChannel = extras.getInt(EXTRA_HTTP_CHANNEL, 0);
            PhoneNetworkSharedPrefer.getInstance(getContext()).setHttpChannel(apiName,httpChannel);
            return null;
        }
		return super.call(method, apiName, extras);
	}
}
