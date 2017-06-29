package com.tencent.tws.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pacewear.httpframework.HttpModule;
import com.pacewear.httpframework.HttpModule.IHttpInvokeCallback;
import com.tencent.tws.api.IHttpCallBack.Stub;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

public  class HttpManager implements Handler.Callback{
	private static final String TAG  = HttpManager.class.toString();
	
	private static HttpManager  sInstance;
	
    private HandlerThread           mWorkerThread;
    private Handler                 mThreadHandler;
    private boolean isHandlerStart = false;
    
    //internal use for managing package
    private static final int addPackage = 0x01;
    private static final int removePackage = 0x02;
    private static final int doReply = 0x03;
   
    
    private static final int addUserRequest = 0x04;
    private static final int doSendPackage = 0x05;

    private static final int rebinderService =0x70;
    private static final int stopHttpService =0x71;
    
    private static long seds = 0;
    private static String mPackageName;
    
    private BlockingQueue<HttpPackage> mSend = new LinkedBlockingQueue<HttpPackage>();//save request from application
    private HashMap<Long,HttpResponseListener> mMap = new HashMap<Long,HttpResponseListener>();//HashMap<mPackageName+sed,HttpResponse>
    private BlockingQueue<HttpPackage> mReply = new LinkedBlockingQueue<HttpPackage>();//save feedback from http-service
    
	private Context mContext;
	
	private boolean ifCallBackRegisterOk = false;
	
	 /** 
     * service的回调方法 
     */ 
	public class HttpmanagerCallBack extends IHttpCallBack.Stub{
		
		private String PackageName;
		
		public HttpmanagerCallBack(String Name){
			PackageName = Name; 
		}
		
		public String getName(){
			return PackageName;
		}
		
		@Override
		public void showResult(HttpPackage resultData) throws RemoteException {
			// TODO Auto-generated method stub
			mThreadHandler.obtainMessage(addPackage,resultData).sendToTarget();//send to result
		}
		
	}
    private Stub mCallback ;//aidl call back
    
/*    private IBinder.DeathRecipient mDeathRecipient = new DeathRecipient(){

		@Override
		public void binderDied() {
			// TODO Auto-generated method stub
			if(mIHttpService == null){
				return ;
			}
			
			mIHttpService.asBinder().unlinkToDeath(mDeathRecipient, 0);
			mIHttpService = null;
			mThreadHandler.sendEmptyMessageDelayed(rebinderService, 1000);//rebinder service
		}
    	
    };//add 12.30
*/
	
	private IHttpService mIHttpService;//save handle 
	
	private boolean isServiceRun = false;
	
	private int retryTimes = 0;
	
	private static int maxRetryTimes = 10;
    
    public static synchronized HttpManager getInstance(Context context)
    {
        if (context == null)
            throw new NullPointerException("context is null");

        if (context.getApplicationContext() == null)
            throw new NullPointerException("application context is null");

        if (sInstance == null)
        	sInstance = new HttpManager(context.getApplicationContext());
        
        return sInstance;
    }
    
    private HttpManager(Context context)
    {
        this.mContext = context;
        mWorkerThread = new HandlerThread("HttpManager");
        
        isHandlerStart = true;//start flag;
        mWorkerThread.start();//use thread to to work
        mThreadHandler = new Handler(mWorkerThread.getLooper(), this);//work thread
      
        mPackageName = context.getPackageName();//get packageName
        
        isServiceRun = bindConnectService();//binder httpService
        
/*        IntentFilter intentfilter = new IntentFilter(HttpRequestCommand.HttpBroadcastDef);//register broadcast
        //mContext.registerReceiver(mReceiver,intentfilter);
*/        
         //mCallback = new HttpmanagerCallBack(mPackageName);//register call back
        
		Log.d(TAG,"HttpManager(Context context)"+mPackageName);
    }
    
    private void ensureStartBasicService(){
    	if(isHandlerStart == false){
    	  isHandlerStart = true;//start flag;
    	  if(!isServiceRun)
    		  isServiceRun = bindConnectService();//binder httpService
    	  
    	  Log.d("HttpManager","ensureStartBasicService start all service ok");
    	}
    }
    
