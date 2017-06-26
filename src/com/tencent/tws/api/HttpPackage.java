
package com.tencent.tws.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/*
 * this class definite the HttpService structure
 */
public class HttpPackage implements Parcelable {
    // fix head:as its name
    public long mSessionID;// 应用内会话ID，唯一
    public String mPackageName;// 应用包名
    public int mPackageType;// 请求或者答复类型，POST OR GET,Reply type

    // uncertain length, bring the real http-request
    public int mStatusCode;// user for return mark
    // public int TTL;//live time
    public String mHttpData;// HTTP包数据
    private HttpResponseExtra mHttpResponseExtra = null;

    public HttpPackage(long mSessionID, String mPackageName, int mPackageType, int statusCode,
            String mHttpData) {
        this.mSessionID = mSessionID;
        this.mPackageName = mPackageName;
        this.mPackageType = mPackageType;
        this.mStatusCode = statusCode;
        this.mHttpData = mHttpData;
    }

    public void setResponseExtra(HttpResponseExtra extra) {
        mHttpResponseExtra = extra;
    }

    public void setSessionId(long mSessionID) {
        this.mSessionID = mSessionID;
    }

    public HttpResponseExtra getResponseExtra() {
        return mHttpResponseExtra;
    }

    public long getSessionId() {
        return mSessionID;
    }

    public void setName(String mPackageName) {
        this.mPackageName = mPackageName;

    }

    public String getName() {
        return mPackageName;
    }

    public void setType(int mPackageType) {
        this.mPackageType = mPackageType;

    }

    public int getType() {
        return mPackageType;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getHttpData() {
        return mHttpData;
    }

    public void setHttpData(String mHttpData) {
        this.mHttpData = mHttpData;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static HttpPackage StringToHttpPackage(String data) {
        long mSessionID;
        String mPackageName;
        int mPackageType;
        int mStatusCode;
        String mHttpData;

        try {
            JSONObject resultJson = new JSONObject(data);

            mSessionID = resultJson.getLong("mSessionID");
            mPackageName = resultJson.getString("mPackageName");
            mPackageType = resultJson.getInt("mPackageType");
            mStatusCode = resultJson.getInt("mStatusCode");
            mHttpData = resultJson.getString("mHttpData");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return null;
        }

        return new HttpPackage(mSessionID, mPackageName, mPackageType, mStatusCode, mHttpData);
    }

    public static String HttpPackageToString(HttpPackage mHttpData) {
        JSONObject resultJson = new JSONObject();

        try {
            resultJson.put("mSessionID", mHttpData.mSessionID);
            resultJson.put("mPackageName", mHttpData.mPackageName);
            resultJson.put("mPackageType", mHttpData.mPackageType);
            resultJson.put("mStatusCode", mHttpData.mStatusCode);
            resultJson.put("mHttpData", mHttpData.mHttpData);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return null;
        }

        return resultJson.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeLong(this.mSessionID);
        dest.writeString(this.mPackageName);
        dest.writeInt(mPackageType);
        dest.writeInt(mStatusCode);
        dest.writeString(mHttpData);
    }

    public static final Parcelable.Creator<HttpPackage> CREATOR = new Parcelable.Creator<HttpPackage>() {
        @Override
        public HttpPackage createFromParcel(Parcel source) {// 从Parcel中读取数据，返回person对象
            if (source == null)
                Log.d("HttpPackage", "error may happen");
            return new HttpPackage(source.readLong(), source.readString(), source.readInt(),
                    source.readInt(), source.readString());
        }

        @Override
        public HttpPackage[] newArray(int size) {
            return new HttpPackage[size];
        }
    };

}
