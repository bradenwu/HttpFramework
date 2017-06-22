
package com.tencent.tws.api.http.xutilwrap;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;
import com.tencent.tws.api.http.xutilwrap.XUtilHttp.IHttpStreamHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Pattern;

public class GetTxtHandler extends StreamHandler implements IHttpStreamHandler {

    @Override
    public int onHandle(final HttpPackage e1) {
        if (e1.mPackageType != HttpRequestCommand.GET_WITH_GENERAL_TEXT
                && e1.mPackageType != HttpRequestCommand.GET_WITH_GENERAL_FILE) {
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

        if (e1.mPackageType == HttpRequestCommand.GET_WITH_GENERAL_TEXT) {

            mHttpUtils.send(HttpRequest.HttpMethod.GET, mParams.URl, params,
                    new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            // TODO Auto-generated method stub
                            e1.mStatusCode = HttpRequestCommand.NETWORKFAIL_STATUS;
                            e1.mPackageType = HttpResponseResult.DirectDatas;
                            e1.mHttpData = arg0.getExceptionCode() + arg1;

                            postResultCallback(e1);// now do reply
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> arg0) {
                            // TODO Auto-generated method stub
                            e1.mStatusCode = HttpRequestCommand.NORMAL_STATUS;
                            e1.mPackageType = HttpResponseResult.DirectDatas;

                            Header[] hearArray = arg0.getAllHeaders();

                            e1.mHttpData = EncodeHttpResonse(hearArray,
                                    arg0.result);

                            postResultCallback(e1);// do reply
                        }

                    });// send end

        } else {// download file
            final long mFileName = System.currentTimeMillis();// generate file
                                                              // name

            final String mExtensionName = getFileExtensionFromUrl(mParams.URl);
            if (mExtensionName == null) {
                // when encounter error data,return ahead
                e1.mStatusCode = HttpRequestCommand.NETWORKFAIL_STATUS;
                e1.mPackageType = HttpResponseResult.DirectDatas;
                e1.mHttpData = "unsatified Data send";
                return 0;
            }

            final String mPhoneDir = Environment.getExternalStorageDirectory()
                    .getPath() + "/" + mFileName + "." + mExtensionName;
            Log.i(TAG, "SEND_ FILE FILE_DIR:"
                    + Environment.getExternalStorageDirectory().getPath());
            mHttpUtils.download(mParams.URl, mPhoneDir, params,
                    new RequestCallBack<File>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            // TODO Auto-generated method stub
                            Log.i(TAG, "HttpRequest onFailure paramString:"
                                    + arg1);

                            e1.mStatusCode = HttpRequestCommand.NETWORKFAIL_STATUS;
                            e1.mPackageType = HttpResponseResult.DirectDatas;
                            e1.mHttpData = arg1;
                            postResultCallback(e1);// use link structure, it will
                                                   // not fail
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> arg0) {
                            // TODO Auto-generated method stub
                            sendFile(e1, mPhoneDir, mFileName, mExtensionName);
                        }

                    });

        } // end else
        return 1;
    }

    @Override
    protected String getTAG() {
        return "GetTxtHandler";
    }

    private String EncodeHttpResonse(Header[] hearArray, String mResponseBody) {
        // encode HttpResponce into jsonobject

        JSONObject jsonObject = new JSONObject();// main object
        JSONObject jsonHead = new JSONObject();

        for (int i = 0; i < hearArray.length; i++) {
            try {
                jsonHead.put(hearArray[i].getName(), hearArray[i].getValue());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            jsonObject.put("ResponseHead", jsonHead);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "put responseHead error");
        }

        try {
            jsonObject.put("ResponseBody", mResponseBody);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "put responseBody error");
        }

        return jsonObject.toString();
    }

    public String getFileExtensionFromUrl(String url) {
        // this method do extract extension name from url
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename = 0 <= filenamePos ? url.substring(filenamePos + 1)
                    : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty()
                    && Pattern
                            .matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }
}