    private boolean ensureBindServiceIsOk(){//check service weather is ok
    	
    	if(isServiceRun == true){
    		retryTimes = 0;
    		Log.d(TAG,"stop retry");
    		return true;
    	}
    	else{
    		
    		if(retryTimes >= maxRetryTimes)
    			return false;//up-to-now should not rebind service anymore
    		
    		isServiceRun = bindConnectService();//rebind service
    		retryTimes++;
    		Log.d(TAG,"retry times add");
    		return isServiceRun;
    	}
    }
    
    
    private boolean bindConnectService()
    {
        boolean bRet;
        Intent oIntent = new Intent();
        oIntent.setAction(HttpRequestCommand.HttpServiceName); //oIntent.setAction(DevConnectService.SERVICE_NAME);
        oIntent.setPackage("com.tencent.tws.watchside");//modify to setPackage
        bRet = mContext.bindService(oIntent, m_oServiceConnection, Context.BIND_AUTO_CREATE);//bind http-service 
        return bRet;
    }
    
    private ServiceConnection  m_oServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
  /*      	try {
        		//mIHttpService.unregisterCallback(mPackageName,mCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        	
        	mIHttpService = null;
        	
        	Log.d("HttpManager","unbinder service is ok");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
        	mIHttpService = IHttpService.Stub.asInterface(service);
        	mCallback = new HttpmanagerCallBack("com.tencent.tws.api.watch");
/*        	try {
				service.linkToDeath(mDeathRecipient, 0);//service close rebinder
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}//add 12.30,
*/        	
        	try {
        	    Log.d("HttpManager","register service start");
				mIHttpService.registerCallback(mPackageName,mCallback);
				 Log.d("HttpManager","register service end");
				ifCallBackRegisterOk = true;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ifCallBackRegisterOk = false;
				Log.d("HttpManager","register service is bad");
			}
        	
