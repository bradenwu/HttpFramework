
package com.pacewear.httpframework.httpframework.okhttp;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.pacewear.httpframework.common.ByteUtil;
import com.pacewear.httpframework.common.FileUtil;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpRequestGeneralParams;
import com.tencent.tws.api.HttpResponseResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpUtil {
    private static final String TAG = "OkHttpUtil";
    private OkHttpClient.Builder mBuilder = null;

    public OkHttpUtil() {
        mBuilder = new OkHttpClient.Builder();
    }

    public void syncCall(HttpPackage e1) {
        Response response = callInternal(e1);
        switch (e1.mPackageType) {
            case HttpRequestCommand.GET_WITH_STREAMRETURN:
            case HttpRequestCommand.POST_WITH_STRAMRETURN:
                e1.mHttpData = onParseInputStream(response);
                break;
            case HttpRequestCommand.GET_TEXT:
                try {
                    e1.mHttpData = response.body().string();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case HttpRequestCommand.POST:
            case HttpRequestCommand.GET_WITH_GENERAL_TEXT:
            case HttpRequestCommand.POST_WITH_GENERAL:
                e1.mHttpData = onParseCommonResponse(response);
                break;
            case HttpRequestCommand.GET_WITH_GENERAL_FILE:
            case HttpRequestCommand.GET_PNG_IMAGE:
                onParseDownloadRsp(e1, response);
                break;
            default:
                break;
        }
    }

    private String onParseInputStream(Response response) {
        ResponseBody body = response.body();
        byte[] bs = ByteUtil.toBytes(body.byteStream());
        return Base64.encodeToString(bs, Base64.DEFAULT);
    }

    private String onParseCommonResponse(Response response) {
        Headers headers = response.headers();
        String strBody = "";
        try {
            strBody = response.body().string();
            return EncodeHttpResonse(headers, strBody);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private void onParseDownloadRsp(HttpPackage e1, Response response) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            long fileName = System.currentTimeMillis();
            String extensionName = FileUtil.getFileExtensionFromUrl(e1.mHttpData);
            String destFileDir = Environment.getExternalStorageDirectory()
                    .getPath() + "/" + fileName + "." + extensionName;
            File file = FileUtil.createFile(destFileDir);
            if (file == null) {
                throw new FileNotFoundException("file create fail");
            }
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            // 如果下载文件成功，第一个参数为文件的绝对路径
        } catch (IOException e) {
            e1.mStatusCode = HttpRequestCommand.NETWORKFAIL_STATUS;
            e1.mHttpData = e.getMessage();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }
    }

    private Response callInternal(HttpPackage e1) {
        HttpRequestGeneralParams param = HttpRequestGeneralParams
                .StringToHttpRequestGeneralParams(e1.mHttpData);
        RequstConvert.parseHttpParam(mBuilder, param, param.mMaskFlag);
        OkHttpClient client = mBuilder.build();
        Request.Builder requestBuilder = new Request.Builder();
        RequstConvert.parseHttpRequest(requestBuilder, param, param.mMaskFlag);
        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            // 解析response
            if (response.isSuccessful()) {
                e1.mStatusCode = -1;
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String EncodeHttpResonse(Headers headers, String strBody) {
        // encode HttpResponce into jsonobject

        JSONObject jsonObject = new JSONObject();// main object
        JSONObject jsonHead = new JSONObject();
        for (int i = 0; i < headers.size(); i++) {
            try {
                jsonHead.put(headers.name(i), headers.value(i));
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
            jsonObject.put("ResponseBody", strBody);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "put responseBody error");
        }

        return jsonObject.toString();
    }
}
