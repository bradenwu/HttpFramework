
package com.pacewear.httpframework.okhttp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;

public class HeadersBuilder implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<String> namesAndValues = new ArrayList<String>();

    public HeadersBuilder(Headers headers) {
        namesAndValues = Arrays.asList(headers.namesAndValues);
    }

    public HeadersBuilder(List<String> list) {
        namesAndValues = list;
    }

    public List<String> getHeaders() {
        return namesAndValues;
    }

    // @Override
    // public int describeContents() {
    // return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    // dest.writeStringList(namesAndValues);
    // }
    //
    // public static final Parcelable.Creator<HeadersBuilder> CREATOR = new
    // Parcelable.Creator<HeadersBuilder>() {
    //
    // @Override
    // public HeadersBuilder createFromParcel(Parcel source) {
    // HeadersBuilder builder = new HeadersBuilder();
    // builder.namesAndValues = source.createStringArrayList();
    // return builder;
    // }
    //
    // @Override
    // public HeadersBuilder[] newArray(int size) {
    // return new HeadersBuilder[size];
    // }
    // };

    public static Headers toHeaders_Builder(HeadersBuilder srcBuilder) {
        Headers.Builder targetBuilder = new Headers.Builder();
        List<String> list = srcBuilder.getHeaders();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i += 2) {
                targetBuilder.add(list.get(i), list.get(i + 1));
            }
        }
        return targetBuilder.build();
    }
}
