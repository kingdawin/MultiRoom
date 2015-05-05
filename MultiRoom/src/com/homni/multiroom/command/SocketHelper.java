package com.homni.multiroom.command;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;

/**
 * Socket收发UDP
 * 
 * @author kingdawin
 *
 */
public class SocketHelper
{
	private final String TAG="SocketHelper";
	public  SocketHelper mSocketHelper;
	public  DatagramSocket mSocket;	
	public DatagramPacket mPacket;
	// 发送缓冲区
	public byte[] sendBuffer;
	// 接收缓冲区
	public byte[] receiveBuffer;
	/**设置超时为200ms秒 */ 
	private static final int TIMEOUT = 200/*1000*/;   
	
	/** 协议头部 */
	public byte[] head = new byte[] { 'X', 'X', 'X', 'C', 'M', 'D' };
	/** 校验和 */
	public byte[] checkSum = new byte[2];
	/** 协议版本 */
	byte[] protocalVersion = new byte[1];
	/** 本机IP */
	byte[] localIP = new byte[4];
	/** 本机端口 */
	byte[] localPort = new byte[2];
	/** 目标IP */
	byte[] destIP = new byte[4];
	/** 目标端口 */
	byte[] destPort = new byte[2];
	/** 本机Id */
	byte[] localId = new byte[4];
	/** 会话Id */
	byte[] sessionId = new byte[4];
	/** 命令 */
	public byte[] cmd = new byte[1];
	
	/** 结束符 */
	public byte[] end = new byte[] { (byte) 0xff, (byte) 0xff };

	public SocketHelper()
	{
		// 创建UDP datagram socket
		//创建一个DatagramSocket实例，并将该对象绑定到本机默认IP地址、本机所有可用端口中随机选择的某个端口
		try
		{
			mSocket = new DatagramSocket();
			//设置阻塞时间 
			mSocket.setSoTimeout(TIMEOUT);			
		} catch (SocketException e)
		{
			// create socket fail
			Log.e(TAG, "SocketException error:"+e.getMessage());
		}
	}
}