        	Log.d("HttpManager","binder service is ok");
        }
        
    };
    
    private void unBindConnectService()
    {
        Intent oIntent = new Intent();
        oIntent.setAction(HttpRequestCommand.HttpServiceName);
        mContext.unbindService(m_oServiceConnection);
    }
    
    public synchronized void postHttpRequest(int type, String URL, HttpResponseListener mHttpResponseListener){
    	//do send message,more work - need to check URL,type ok?
    	ensureStartBasicService();//ensure basic service start-handler thread and httpservice binder
    	
    	Message msg = mThreadHandler.obtainMessage(addUserRequest,mHttpResponseListener);
		Bundle bundle = new Bundle();    
        bundle.putLong("sessionID",seds++);
        bundle.putInt("requestType",type);
        bundle.putString("URL", URL);
        msg.setData(bundle);
        msg.sendToTarget();//do send operation
    	
        Log.d("HttpManager","postHttpRequest is ok +"+seds+"+"+URL);
    }//this method need synchronized,may be called  by different thread 
    
    public synchronized void postHttpPostRequest(int type,HttpPostRequestParams mParams,HttpResponseListener mHttpResponseListener){
    	ensureStartBasicService();
    	
    	Message msg = mThreadHandler.obtainMessage(addUserRequest,mHttpResponseListener);
    	Bundle bundle = new Bundle();
        bundle.putLong("sessionID",seds++);
        bundle.putInt("requestType",type);
        bundle.putString("URL",mParams.RequestParamsToString());
        msg.setData(bundle);
        msg.sendToTarget();//do send operation
        Log.d("HttpManager","postHttpPostRequest is ok +"+seds+"+"+mParams.RequestParamsToString());
    }
    
    public synchronized void postGeneralHttpRequest(HttpRequestGeneralParams mParams,HttpResponseListener mHttpResponseListener){
    	ensureStartBasicService();
    	
    	Message msg = mThreadHandler.obtainMessage(addUserRequest,mHttpResponseListener);
    	Bundle bundle = new Bundle();
        bundle.putLong("sessionID",seds++);
        bundle.putInt("requestType",mParams.requestType);
        bundle.putString("URL",HttpRequestGeneralParams.HttpRequestGeneralParamsToString(mParams));
        msg.setData(bundle);
        msg.sendToTarget();//do send operation
        Log.d("HttpManager","postGeneralHttpRequest is ok +"+seds+"+"+HttpRequestGeneralParams.HttpRequestGeneralParamsToString(mParams));
    }
    
    private void doAddReply(HttpPackage mData){
    	mReply.add(mData);
    }
    
    private void dealReply(HttpPackage mData){
    	//a new thread do this work
    	if(mData == null)
    		return ;
    	
    	Log.d("HttpManager","HttpManager dealReply+"+mData.mSessionID+" data"+mData.mHttpData);
    	
    	if(mMap.containsKey(mData.getSessionId())){
    		
    	HttpResponseListener e = mMap.get(mData.getSessionId());
    	
        if(mData.getStatusCode() == HttpRequestCommand.NORMAL_STATUS){
          	if(mData.getType() == HttpRequestCommand.GET_WITH_STREAMRETURN || mData.getType() == HttpRequestCommand.POST_WITH_STRAMRETURN){
        		String mSuffix = mData.mHttpData;
        		if(mSuffix.endsWith(".txt")){//file
        			String pathName = "/sdcard/"+mData.mHttpData;
        			Log.d(TAG,"now path name is "+pathName);
        			File filename = new File(pathName);
        			StringBuilder str = new StringBuilder();
        			if(filename.exists()){
        				  
						try {
							BufferedReader in;
							in = new BufferedReader(new FileReader(filename));
	       		            
	       		            String tmp;
        		            while ((tmp = in.readLine()) != null) 
        		            {
        		                  str.append(tmp);
        		            }
        		            in.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}//end catch
						
						mData.mHttpData = str.toString();//now load string with Base64
						Log.d(TAG,"now load string with Base64 is ok");
        			}
        		}
        	}//end if
        	
    		e.onResponse(new HttpResponseResult(mData.getStatusCode(), mData.mPackageType, mData.mHttpData));
    		Log.d("HttpManager","onResponce ok");
    	}
    	else{
    		//under error status ,no data get
    		e.onError(mData.getStatusCode(),new HttpResponseResult(mData.getStatusCode(), mData.mPackageType, mData.mHttpData));
    		Log.d("HttpManager","onResponce bad");
    	}
    	
    	mMap.remove(mData.getSessionId());//remove, no longer get callback
    	
    	}//condition operation
    }
    
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		
		case addPackage:{//receive http-request callback
			HttpPackage e = (HttpPackage)msg.obj;
			doAddReply(e);
			mThreadHandler.sendEmptyMessage(doReply);//add package will touch an runnable to deal it
			return true;
		}
		
		case removePackage:{//no longer listen HttpResponseListener,retry times is enough
			for(HttpPackage mItem :mSend){
				mMap.get(mItem.mSessionID).onError(HttpRequestCommand.HTTPSERVICEFAIL, new HttpResponseResult(HttpRequestCommand.HTTPSERVICEFAIL,HttpResponseResult.DirectDatas,"NONE"));
				mMap.remove(mItem.mSessionID);
				Log.d("HttpManager","return NONE value by the condition Icallback is null");
			}
			mSend.clear();
			return true;
		}
		
		case doReply:{
			dealReply(mReply.poll());
			return true;
		}
		
		case addUserRequest:{
			HttpResponseListener e = (HttpResponseListener)msg.obj;
			long seed = msg.getData().getLong("sessionID");
			int type = msg.getData().getInt("requestType");
			String url = msg.getData().getString("URL");
			HttpPackage p = new HttpPackage(seed,mPackageName,type,HttpRequestCommand.UNDEFINE_STATUS,url);
			
			mMap.put(seed, e);
			
			mSend.add(p);
			
			mThreadHandler.sendEmptyMessage(doSendPackage);//add package will touch an runnable to send it
			Log.d("HttpManager","handleMessage addUserRequest is ok");
			return true;
		}
		
		case doSendPackage:{
			Log.d("HttpManager","enter handleMessage doSendPackage is ok");
			boolean isNetAvailable = HttpModule.isNetChannelAvailble(mContext);
			    if(!isNetAvailable && !ensureBindServiceIsOk()){//when retry times is more than maxRetryTimes,all message will be cleared
			    	if(retryTimes >= maxRetryTimes){
			    		mThreadHandler.sendEmptyMessage(removePackage);
			    	}
			    	else{
			    		mThreadHandler.sendEmptyMessageDelayed(rebinderService, 1000);
			    	}
			    		
			    	return true;//return value
			    }//end if
			    
			   if(!isNetAvailable && mIHttpService == null){
				   
			    	mThreadHandler.sendEmptyMessageDelayed(doSendPackage,1000);
			    	return true;
			    }//bindService  is Ok
			    
			 //  if(ifCallBackRegisterOk == false){
			//	   mThreadHandler.sendEmptyMessage(removePackage);
			//	   return true;
			//   }//register call back fail ,can not recovery from it.service is ok ,but can not unregister
			   
				HttpPackage mTemp = null;
				try {
					
					if(mSend.isEmpty()){
						return true;
					}//check if empty
					
					HttpPackage e = mSend.poll();
					mTemp = e;
					Log.d("HttpManager","11 + "+e.mPackageName);
					if(e != null){
						Log.d("HttpManager","before getrequest");
						if(isNetAvailable){
						    invokeHttp(e);
						} else {
						    mIHttpService.getRequest(e);//final operation-binder call
						}
						Log.d("HttpManager","handleMessage doSendPackage is ok");
					}
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//when remote service fail
					if(mMap.containsKey(mTemp.mSessionID)){
						mMap.get(mTemp.mSessionID).onError(HttpRequestCommand.HTTPSERVICEFAIL, new HttpResponseResult(HttpRequestCommand.HTTPSERVICEFAIL,HttpResponseResult.DirectDatas,"NONE"));
						mMap.remove(mTemp.mSessionID);
						Log.d("HttpManager","fail with aidl getRequest method,so value give NONE");
					}//when service is crash ,we do reply
				}

			return true;
		}
		
		case rebinderService:{//when service is unconnect
			Log.d("HttpManager","enter rebinder service");
		    if(!ensureBindServiceIsOk()){
		    	if(retryTimes >= maxRetryTimes){
		    		mThreadHandler.sendEmptyMessage(removePackage);
		    	}
		    	else{
		    		mThreadHandler.sendEmptyMessageDelayed(rebinderService, 1000);
		    	}
		    		
		    	return true;
		    }
			
			mThreadHandler.sendEmptyMessage(doSendPackage);
		}
		
		case stopHttpService:
			mSend.clear();
			mMap.clear();//clear all call back
			mReply.clear();
			mThreadHandler.removeCallbacksAndMessages(null);
			
			try {
			if(mIHttpService != null)
				mIHttpService.unregisterCallback(mPackageName,mCallback);
    		Log.d("HttpManager","unregister callback is ok");
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			
			if(isServiceRun)
				unBindConnectService();//unbinder service
			isServiceRun = false;//
			isHandlerStart = false;
			ifCallBackRegisterOk = false;
			return true;
		
		}//switch end
		return false;
	}

	public synchronized void stopHttpManagerService(){
		//this will recycle httpmanager's all resource,when close your application not activity ,you can use it
		if(!isHandlerStart){
			return ;
		}
		
/*		try {
			Log.d("HttpManager","stopHttpManagerService start unregister");
			mIHttpService.unregisterCallback(mPackageName,mCallback);
			Log.d("HttpManager","stopHttpManagerService end unregister");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		///if(isServiceRun){
		//	unBindConnectService();
		//}//unbound service
		
		mThreadHandler.sendEmptyMessage(stopHttpService);
		
		//mContext.unregisterReceiver(mReceiver);//no longer receive broadcast
		
/*		if(mThreadHandler != null){
			mThreadHandler.removeCallbacksAndMessages(null);
			mThreadHandler = null;
		}
		
		if(mWorkerThread != null){
			mWorkerThread.getLooper().quitSafely();//stop the thread
			mWorkerThread = null;
		}*/
		
		//isServiceRun = false;
		
		Log.d("HttpManager","stopHttpManagerService is ok and goto end");
		
	}

	private void invokeHttp(HttpPackage e) {
	    HttpModule.invokeHttp(mContext, e, new IHttpInvokeCallback() {
            
            @Override
            public void onCallback(HttpPackage resultData) {
                mThreadHandler.obtainMessage(addPackage,resultData).sendToTarget();//send to result
            }
        });
	}
}
