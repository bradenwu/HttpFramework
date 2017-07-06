
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
    private long mSessionID;// 应用内会话ID，唯一
    private String mPackageName;// 应用包名
    private int mPackageType;// 请求或者答复类型，POST OR GET,Reply type
    private int mReplyType = 0;
    // uncertain length, bring the real http-request
    private int mStatusCode;// user for return mark
    // public int TTL;//live time
    private String mHttpData;// HTTP包数据
    private String mHttpResponseExtra = null;

    public HttpPackage(long mSessionID, String mPackageName, int mPackageType, int replyType,
            int statusCode,
            String mHttpData, String extra) {
        this.mSessionID = mSessionID;
        this.mPackageName = mPackageName;
        this.mPackageType = mPackageType;
        this.mReplyType = replyType;
        this.mStatusCode = statusCode;
        this.mHttpData = mHttpData;
        this.mHttpResponseExtra = extra;
    }

    public void setResponseExtra(String extra) {
        mHttpResponseExtra = extra;
    }

    public String getResponseExtra() {
        return mHttpResponseExtra;
    }

    public void setSessionId(long mSessionID) {
        this.mSessionID = mSessionID;
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

    public void setReplyType(int type) {
        mReplyType = type;
    }

    public int getReplyType() {
        return mReplyType;
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
        int mReplyType;
        int mStatusCode;
        String mHttpData;
        String mExtra;
        try {
            JSONObject resultJson = new JSONObject(data);
            mSessionID = resultJson.optLong("mSessionID");
            mPackageName = resultJson.optString("mPackageName");
            mPackageType = resultJson.optInt("mPackageType");
            mReplyType = resultJson.optInt("mReplyType");
            mStatusCode = resultJson.optInt("mStatusCode");
            mHttpData = resultJson.optString("mHttpData");
            mExtra = resultJson.optString("mExtra");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return null;
        }

        return new HttpPackage(mSessionID, mPackageName, mPackageType, mReplyType, mStatusCode,
                mHttpData, mExtra);
    }

    public static String HttpPackageToString(HttpPackage mHttpData) {
        JSONObject resultJson = new JSONObject();

        try {
            resultJson.put("mSessionID", mHttpData.mSessionID);
            resultJson.put("mPackageName", mHttpData.mPackageName);
            resultJson.put("mPackageType", mHttpData.mPackageType);
            resultJson.put("mReplyType", mHttpData.mReplyType);
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
        dest.writeInt(mReplyType);
        dest.writeInt(mStatusCode);
        dest.writeString(mHttpData);
    }

    public static final Parcelable.Creator<HttpPackage> CREATOR = new Parcelable.Creator<HttpPackage>() {
        @Override
        public HttpPackage createFromParcel(Parcel source) {// 从Parcel中读取数据，返回person对象
            if (source == null)
                Log.d("HttpPackage", "error may happen");
            return new HttpPackage(source.readLong(), source.readString(), source.readInt(),
                    source.readInt(),
                    source.readInt(), source.readString(), source.readString());
        }

        @Override
        public HttpPackage[] newArray(int size) {
            return new HttpPackage[size];
        }
    };

}
