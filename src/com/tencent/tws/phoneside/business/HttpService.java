
package com.tencent.tws.phoneside.business;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import qrom.component.log.QRomLog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.pacewear.httpframework.HttpModule;
import com.pacewear.httpframework.HttpModule.IHttpInvokeCallback;
import com.tencent.tws.api.FileTransferManager;
import com.tencent.tws.api.FileTransferManager.FileTransferListener;
import com.tencent.tws.api.HttpPackage;
import com.tencent.tws.api.HttpRequestCommand;
import com.tencent.tws.api.HttpResponseResult;
import com.tencent.tws.framework.common.MsgSender.MsgSendCallBack;

public class HttpService extends Service {
    public final static String TAG = "HttpService";

    private static String SEND_PKG_NAME = "com.tencent.tws.watchside";

    public static String HTTP_SERVICE_DIR = "HttpServiceDir";

    private AtomicBoolean mNetWorkStatus = new AtomicBoolean(true);

    private AtomicBoolean mBTStatus = new AtomicBoolean(true);
    MyReceiver mInnerBroadcast = new MyReceiver();// inner broadcastRecieve

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            mNetWorkStatus.set(mobileInfo.isConnected()
                    || wifiInfo.isConnected());// set boolean status

        } // 如果无网络连接activeInfo为null

    }

    ExecutorService fixedThreadPool;// thread pool service

    private static final int POOLSIZE = 2;

    public static BlockingQueue<HttpPackage> waitqueue = new LinkedBlockingQueue<HttpPackage>();// keep
                                                                                                // all
                                                                                                // not
                                                                                                // deal
                                                                                                // request

    public static BlockingQueue<HttpPackage> replyqueue = new LinkedBlockingQueue<HttpPackage>();

    private Object mLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        mNetWorkStatus.set(isNetworkConnected(this));// set network status
        registerNetWorkConnect();
        // add by fine because this service is never stop , so no need unbind ;
        monitorWatchConnection();
        Log.d(TAG, "HttpService is start + network " + isNetworkConnected(this));
    }

    private void registerNetWorkConnect() {
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mInnerBroadcast, filter);
    };

    private void unRegisterNetWorkConnect() {
        unregisterReceiver(mInnerBroadcast);
    }

    private final void monitorWatchConnection() {
        QRomLog.d(TAG, "monitorWatchConnection");
        IntentFilter intent = new IntentFilter();
        intent.addAction("Action.Tws.device_connected");
        intent.addAction("Action.Tws.device_active_disconnected");
        intent.addAction("Action.Tws.device_passive_disconnected");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent == null || intent.getAction() == null) {
                    return;
                }

                String action = intent.getAction();
                QRomLog.d(TAG, "monitorWatchConnection onReceive action:"
                        + action);

                if (action.equalsIgnoreCase("Action.Tws.device_connected")) {
                    mBTStatus.set(true);
                    synchronized (mLock) {
                        mLock.notify();
                    }
                    QRomLog.d(TAG, "notify  send thread start ");
                } else if (action.equalsIgnoreCase("Action.Tws.device_active_disconnected")
                        || action.equalsIgnoreCase("Action.Tws.device_passive_disconnected")) {
                    // mHandler.sendEmptyMessage(MSG_WATCH_DISCONNECTED);
                    mBTStatus.set(false);
                }
            }
        }, intent);
    }

    // net work status get
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void init() {

        fixedThreadPool = Executors.newFixedThreadPool(POOLSIZE);

        fixedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    doDealReplyMessage();
                } // while looper,just search
            }
        });

        fixedThreadPool.execute(new Runnable() {

            // use one thread to reply watch side
            @Override
            public void run() {
                while (true) {
                    doSendHttpBackMessage();
                }
            }

        });

    }

    private static void doAddHttpRequestMessage(String data) {
        // add HttpPackage that comes from watchside ,this function will no be
        // blocked
        final HttpPackage e = HttpPackage.StringToHttpPackage(data);

        if (e == null)
            return;

        waitqueue.add(e);

        Log.i(TAG, "doAddHttpRequestMessage is ok");
    }

    private void doDealReplyMessage() {
        // this function may block
        HttpPackage e1 = null;
        try {
            e1 = waitqueue.take();
        } catch (InterruptedException e2) {
            e2.printStackTrace();

            return;
        }

        final HttpPackage e = e1;

        if (e == null)
            return;

        // fail with netWorkStatus
        if (mNetWorkStatus.get() == false) {
            e.setStatusCode(HttpRequestCommand.NETWORKFAIL_STATUS);
            e.setReplyType(HttpResponseResult.DirectDatas);
            e.setHttpData("current network is not working,please check your phone");

            replyqueue.offer(e);// just do reply
            return;
        }

        // now do real http-opearation
        HttpModule.invokeHttp(getApplicationContext(), e, new IHttpInvokeCallback() {

            @Override
            public void onCallback(HttpPackage resultData) {
                sendReplyWithFile(resultData);
            }
        });
    }

    private void sendReplyWithFile(HttpPackage e) {
        // TODO 已经下载了的数据？？
        if (e.getReplyType() == HttpResponseResult.isFileDatas) {
            String targetFile = e.getHttpData();
            int len = targetFile.length();
            String fileName = targetFile.substring(targetFile.lastIndexOf("/") + 1, len);
            int fileNameLen = fileName.length();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileNameLen);
            sendFile(e, targetFile, fileName, extension);
            return;
        }
        if (e.getHttpData().length() >= 16 * 1024) {
            // if we encounter big body ,so transfer with file
            final long mFileName = System.currentTimeMillis();// generate file
                                                              // name
            final String mPhoneDir = Environment.getExternalStorageDirectory()
                    .getPath() + "/" + mFileName + "." + "txt";

            File file = new File(mPhoneDir);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                InputStream in = new ByteArrayInputStream(
                        e.getHttpData().getBytes());
                OutputStream out = new FileOutputStream(mPhoneDir);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            sendFile(e, mPhoneDir, mFileName + "", "txt");// send to destination
        } else {
            Log.d(TAG, "run direct is right");
            replyqueue.offer(e);
        }
    }

    private void doSendHttpBackMessage() {
        // send http-package reply, this function will be blocked

        synchronized (mLock) {
            try {
                if (!mBTStatus.get()) {
                    Log.d(TAG, " doSendHttpBackMessage  sleep start !   ");
                    mLock.wait();
                    Log.d(TAG, " doSendHttpBackMessage  restart again  !   ");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final HttpPackage e;

        try {
            e = replyqueue.take();
        } catch (InterruptedException e1) {
            e1.printStackTrace();

            return;
        }

        if (e == null)
            return;
        // get null object ,should return it

        boolean sucess = sendData(HttpPackage.HttpPackageToString(e),
                new MsgSendCallBack() {

                    @Override
                    public void onSendResult(boolean bSuc, long lSendReqId) {
                        if (!bSuc) {
                            replyqueue.offer(e);
                            QRomLog.w(TAG, "send msg to dma  error "
                                    + e.getHttpData());
                        }
                    }

                    @Override
                    public void onLost(int nReason, long lReqId) {
                        replyqueue.offer(e);
                        QRomLog.w(TAG, "send msg to dma  error " + e.getHttpData());
                    }
                });// send to watchside
        if (!sucess) {
            replyqueue.offer(e);
        }
    }

    public static void getWatchSideMessage(String data) {
        doAddHttpRequestMessage(data);
    }

    private void DebugUser(String packagePath) {
        // this function to delete user download in phone sdcard
        File f = new File(packagePath);
        if (f.exists()) {
            f.delete();
        }
    }

    private void sendFile(final HttpPackage e, final String sourcePath,
            final String mFileName, final String mExtensionName) {
        FileTransferManager.getInstance(HttpService.this).sendFile(sourcePath,
                HTTP_SERVICE_DIR, new FileTransferListener() {
                    @Override
                    public void onTransferComplete(long requestId,
                            String filePath) {
                        if (e.getType() != HttpRequestCommand.GET_WITH_STREAMRETURN
                                && e.getType() != HttpRequestCommand.POST_WITH_STRAMRETURN) {
                            e.setReplyType(HttpResponseResult.inDirectDatas);
                        }
                        e.setHttpData(HTTP_SERVICE_DIR + "/" + mFileName + "."
                                + mExtensionName);// dir_path is fix dir_path
                                                  // plus
                                                  // time_path.mExtensionName
                        e.setStatusCode(HttpRequestCommand.NORMAL_STATUS);

                        Log.i(TAG, "onTransfer is ok");
                        replyqueue.offer(e);// use link structure, it will not
                                            // fail

                        DebugUser(sourcePath);// huanxxiao add,should remove
                                              // sourch dir
                        // sendData(HttpPackage.HttpPackageToString(e));
                    }

                    @Override
                    public void onTransferError(long requestId,
                            String fileName, int errorCode) {
                        Log.i(TAG, "onTransferError");
                        if (e.getType() != HttpRequestCommand.GET_WITH_STREAMRETURN
                                && e.getType() != HttpRequestCommand.POST_WITH_STRAMRETURN) {
                            e.setReplyType(HttpResponseResult.inDirectDatas);
                        }
                        e.setHttpData("NONE");
                        e.setStatusCode(HttpRequestCommand.NETWORKFAIL_STATUS);

                        replyqueue.offer(e);// use link structure, it will not
                                            // fail

                        DebugUser(sourcePath);// huanxxiao add,should remove
                                              // source dir
                        // sendData(HttpPackage.HttpPackageToString(e));
                    }

                    @Override
                    public void onTransferCancel(long requestId, int reason) {

                    }

                    @Override
                    public void onTransferProgress(long requestId,
                            String fileName, long progress) {

                    }

                });
    }

    private boolean sendData(String result, MsgSendCallBack callcaBack) {
        int mResult = RawDataSendPhone.sendMessageToWatchSide(result,
                callcaBack);

        if (mResult == 0) {
            Log.i(TAG, "sendData is ok");
            return true;
        } else {
            Log.i(TAG, "sendData is bad");
            return false;
        }
    }

    @Override
    public void onDestroy() {
        // PassThroughManager.getInstance(this).unRegDataRecver();

        unRegisterNetWorkConnect();

        fixedThreadPool.shutdown();// stop thread-pool

        waitqueue.clear();
        replyqueue.clear();// package clear

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isHttpServiceReady() {
        return true;
    }

}
