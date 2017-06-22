
package com.tencent.tws.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class FileTransferManager {
    private static final String TAG = "FileTransferManager";
    private static FileTransferManager mInstance;
    private final Context mContext;
    private final Handler mUiHandler;
    private static final Object sLock = new Object();

    private static TaskChannelManager sTaskChannelManager;

    public static final String FILE_SERVICE_ACTION = "com.tencent.tws.commonbusiness.FileTransferService";

    private static final int FILE_SERVICE_BIND_FLAGS;

    private static final int TASK_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000;
    private static final int TASK_CHANNEL_RETRY_MAX_COUNT = 6;

    static {
        FILE_SERVICE_BIND_FLAGS = Context.BIND_AUTO_CREATE;
    }

    public static synchronized FileTransferManager getInstance(Context context) {
        if (context == null)
            throw new NullPointerException("context is null");

        if (context.getApplicationContext() == null)
            throw new NullPointerException("application context is null");

        if (mInstance == null)
            mInstance = new FileTransferManager(context);

        return mInstance;
    }

    private FileTransferManager(Context context) {
        this.mContext = context;
        this.mUiHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * @Method: sendFile
     * @Description: 发送文件
     * @param filePath ，源文件路径
     * @param dstPath ，目标设备文件目录，这里默认是null，代表默认放到/sdcard/twsReceived目录下
     * @返回类型：int
     */
    public void sendFile(String filePath, FileTransferListener listener) {
        sendFile(filePath, null, listener);
    }

    /**
     * @Method: sendFile
     * @Description: 发送文件
     * @param filePath ，源文件路径
     * @param dstPath ，目标设备文件目录，属于子目录，非完整文件路径
     * @返回类型：int
     */
    public void sendFile(String filePath, String dstPath, FileTransferListener listener) {
        pushTaskChannelQueue(new FileTransferTask(filePath, dstPath, listener));
    }

    public void cancel(String filePath) {
        pushTaskChannelQueue(new FileTransferCancelTask(filePath));
    }

    private interface Task {
        public void send(IFileTransferService service) throws RemoteException;
    }

    private class FileTransferTask implements Task {
        final String filePath;
        final String dstPath;
        final IFileTransferListener iListener;
        final FileTransferListener listener;

        public FileTransferTask(String filePath, String dstPath, FileTransferListener listener) {
            this.filePath = filePath;
            this.dstPath = dstPath;
            this.listener = listener;
            this.iListener = createIFileTransferListener(listener);
        }

        @Override
        public void send(IFileTransferService service) throws RemoteException {
            service.sendFile(filePath, dstPath, iListener);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("FileTransferTask[");
            sb.append("filePath:").append(filePath);
            sb.append(", dstPath:").append(dstPath);
            sb.append(", listener:").append(listener);
            sb.append("]");
            return sb.toString();
        }

        private IFileTransferListener createIFileTransferListener(
                final FileTransferListener listener) {
            IFileTransferListener iListener = new IFileTransferListener.Stub() {
                @Override
                public void onTransferProgress(final long requestId, final String fileName,
                        final long progress) throws RemoteException {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onTransferProgress(requestId, fileName, progress);
                        }
                    });
                }

                @Override
                public void onTransferError(final long requestId, final String fileName,
                        final int errorCode) throws RemoteException {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onTransferError(requestId, fileName, errorCode);
                        }
                    });
                }

                @Override
                public void onTransferComplete(final long requestId, final String filePath)
                        throws RemoteException {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onTransferComplete(requestId, filePath);
                        }
                    });
                }

                @Override
                public void onTransferCancel(final long requestId, final int reason)
                        throws RemoteException {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onTransferCancel(requestId, reason);
                        }
                    });
                }
            };

            return iListener;
        }
    }

    private class FileTransferCancelTask implements Task {
        final String filePath;

        public FileTransferCancelTask(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void send(IFileTransferService service) throws RemoteException {
            service.cancel(filePath);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("FileTransferCancelTask[");
            sb.append("filePath:").append(filePath);
            sb.append("]");
            return sb.toString();
        }
    }

    /**
     * Push a lbs task for distribution to task side channels.
     */
    private void pushTaskChannelQueue(Task task) {
        synchronized (sLock) {
            if (sTaskChannelManager == null) {
                sTaskChannelManager = new TaskChannelManager(mContext.getApplicationContext());
            }
        }
        sTaskChannelManager.queueTask(task);
    }

    /**
     * Helper class to manage a queue of pending tasks to send to lbs service channel listeners.
     */
    private static class TaskChannelManager implements Handler.Callback, ServiceConnection {
        private static final int MSG_QUEUE_TASK = 0;
        private static final int MSG_SERVICE_CONNECTED = 1;
        private static final int MSG_SERVICE_DISCONNECTED = 2;
        private static final int MSG_RETRY_LISTENER_QUEUE = 3;

        private static final String KEY_BINDER = "binder";

        private final Context mContext;
        private final HandlerThread mHandlerThread;
        private final Handler mHandler;

        private final Map<ComponentName, ListenerServieRecord> mRecordMap = new HashMap<ComponentName, ListenerServieRecord>();
        // private Set<String> mCachedEnabledPackages = new HashSet<String>();

        public TaskChannelManager(Context context) {
            mContext = context;
            mHandlerThread = new HandlerThread("FileTransferManager");
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper(), this);
        }

        /**
         * Queue a new task to be sent to all listeners. This function can be called from any
         * thread.
         */
        public void queueTask(Task task) {
            mHandler.obtainMessage(MSG_QUEUE_TASK, task).sendToTarget();
        }

        @Override
        public boolean handleMessage(Message msg) {
            Log.v(TAG, "handleMessage ============ " + msg.what);
            switch (msg.what) {
                case MSG_QUEUE_TASK:
                    handleQueueTask((Task) msg.obj);
                    return true;
                case MSG_SERVICE_CONNECTED:
                    ServiceConnectedEvent event = (ServiceConnectedEvent) msg.obj;
                    handleServiceConnected(event.componentName, event.iBinder);
                    return true;
                case MSG_SERVICE_DISCONNECTED:
                    handleServiceDisconnected((ComponentName) msg.obj);
                    return true;
                case MSG_RETRY_LISTENER_QUEUE:
                    handleRetryListenerQueue((ComponentName) msg.obj);
                    return true;
            }
            return false;
        }

        private void handleQueueTask(Task task) {
            updateListenerMap();
            for (ListenerServieRecord record : mRecordMap.values()) {
                record.taskQueue.add(task);
                processListenerQueue(record);
            }
        }

        private void handleServiceConnected(ComponentName componentName, IBinder iBinder) {
            ListenerServieRecord record = mRecordMap.get(componentName);
            if (record != null) {
                record.service = IFileTransferService.Stub.asInterface(iBinder);
                record.retryCount = 0;
                processListenerQueue(record);
            }
        }

        private void handleServiceDisconnected(ComponentName componentName) {
            ListenerServieRecord record = mRecordMap.get(componentName);
            if (record != null) {
                ensureServiceUnbound(record);
            }
        }

        private void handleRetryListenerQueue(ComponentName componentName) {
            ListenerServieRecord record = mRecordMap.get(componentName);
            if (record != null) {
                processListenerQueue(record);
            }
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Connected to service " + componentName);
            }
            mHandler.obtainMessage(MSG_SERVICE_CONNECTED,
                    new ServiceConnectedEvent(componentName, iBinder)).sendToTarget();
        }

        /**
         * Check the current list of enabled listener packages and update the records map
         * accordingly.
         */
        private void updateListenerMap() {
            // Set<String> enabledPackages = new HashSet<String>();
            // enabledPackages.add("com.tencent.tws.devicemanager");
            // enabledPackages.add("com.tencent.tws.watchside");
            // mCachedEnabledPackages = enabledPackages;

            // step1: 找出
            // PackageManager里所有intent的filter为ACTION_BIND_SIDE_CHANNEL的service,添加到mRecordMap,为下一步binder这些对应的service做准备
            List<ResolveInfo> resolveInfos = mContext.getPackageManager().queryIntentServices(
                    new Intent().setAction(FILE_SERVICE_ACTION), PackageManager.GET_SERVICES);
            Log.v(TAG, "resolveInfos size is =========== " + resolveInfos.size());
            Set<ComponentName> enabledComponents = new HashSet<ComponentName>();
            for (ResolveInfo resolveInfo : resolveInfos) {
                // Log.v(TAG, "updateListenerMap : " + resolveInfo.serviceInfo.packageName);
                // if (!enabledPackages.contains(resolveInfo.serviceInfo.packageName))
                // {
                // continue;
                // }
                ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName,
                        resolveInfo.serviceInfo.name);
                if (resolveInfo.serviceInfo.permission != null) {
                    Log.w(TAG, "Permission present on component " + componentName
                            + ", not adding listener record.");
                    continue;
                }
                enabledComponents.add(componentName);
            }
            // Ensure all enabled components have a record in the listener map.
            for (ComponentName componentName : enabledComponents) {
                if (!mRecordMap.containsKey(componentName)) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Adding listener record for " + componentName);
                    }
                    mRecordMap.put(componentName, new ListenerServieRecord(componentName));
                }
            }
            // Remove listener records that are no longer for enabled components.
            Iterator<Map.Entry<ComponentName, ListenerServieRecord>> it = mRecordMap.entrySet()
                    .iterator();
            while (it.hasNext()) {
                Map.Entry<ComponentName, ListenerServieRecord> entry = it.next();
                if (!enabledComponents.contains(entry.getKey())) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Removing listener record for " + entry.getKey());
                    }
                    ensureServiceUnbound(entry.getValue());
                    it.remove();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Disconnected from service " + componentName);
            }
            mHandler.obtainMessage(MSG_SERVICE_DISCONNECTED, componentName).sendToTarget();
        }

        /**
         * Ensure we are already attempting to bind to a service, or start a new binding if not.
         * 
         * @return Whether the service bind attempt was successful.
         */
        private boolean ensureServiceBound(ListenerServieRecord record) {
            if (record.bound) {
                return true;
            }
            Intent intent = new Intent(FILE_SERVICE_ACTION).setComponent(record.componentName);
            record.bound = mContext.bindService(intent, this, FILE_SERVICE_BIND_FLAGS);
            if (record.bound) {
                record.retryCount = 0;
            } else {
                Log.w(TAG, "Unable to bind to listener " + record.componentName);
                mContext.unbindService(this);
            }
            return record.bound;
        }

        /**
         * Ensure we have unbound from a service.
         */
        private void ensureServiceUnbound(ListenerServieRecord record) {
            if (record.bound) {
                mContext.unbindService(this);
                record.bound = false;
            }
            record.service = null;
        }

        /**
         * Schedule a delayed retry to communicate with a listener service. After a maximum number
         * of attempts (with exponential back-off), start dropping pending tasks for this listener.
         */
        private void scheduleListenerRetry(ListenerServieRecord record) {
            if (mHandler.hasMessages(MSG_RETRY_LISTENER_QUEUE, record.componentName)) {
                return;
            }
            record.retryCount++;
            if (record.retryCount > TASK_CHANNEL_RETRY_MAX_COUNT) {
                Log.w(TAG, "Giving up on delivering " + record.taskQueue.size() + " tasks to "
                        + record.componentName + " after " + record.retryCount + " retries");
                record.taskQueue.clear();
                return;
            }
            int delayMs = TASK_CHANNEL_RETRY_BASE_INTERVAL_MS * (1 << (record.retryCount - 1));
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Scheduling retry for " + delayMs + " ms");
            }
            Message msg = mHandler.obtainMessage(MSG_RETRY_LISTENER_QUEUE, record.componentName);
            mHandler.sendMessageDelayed(msg, delayMs);
        }

        /**
         * Perform a processing step for a listener. First check the bind state, then attempt to
         * flush the task queue, and if an error is encountered, schedule a retry.
         */
        private void processListenerQueue(ListenerServieRecord record) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Processing component " + record.componentName + ", "
                        + record.taskQueue.size() + " queued tasks");
            }
            if (record.taskQueue.isEmpty()) {
                return;
            }
            if (!ensureServiceBound(record) || record.service == null) {
                // Ensure bind has started and that a service interface is ready to use.
                scheduleListenerRetry(record);
                return;
            }
            // Attempt to flush all items in the task queue.
            while (true) {
                Task task = record.taskQueue.peek();
                if (task == null) {
                    break;
                }
                try {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Sending task " + task);
                    }
                    task.send(record.service);
                    record.taskQueue.remove();
                } catch (DeadObjectException e) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Remote service has died: " + record.componentName);
                    }
                    break;
                } catch (RemoteException e) {
                    Log.w(TAG, "RemoteException communicating with " + record.componentName, e);
                    break;
                }
            }
            if (!record.taskQueue.isEmpty()) {
                // Some tasks were not sent, meaning an error was encountered, schedule a retry.
                scheduleListenerRetry(record);
            }
        }

        private static class ListenerServieRecord {
            public final ComponentName componentName;
            /** Whether the service is currently bound to. */
            public boolean bound = false;
            /** The service stub provided by onServiceConnected */
            public IFileTransferService service;
            /** Queue of pending tasks to send to this listener service */
            public LinkedList<Task> taskQueue = new LinkedList<Task>();
            /**
             * Number of retries attempted while connecting to this listener service
             */
            public int retryCount = 0;

            public ListenerServieRecord(ComponentName componentName) {
                this.componentName = componentName;
            }
        }

        private static class ServiceConnectedEvent {
            final ComponentName componentName;
            final IBinder iBinder;

            public ServiceConnectedEvent(ComponentName componentName, final IBinder iBinder) {
                this.componentName = componentName;
                this.iBinder = iBinder;
            }
        }
    }

    public interface FileTransferListener {
        public abstract void onTransferError(long requestId, String fileName, int errorCode);

        public abstract void onTransferComplete(long requestId, String filePath);

        public abstract void onTransferCancel(long requestId, int reason);

        public abstract void onTransferProgress(long requestId, String fileName, long progress);
    }

}
