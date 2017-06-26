
package com.tencent.tws.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseExtra implements Parcelable {
    private Map<String, String> mHeaders = new HashMap<String, String>();

    public HttpResponseExtra() {
    }

    public void setHeaders(Map<String, String> map) {
        mHeaders = map;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeMap(mHeaders);
    }

    public static final Parcelable.Creator<HttpResponseExtra> CREATOR = new Parcelable.Creator<HttpResponseExtra>() {

        @Override
        public HttpResponseExtra createFromParcel(Parcel paramParcel) {
            HttpResponseExtra bean = new HttpResponseExtra();
            bean.mHeaders = paramParcel.readHashMap(HashMap.class.getClassLoader());
            return bean;
        }

        @Override
        public HttpResponseExtra[] newArray(int paramInt) {
            // TODO Auto-generated method stub
            return new HttpResponseExtra[paramInt];
        }
    };
}
