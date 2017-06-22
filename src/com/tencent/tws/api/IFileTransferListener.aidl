package com.tencent.tws.api;

/*	IFileTransferService,  the service to provide the file transfer api to the third app
*	Note: the third app must call the FileTransferService once before use this service
*/
interface IFileTransferListener
{
	 void onTransferError(long requestId, String fileName, int errorCode);

     void onTransferComplete(long requestId, String filePath);

     void onTransferCancel(long requestId, int reason);

     void onTransferProgress(long requestId, String fileName, long progress);
}