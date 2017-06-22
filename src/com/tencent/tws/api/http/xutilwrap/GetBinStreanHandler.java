
package com.tencent.tws.api.http.xutilwrap;

import android.util.Base64;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.http.utils.ByteUtil;
import com.tencent.tws.api.http.xutilwrap.XUtilHttp.IHttpStreamHandler;

public class GetBinStreanHandler extends StreamHandler implements IHttpStreamHandler {

    @Override
    public int onHandle(HttpPackage e1) {
        if (e1.mPackageType != HttpRequestCommand.GET_WITH_STREAMRETURN) {
            return -1;
        }
        Log.i(TAG, "enter dealWithGetBinaryStream success POST result");
        HttpRequestGeneralParams mParams = HttpRequestGeneralParams
                .StringToHttpRequestGeneralParams(e1.mHttpData);

        if (mParams == null)
            return -1;

        int mMaskFlag = mParams.mMaskFlag;
        HttpUtils mHttpUtils = generateHttpUtils(mMaskFlag, mParams); // get
                                                                      // httpUtils

        RequestParams params = generateRequestParams(mParams, mMaskFlag);// get
                                                                         // RequestParams

        try {
            byte[] data = null;
            String mBase64Str;

            ResponseStream is = mHttpUtils.sendSync(HttpRequest.HttpMethod.GET,
                    mParams.URl, params);
            data = ByteUtil.toBytes(is);
            Log.i(TAG, "raw result is " + data + "is " + is.getStatusCode());

            if (data != null && data.length > 0) {
                mBase64Str = Base64.encodeToString(data, Base64.DEFAULT);// convert
                                                                         // byte[]
                                                                         // to
                                                                         // Base64
                e1.mHttpData = mBase64Str;
                Log.i(TAG, "encode string  result is " + mBase64Str);
            }
            if (is.getStatusCode() == 200) {// ok result
                e1.mStatusCode = HttpRequestCommand.NORMAL_STATUS;
            } else {// fail result
                e1.mStatusCode = HttpRequestCommand.NETWORKFAIL_STATUS;
                // e1.mPackageType = HttpResponseResult.DirectDatas; //binary
                // stream only return direct data
            }

        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            e1.mStatusCode = HttpRequestCommand.NETWORKFAIL_STATUS;
            e1.mHttpData = e.toString();
        }
        return 0;
    }

    @Override
    protected String getTAG() {
        return "GetBinStreanHandler";
    }

}
