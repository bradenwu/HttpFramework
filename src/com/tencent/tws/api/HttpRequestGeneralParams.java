
package com.tencent.tws.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class HttpRequestGeneralParams {
    // below represent CONTROL of HTTPUTILS
    public static int CONTROL_TIMEOUT = 0x00000001;// request timeout flag
    public static int CONTROL_CACHETIMEOUT = 0x00000010;// cache timeout ,may not use
    public static int CONTROL_CACHERESULT = 0x00000100;// if cache http-request result
    public static int CONTROL_USERAGENT = 0x00001000;// if use user-agent
    public static int CONTROL_PROXY = 0x00010000; // if setproxy
    // below represent general head of HTTPUTILS
    public static int HEADPART = 0x00010000;
    // below represent general body of HTTPUTILS

    public static int BODYPART = 0x01000000;
    public static int BODYPART_WITHENTITY = 0x10000000;
    public static int BODYMASK = 0x11000000;
    public static final String HEADER_PROXY_URL = "header_proxy_url";
    public static final String HEADER_PROXY_PORT = "header_proxy_port";
    public int mMaskFlag = 0;

    public String UserAgent = null;

    public int cacheTimeOut = 0;

    public int requestTimeOut;

    public String URl;
    private HashMap<String, String> mHeader = new HashMap<String, String>();
    private HashMap<String, String> mBodyparams = new HashMap<String, String>();
    public String mBodyEntity = null;
    public String mBodyEntityStringEncoding = null;

    public int requestType;

    public HttpRequestGeneralParams() {

    }

    public void setUserAgent(String mAgent) {
        this.UserAgent = mAgent;
        mMaskFlag |= CONTROL_USERAGENT;// add userAgent flag
    }

    public void setRequestTimeOut(int time) {
        // http-request timeout
        this.requestTimeOut = time;
        mMaskFlag |= CONTROL_TIMEOUT;
    }

    public void setProxy(String url, int port) {
        mMaskFlag |= CONTROL_PROXY;
        mHeader.put(HEADER_PROXY_URL, url);
        mHeader.put(HEADER_PROXY_PORT, port + "");
    }

    public void setCacheResult(boolean flag, int timeOut) {
        // if you want to reserve http-request result ,set the length of timeout
        if (flag == true) {
            mMaskFlag |= CONTROL_CACHERESULT;
            cacheTimeOut = timeOut;
        }
    }

    public void setRequestType(int type) {
        // post or get request
        this.requestType = type;
    }

    public void addUrl(String url) {
        URl = url;
    }

    public void addHeader(String key, String value) {
        mHeader.put(key, value);
        mMaskFlag |= HEADPART;
    }

    public void addBodyParameter(String key, String value) {
        mBodyparams.put(key, value);
        mMaskFlag |= BODYPART;

        Log.d("xiaohuan", "extract value now " + Integer.toHexString(mMaskFlag & BODYPART));
    }

    public void addBodyEntity(String body) {
        mBodyEntity = body;

        mMaskFlag |= BODYPART_WITHENTITY;

        Log.d("xiaohuan", "should nerver enter this");
    }

    public void addBodyEntity(String body, String bodyEncoding) {
        mBodyEntity = body;
        mBodyEntityStringEncoding = bodyEncoding;
        mMaskFlag |= BODYPART_WITHENTITY;

        Log.d("xiaohuan", "should nerver enter this");
    }

    public HashMap<String, String> getHeader() {
        return mHeader;
    }

    public HashMap<String, String> getBodyParamete() {
        return mBodyparams;
    }

    public static String HttpRequestGeneralParamsToString(HttpRequestGeneralParams mParams) {
        JSONObject jsonObject = new JSONObject();// main object

        // 1.add control part
        try {
            jsonObject.put("ControlPart", mParams.mMaskFlag);

            if ((mParams.mMaskFlag & CONTROL_TIMEOUT) != 0) {
                jsonObject.put("TimeOut", mParams.requestTimeOut);
            }

            if ((mParams.mMaskFlag & CONTROL_CACHERESULT) != 0) {
                jsonObject.put("CacheTimeOut", mParams.cacheTimeOut);
            }

            if ((mParams.mMaskFlag & CONTROL_USERAGENT) != 0) {
                jsonObject.put("UserAgent", mParams.UserAgent);
            }
            // now finish put control part

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 2.add headPart
        if ((mParams.mMaskFlag & HEADPART) != 0) {// head-part flag play effect

            JSONObject jsonHeadArray = new JSONObject();

            for (Map.Entry<String, String> entry : mParams.mHeader.entrySet()) {
                try {
                    jsonHeadArray.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // catch
            } // for

            try {
                jsonObject.put("headpart", jsonHeadArray);// put headPart
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        // 3.add bodyPart
        if ((mParams.mMaskFlag & BODYMASK) != 0) {
            Log.d("xiaohuan",
                    "enter body part " + Integer.toHexString(mParams.mMaskFlag & BODYPART));
            if ((mParams.mMaskFlag & BODYPART_WITHENTITY) != 0) {

                Log.d("xiaohuan", "enter body entity part:"
                        + Integer.toHexString(mParams.mMaskFlag & BODYPART_WITHENTITY));

                try {
                    jsonObject.put("body_string_enty", mParams.mBodyEntity);// put bodyEntity
                    jsonObject.put("body_string_enty_encoding", mParams.mBodyEntityStringEncoding);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {// not bodyEntity
                JSONObject jsonBodyArray = new JSONObject();

                for (Map.Entry<String, String> entry1 : mParams.mBodyparams.entrySet()) {
                    Log.d("xiaohuan", "enter body mParams part");
                    try {
                        jsonBodyArray.put(entry1.getKey(), entry1.getValue());
                        Log.d("xiaohuan", "key:" + entry1.getKey() + "value" + entry1.getValue());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } // for

                try {
                    jsonObject.put("bodypart", jsonBodyArray);// put bodyPart
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } // end else
        } // end if

        // 4.add url and request type now
        try {
            jsonObject.put("url", mParams.URl);
            jsonObject.put("requestType", mParams.requestType);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // url

        return jsonObject.toString();// finish encode String
    }

    public static HttpRequestGeneralParams StringToHttpRequestGeneralParams(String mData) {
        HttpRequestGeneralParams mResult = new HttpRequestGeneralParams();

        try {
            JSONObject resultJson = new JSONObject(mData);

            // 1.extract from control part
            int mMaskFlag = resultJson.getInt("ControlPart");
            mResult.mMaskFlag = mMaskFlag;
            if ((mMaskFlag & CONTROL_TIMEOUT) != 0) {
                mResult.requestTimeOut = resultJson.optInt("TimeOut");
            }

            if ((mMaskFlag & CONTROL_CACHERESULT) != 0) {
                mResult.cacheTimeOut = resultJson.optInt("CacheTimeOut");
            }

            if ((mMaskFlag & CONTROL_USERAGENT) != 0) {
                mResult.UserAgent = resultJson.optString("UserAgent");
            }

            // 2.extract from head part
            if ((mMaskFlag & HEADPART) != 0) {
                JSONObject jsonHeadArray = resultJson.optJSONObject("headpart");

                // now load head part
                Iterator it = jsonHeadArray.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = jsonHeadArray.optString(key);
                    mResult.addHeader(key, value);
                } // while end
            }

            // 3.extract from body part
            if ((mMaskFlag & BODYMASK) != 0) {
                if ((mMaskFlag & BODYPART_WITHENTITY) != 0) {
                    if (TextUtils.isEmpty(resultJson.optString("body_string_enty_encoding")))
                        mResult.addBodyEntity(resultJson.optString("body_string_enty"));// now add
                                                                                        // body
                                                                                        // entity
                    else
                        mResult.addBodyEntity(resultJson.optString("body_string_enty"),
                                resultJson.optString("body_string_enty_encoding"));
                } else {// not body entity
                    JSONObject jsonBodyArray = resultJson.optJSONObject("bodypart");
                    Log.d("xiaohuan", "extract value now");
                    Iterator it1 = jsonBodyArray.keys();
                    while (it1.hasNext()) {
                        String key = (String) it1.next();
                        String value = jsonBodyArray.optString(key);
                        mResult.addBodyParameter(key, value);
                    } // while end

                }
            }

            // 4.extract URL and request type
            mResult.URl = resultJson.optString("url");
            mResult.requestType = resultJson.optInt("requestType");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return null;
        }

        return mResult;
    }

}
