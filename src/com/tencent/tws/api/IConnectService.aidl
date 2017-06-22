package com.tencent.tws.api;

interface IConnectService
{
	int asyncStartConnectBtDev( String strAddress, String packageName );
	int asyncDisConnecteBtDev( String strAddress, String packageName );
	List<String> getConnectedBtDevs( String packageName ); 
	String getLastConnectedBtDevs( String packageName );
	boolean isDevConnected( String packageName );
}