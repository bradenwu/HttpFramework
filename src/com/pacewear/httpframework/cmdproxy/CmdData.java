
package com.pacewear.httpframework.cmdproxy;

import android.os.Parcel;
import android.os.Parcelable;

import com.qq.taf.jce.JceStruct;

public class CmdData implements Parcelable {
    public static final Parcelable.Creator<CmdData> CREATOR = new Parcelable.Creator<CmdData>() {

        @Override
        public CmdData createFromParcel(Parcel paramParcel) {
            JceStruct data = (JceStruct) paramParcel.readSerializable();
            return new CmdData(data);
        }

        @Override
        public CmdData[] newArray(int paramInt) {
            return new CmdData[paramInt];
        }
    };
    private JceStruct mData = null;

    public CmdData(JceStruct data) {
        mData = data;
    }

    public JceStruct getData() {
        return mData;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeSerializable(mData);
    }

}
