package com.tencent.tws.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



	
	public class HttpPostRequestParams {
		
		public static final String TAG = "HttpPostRequestParams";
		public String URl;
		private HashMap<String,String> mHeader = new HashMap<String,String>();
		private HashMap<String,String> mBodyparams = new HashMap<String,String>();
		public String mBodyEntity = null;
		public String mBodyEntityStringEncoding = null;
		
		public HttpPostRequestParams(){
			
		}
		
		public void addUrl(String url){
			URl = url;
		}
		
		public void addHeader(String key, String value){
			mHeader.put(key, value);
		}
		
		public void addBodyParameter(String key, String value){
			mBodyparams.put(key, value);
		}
		
		public void addBodyEntity(String body){
			mBodyEntity = body;
		}
		
		public void addBodyEntity(String body, String bodyEncoding){
			mBodyEntity = body;
			mBodyEntityStringEncoding = bodyEncoding;
		}
		
		public HashMap<String,String> getHeader(){
			return mHeader;
		}
		
		public HashMap<String,String> getBodyParamete(){
			return mBodyparams;
		}
		
		public static HttpPostRequestParams StringToRequestParams(String mData){
			
			HttpPostRequestParams mResult = new HttpPostRequestParams();
			
			try {
				JSONObject resultJson = new JSONObject(mData);
				boolean mHasHead = resultJson.getBoolean("hasHead");
				boolean mHasBody = resultJson.getBoolean("hasBody");
				
				if(mHasHead){
				
				JSONObject jsonHeadArray = resultJson.getJSONObject("head");
				
				//now load head part
				 Iterator it = jsonHeadArray.keys();
				 while (it.hasNext()) { 
					 String key = (String) it.next();  
		             String value = jsonHeadArray.getString(key);
		             Log.d(TAG,"head part key-value is "+key+":"+value);
		             mResult.addHeader(key, value);
				 }//while end
				 
				}//head end
				 
				if(mHasBody){
				boolean mhasBodyEnty = resultJson.getBoolean("bodyEnty");
				JSONObject jsonBodyArray = resultJson.getJSONObject("body");
				
				if(!mhasBodyEnty){
				 //now load body part
				 Iterator it1 = jsonBodyArray.keys();
				 while (it1.hasNext()) { 
					 String key = (String) it1.next();  
		             String value = jsonBodyArray.getString(key);
		             Log.d(TAG,"body part key-value is "+key+":"+value);
		             mResult.addBodyParameter(key, value);
				 }//while end
				}//if
				else{
					mResult.mBodyEntity = jsonBodyArray.getString("body_string_enty");
					mResult.mBodyEntityStringEncoding = jsonBodyArray.optString("body_string_enty_encoding");
				}
				
				 
				}//body end
				
				 
				 mResult.URl = resultJson.getString("url");//get url
				 Log.d(TAG,"url part is "+mResult.URl);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			}
			
			return mResult;
		}
		
		public String RequestParamsToString(){
			 JSONObject jsonObject = new JSONObject();//main object
			 
			 //head start
			 int headCount = mHeader.size();
			 JSONObject jsonHeadArray = new JSONObject();
			 
			 if(headCount > 0){	
			 
		     for(Map.Entry<String, String> entry : mHeader.entrySet()){
		    	 try {
		    		 jsonHeadArray.put(entry.getKey(), entry.getValue());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			    }//catch
		     }//for
		     
		     try {
				jsonObject.put("hasHead", true);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//add has head
		     
			}//if
			 else{//headCount is 0
				 
			     try {
					  jsonObject.put("hasHead", false);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//add has head
			 }//else
			 
			 //head end
		     
		     //body judge start
		     JSONObject jsonBodyArray = new JSONObject();
		     boolean  mHasBody = (mBodyparams.size() > 0) || (mBodyEntity != null);
		     
		     if(mHasBody){
		    	 
		     if(mBodyEntity == null){//if not have body enty
		    	 for(Map.Entry<String, String> entry1 : mBodyparams.entrySet()){
		    		 
		    	 try {
		    		 jsonBodyArray.put(entry1.getKey(), entry1.getValue());
				 } catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
		    	 
		    	 }//for
		    	 
	    		 try {
					jsonObject.put("bodyEnty", false);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//set bodyenty true
	    		 
		     }//if
		     else{//if has body enty
		    	 try {
					jsonBodyArray.put("body_string_enty", mBodyEntity);
					jsonBodyArray.put("body_string_enty_encoding", mBodyEntityStringEncoding);
					jsonObject.put("bodyEnty", true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     }//else
		     
		    try{
				jsonObject.put("hasBody", true);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//add has body
		    
		    }//if
		    else{
			     try {
						jsonObject.put("hasBody", false);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//add has head
		    	
		    }
		    //body judge end 
		     
	         try {
	        	 if(headCount > 0)
	        		 jsonObject.put("head", jsonHeadArray);//head 
	        	 
	        	 if(mHasBody)
	        		 jsonObject.put("body", jsonBodyArray);//body 
	        	 
				jsonObject.put("url", this.URl);//url
				 Log.d(TAG,"url put finish" + this.URl);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         
		     
	         return jsonObject.toString();

			
		}
	}


