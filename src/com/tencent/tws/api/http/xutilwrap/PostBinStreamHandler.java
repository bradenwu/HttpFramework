
package com.tencent.tws.api.http.xutilwrap;

import android.util.Base64;

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

public class PostBinStreamHandler extends StreamHandler {

    @Override
    public int onHandle(HttpPackage e1) {

        if (e1.mPackageType != HttpRequestCommand.POST_WITH_STRAMRETURN) {
            return -1;
        }

        HttpRequestGeneralParams mParams = HttpRequestGeneralParams
                .StringToHttpRequestGeneralParams(e1.mHttpData);

        if (mParams == null)
            return -1;
        int mMaskFlag = mParams.mMaskFlag;
        HttpUtils mHttpUtils = generateHttpUtils(mMaskFlag, mParams);

        RequestParams params = generateRequestParams(mParams, mMaskFlag);// get
                                                                         // RequestParams

        try {
            byte[] data = null;
            String mBase64Str;

            ResponseStream is = mHttpUtils.sendSync(
                    HttpRequest.HttpMethod.POST, mParams.URl, params);
            data = ByteUtil.toBytes(is);// we get byte array
            mBase64Str = Base64.encodeToString(data, Base64.DEFAULT);// convert
                                                                     // byte[]
                                                                     // to
                                                                     // Base64
            e1.mHttpData = mBase64Str;

            if (is.getStatusCode() == 200) {// sucess result here
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
        // TODO Auto-generated method stub
        return "PostBinStreamHandler";
    }

}
