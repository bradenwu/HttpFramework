
package com.pacewear.httpframework.okhttp.https;

import android.os.Parcel;
import android.os.Parcelable;

import com.pacewear.httpframework.common.InputStreamUtil;

import java.io.InputStream;

public class SSLParcelParam implements Parcelable {
    private String[] mCertInput;
    private String mBksFileInput;
    private String mPwd;

    private SSLParcelParam() {
    }

    public SSLParcelParam(InputStream[] certInputs, InputStream bksFile, String pwd) {
        try {
            if (certInputs != null) {
                mCertInput = new String[certInputs.length];
                int cnt = 0;
                for (InputStream tmpCert : certInputs) {
                    mCertInput[cnt] = InputStreamUtil.inputStream2String(tmpCert);
                    cnt++;
                }
            }
            mBksFileInput = InputStreamUtil.inputStream2String(bksFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPwd = pwd;
    }

    public String[] getCertInput() {
        return mCertInput;
    }

    public String getBksFile() {
        return mBksFileInput;
    }

    public String getPwd() {
        return mPwd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(mCertInput);
        dest.writeString(mBksFileInput);
        dest.writeString(mPwd);
    }

    public static final Parcelable.Creator<SSLParcelParam> CREATOR = new Parcelable.Creator<SSLParcelParam>() {

        @Override
        public SSLParcelParam createFromParcel(Parcel source) {
            SSLParcelParam param = new SSLParcelParam();
            param.mCertInput = source.createStringArray();
            param.mBksFileInput = source.readString();
            param.mPwd = source.readString();
            return param;
        }

        @Override
        public SSLParcelParam[] newArray(int size) {
            return new SSLParcelParam[size];
        }
    };
    // public static
}
