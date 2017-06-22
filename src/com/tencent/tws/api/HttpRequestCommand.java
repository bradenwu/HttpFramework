package com.tencent.tws.api;

public class HttpRequestCommand {
	
	public static final int POST = 0x00010001;
	public static final int POST_WITH_STRAMRETURN = 0x00010002;
	public static final int POST_WITH_GENERAL = 0x00010003;
	
	public static final int GET_TEXT = 0x00020001;
	public static final int GET_PNG_IMAGE = 0x00020002;
	public static final int GET_JPG_IMAGE = 0x00020003;
	
	public static final int GET_FILE = 0x00020100;
	public static final int GET_WITH_STREAMRETURN = 0x00020101;
	public static final int GET_WITH_GENERAL_TEXT = 0x00020102;
	public static final int GET_WITH_GENERAL_FILE = 0x00020103;
	
	public static final String HttpBroadcastDef = "com.tws.commonservice.httpservice";
	public static final String HttpServiceName = "com.tws.commonservice.httpservice";
	
	public static final int UNDEFINE_STATUS = 0; 
	public static final int NORMAL_STATUS = 1; 
	public static final int NETWORKFAIL_STATUS = -1;
	public static final int BLUETEETHFAIL_STATUS = -2;
	public static final int TIMEOUTSTATUS = -3;
	public static final int HTTPSERVICEFAIL = -4;
}
