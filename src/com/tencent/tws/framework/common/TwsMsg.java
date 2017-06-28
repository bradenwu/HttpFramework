package com.tencent.tws.framework.common;

import com.qq.taf.jce.JceInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TwsMsg {
	private long m_nMsgId;
	private int m_nCmd;
	private byte[] m_byteMsg;
	private int m_nStartPosOfContent;

	private static final int MSGID_LENGTH = Long.SIZE / 8;
	private static final int CMD_LENGTH = Integer.SIZE / 8;
	
	public static final String UTF8 = "UTF8";

	public TwsMsg(byte[] oBytesOfMsg) {
		m_byteMsg = oBytesOfMsg;
	}

	public void parse() throws IOException {
		ByteArrayInputStream oByteArrIn = new ByteArrayInputStream(m_byteMsg);
		DataInputStream oDataIn = new DataInputStream(oByteArrIn);

		m_nMsgId = oDataIn.readLong();
		m_nCmd = oDataIn.readInt();

		m_nStartPosOfContent = MSGID_LENGTH + CMD_LENGTH;
	}

	public long msgId() {
		return m_nMsgId;
	}

	public int cmd() {
		return m_nCmd;
	}
	
	/**
	 * default encoding: GBK
	 * @return
	 */
    public JceInputStream getInputStream() {
        final JceInputStream inputStream = new JceInputStream(m_byteMsg, m_nStartPosOfContent);
        return inputStream;
    }
    
    public JceInputStream getInputStream(String serverEncoding) {
        final JceInputStream inputStream = new JceInputStream(m_byteMsg, m_nStartPosOfContent);
        inputStream.setServerEncoding(serverEncoding);
        return inputStream;
    }
    
    public JceInputStream getInputStreamUTF8() {
        return getInputStream(UTF8);
    }

	public void setCmd(int cmd) {
		this.m_nCmd = cmd;
	}

	public int startPosOfContent() {
		return m_nStartPosOfContent;
	}

	public byte[] msgByte() {
		return m_byteMsg;
	}
}
