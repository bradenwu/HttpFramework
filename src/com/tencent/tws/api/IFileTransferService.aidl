package com.tencent.tws.api;

import com.tencent.tws.api.IFileTransferListener;
/*	IFileTransferService,  the service to provide the file transfer api to the third app
*	Note: the third app must call the FileTransferService once before use this service
*/
interface IFileTransferService
{
	void sendFile(String filePath, String dstPath, IFileTransferListener listener);
	void cancel(String filePath);
}