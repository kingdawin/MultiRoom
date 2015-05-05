package com.homni.multiroom.command;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.R.integer;
import android.util.Log;

import com.homni.multiroom.model.Recorder;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.GlobalValue;
import com.homni.multiroom.util.PhoneInfoHelp;




/**
 * 录音，播放设备控制协议
 * @author Dawin
 *
 */
public class ControlCommand extends SocketHelper
{
	private final String TAG="ControlCommand";
	
	/*===================================CMD========================================*/
	/**Server播放状态查询*/
	private final byte DEVICES_STATUS=(byte)0x41;
	/**设置设备状态*/
	private final byte SETTING_DEVICE_INFO=(byte)0x42;
	/**心跳包*/
	private final byte KEEP_ALIVE=(byte)0x99;	
	/*==============================================================================*/
	
	/*==================================Query Buffer State Command==================================*/
	/**协议头部*/
	//协议长度
	byte[] cmdLength=new byte[2];	
	/**校验和*/	
	/**协议版本*/
	/**命令*/
	/**命令类型*/
	byte[] cmdType=new byte[1];
	/**本机IP*/
	/**本机端口*/
	/**目标IP*/
	/**目标端口*/
	/**本机Id*/
	/**会话Id*/
	/**保留4字节*/
	byte[] other=new byte[4];	
	//buffer status response
	byte[] year = new byte[2];
	byte[] month = new byte[1];
	byte[] day = new byte[1];
	byte[] hour = new byte[4];
	byte[] minute = new byte[4];
	byte[] second = new byte[1];
	byte[] mSecond = new byte[2];
	byte[] leftCurrentSize = new byte[4];
	byte[] rightCurrentSize = new byte[4];
	byte[] otherRes = new byte[4];

	byte[] deviceType=new byte[1];
	/**
	 * 声道类型
	 * 
	 * @author Dawin
	 *
	 */
	public static enum ChannelType {	
		LEFT, RIGHT;
	}
	/**
	 * 服务器缓冲区当前数据大小
	 * 
	 * @param speaker
	 *            喇叭
	 * @param channelType
	 *            声道类型{ChannelType.LEFT,ChannelType.RIGHT}
	 * @return 缓冲区当前数据大小
	 */
	public int getCurrentBufferNum(/*Speaker speaker,*/ ChannelType channelType,int channelID)
	{
		int bufferSize;
		/* =================================发送================================= */
		// 拼接发送协议
		byte[] queryBufferStateCMD = new byte[39];
		System.arraycopy(head, 0, queryBufferStateCMD, 0, 6);
		System.arraycopy(cmdLength, 0, queryBufferStateCMD, 6, 2);
		System.arraycopy(checkSum, 0, queryBufferStateCMD, 8, 2);
		System.arraycopy(protocalVersion, 0, queryBufferStateCMD, 10, 1);
		System.arraycopy(new byte[] { 0x33 }, 0, queryBufferStateCMD, 11, 1);
		System.arraycopy(cmdType, 0, queryBufferStateCMD, 12, 1);
		System.arraycopy(localIP, 0, queryBufferStateCMD, 13, 4);
		System.arraycopy(localPort, 0, queryBufferStateCMD, 17, 2);
		System.arraycopy(destIP, 0, queryBufferStateCMD, 19, 4);
		System.arraycopy(destPort, 0, queryBufferStateCMD, 23, 2);
		System.arraycopy(localId, 0, queryBufferStateCMD, 25, 4);
		System.arraycopy(sessionId, 0, queryBufferStateCMD, 29, 4);
		System.arraycopy(other, 0, queryBufferStateCMD, 33, 4);
		System.arraycopy(end, 0, queryBufferStateCMD, 37, 2);
			
		try
		{
			// 将发送的协议数组封包
			//以一个包含数据的数组来创建DatagramPacket对象，创建该DatagramPacket对象时还指定了IP地址和端口--这就决定了该数据报的目的地
			
			switch (channelID)
			{
			case 1:
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip1), 9200);
				break;
			case 2:
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip2), 9200);
				break;
			case 3:
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip3), 9200);
				break;
			case 4:
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip4), 9200);
				break;
			case 5:
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip5), 9200);
				break;
			case 6:	
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip6), 9200);
				break;
			case 7:
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip7), 9200);
				break;
			case 8:
				mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(GlobalValue.ip8), 9200);
				break;
			}
			
		    //mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, /*speaker.getIp()*/, 9200);
			// 发送
			//使用DatagramSocket发送数据报时，DatagramSocket并不知道将该数据报发送到哪里，而是由DatagramPacket自身决定数据报的目的地。
			//就像码头并不知道每个集装箱的目的地，码头只是将这些集装箱发送出去，而集装箱本身包含了该集装箱的目的地。
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "getCurrentBufferNum sendData() send error:" + e.getMessage());
		}
		/* ============================接收============================== */
		byte[] res = new byte[56];
		//以一个空数组来创建DatagramPacket对象，该对象的作用是接收DatagramSocket中的数据
		mPacket = new DatagramPacket(res, 56);
		try
		{
			//receive()将一直等待（该方法会阻塞调用该方法的线程），直到收到一个数据报为止
			mSocket.receive(mPacket);
		} catch (InterruptedIOException e) { 
	 
			Log.e(TAG, "===============================Timed out!=======================================");           
        } catch (IOException e)
		{
        	 System.out.println("receive error:"+e.getMessage());  
		}  
		StringBuffer bufferSizeStringBuffer = new StringBuffer();
		//Log.e(TAG, "============================开始16进制字符串转整数============================");
		if (channelType == ChannelType.LEFT)
		{
			// 左声道:字节从低位到高位，从下标42-45
			for (int i = 45; i > 41; i--)
			{
				bufferSizeStringBuffer.append(String.format("%02X", res[i]));
			}
			// 16进制字符串转10进制整数
			bufferSize = Integer.parseInt(bufferSizeStringBuffer.toString(), 16);
			System.out.println(" 左声道bufferSize " + bufferSizeStringBuffer + "=" + bufferSize);			
			return bufferSize;
		} else
		{
			//右声道:字节从地位到高位，从下标46-49
			for (int i = 49; i > 45; i--)
			{
				bufferSizeStringBuffer.append(String.format("%02X", res[i]));
			}
			bufferSize = Integer.parseInt(bufferSizeStringBuffer.toString(), 16);
			System.out.println(" 右声道bufferSize " + bufferSizeStringBuffer + "=" + bufferSize);
			return Integer.parseInt(bufferSizeStringBuffer.toString(), 16);
		}
	}
	
	/**
	 * 服务器缓冲区当前数据大小
	 * 
	 * @param ip
	 * @param port
	 * @return 缓冲区当前数据大小
	 */
	public int getCurrentBufferNum(String ip,int port)
	{
		int bufferSize;
		/* =================================发送================================= */
		// 拼接发送协议
		byte[] queryBufferStateCMD = new byte[39];
		System.arraycopy(head, 0, queryBufferStateCMD, 0, 6);
		System.arraycopy(cmdLength, 0, queryBufferStateCMD, 6, 2);
		System.arraycopy(checkSum, 0, queryBufferStateCMD, 8, 2);
		System.arraycopy(protocalVersion, 0, queryBufferStateCMD, 10, 1);
		System.arraycopy(new byte[] { 0x33 }, 0, queryBufferStateCMD, 11, 1);
		System.arraycopy(cmdType, 0, queryBufferStateCMD, 12, 1);
		System.arraycopy(localIP, 0, queryBufferStateCMD, 13, 4);
		System.arraycopy(localPort, 0, queryBufferStateCMD, 17, 2);
		System.arraycopy(destIP, 0, queryBufferStateCMD, 19, 4);
		System.arraycopy(destPort, 0, queryBufferStateCMD, 23, 2);
		System.arraycopy(localId, 0, queryBufferStateCMD, 25, 4);
		System.arraycopy(sessionId, 0, queryBufferStateCMD, 29, 4);
		System.arraycopy(other, 0, queryBufferStateCMD, 33, 4);
		System.arraycopy(end, 0, queryBufferStateCMD, 37, 2);
		try{
	    mPacket = new DatagramPacket(queryBufferStateCMD, queryBufferStateCMD.length, InetAddress.getByName(ip), 9200);
	    // 发送
		//使用DatagramSocket发送数据报时，DatagramSocket并不知道将该数据报发送到哪里，而是由DatagramPacket自身决定数据报的目的地。
		//就像码头并不知道每个集装箱的目的地，码头只是将这些集装箱发送出去，而集装箱本身包含了该集装箱的目的地。
		mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "getCurrentBufferNum sendData() send error:" + e.getMessage());
		}
		/* ============================接收============================== */
		byte[] res = new byte[56];
		//以一个空数组来创建DatagramPacket对象，该对象的作用是接收DatagramSocket中的数据
		mPacket = new DatagramPacket(res, 56);
		try
		{
			//receive()将一直等待（该方法会阻塞调用该方法的线程），直到收到一个数据报为止
			mSocket.receive(mPacket);
		} catch (InterruptedIOException e) { 
	 
			Log.e(TAG, "===============================Timed out!=======================================");           
        } catch (IOException e)
		{
        	 System.out.println("receive error:"+e.getMessage());  
		}  
		StringBuffer bufferSizeStringBuffer = new StringBuffer();
		//Log.e(TAG, "============================开始16进制字符串转整数============================");
		if (port == 8900/*ChannelType.LEFT*/)
		{
			// 左声道:字节从低位到高位，从下标42-45
			for (int i = 45; i > 41; i--)
			{
				bufferSizeStringBuffer.append(String.format("%02X", res[i]));
			}
			// 16进制字符串转10进制整数
			bufferSize = Integer.parseInt(bufferSizeStringBuffer.toString(), 16);
			System.out.println(" 左声道bufferSize " + bufferSizeStringBuffer + "=" + bufferSize);			
			return bufferSize;
		} else
		{
			//右声道:字节从地位到高位，从下标46-49
			for (int i = 49; i > 45; i--)
			{
				bufferSizeStringBuffer.append(String.format("%02X", res[i]));
			}
			bufferSize = Integer.parseInt(bufferSizeStringBuffer.toString(), 16);
			System.out.println(" 右声道bufferSize " + bufferSizeStringBuffer + "=" + bufferSize);
			return Integer.parseInt(bufferSizeStringBuffer.toString(), 16);
		}
	}
	
    private final int CMD_INDEX=11;
	
	/** 
	 * 
	 * 查找服务器IP
	 *  
	 */
	public String getServerIP(){
		Log.d(TAG, "send 0xAA cmd"); 
		//服务器IP
		StringBuffer serverIP=new StringBuffer();
		/*===================发送=====================*/
		byte []reqByte=new byte[49];
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { (byte)0xAA }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4);			
		System.arraycopy(year, 0, reqByte, 33, 2);
		System.arraycopy(month, 0, reqByte, 35, 1);
		System.arraycopy(day, 0, reqByte, 36, 1);		
		System.arraycopy(hour, 0, reqByte, 37, 1);
		System.arraycopy(minute, 0, reqByte, 38, 1);
		System.arraycopy(second, 0, reqByte, 39, 1);
		System.arraycopy(mSecond, 0, reqByte, 40, 2);		
		System.arraycopy(deviceType, 0, reqByte, 42, 1);
		System.arraycopy(other, 0, reqByte, 43, 4);
		System.arraycopy(end, 0, reqByte, 47, 2);			
		try
		{
			mPacket=new DatagramPacket(reqByte, 49, InetAddress.getByName("255.255.255.255"),9200);
			mSocket.send(mPacket);
		}		
		catch (InterruptedIOException e) { 
			// 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求       
			Log.e(TAG, "===============================getServerIP Timed out!======================================="); 
			//return;
        }catch (Exception e) {
			Log.e(TAG,"getServerIP() receive error:"+e.getMessage());  
		}  
		
		/*===================接收=====================*/
		byte[] res = new byte[285];
		mPacket=new DatagramPacket(res, 285);
		try
		{
			mSocket.receive(mPacket);
		} catch (IOException e)
		{
			Log.e(TAG, "查找服务器IP :"+e.getMessage());//获取设备协议 接收出错
		}	
		System.out.println("res[CMD_INDEX]="+res[CMD_INDEX]);
		//查找服务器协议号码 0xAA=-86
		if (res[CMD_INDEX]==-86)
		{
			System.out.println("=============查找服务器IP=============");
			/*for (int i = 0; i < 285; i++)
			{
				System.out.println("[" + i + "]=" + String.format("%02X ", res[i]));
			}*/
			//ip地址在43-46字节
			//字节转成16进制字符串，再转10进制
			serverIP.append(Integer.parseInt(String.format("%02X", res[43]),16)).append(".").append(Integer.parseInt(String.format("%02X", res[44]),16)).append(".").append(Integer.parseInt(String.format("%02X", res[45]),16)).append(".").append(Integer.parseInt(String.format("%02X", res[46]),16));				
		} else
		{
			Log.e(TAG, "查询服务器IP失败");
		}	
		System.out.println("服务器IP:"+serverIP);
		return serverIP.toString();
	}
	
	/**
	 * 在线
	 */
	private final int ON=1;
	/**
	 * 离线
	 */
	private final int OFF=0;
	
	public static List<Recorder> recorders;
	
	/**
	 * 获取设备列表(Player,Recorder)
	 */
	public List<Speaker> getDevices(String ip){
		Log.d(TAG, "send 0x40 getDevices cmd"); 
		List<Speaker> speakers=new ArrayList<Speaker>();
	    recorders=new ArrayList<Recorder>();
		// 发送
		byte []reqByte=new byte[35/*47*/];
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { (byte)0x40 }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4);	//33
		
		/*System.arraycopy(other, 0, reqByte, 33, 4);
		System.arraycopy(other, 0, reqByte, 37, 4);
		System.arraycopy(other, 0, reqByte, 41, 4);	*/	
		System.arraycopy(end, 0, reqByte, 33/*45*/, 2);			
		try
		{			
			mPacket = new DatagramPacket(reqByte, 35, InetAddress.getByName(ip), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, " 获取设备列表协议 error:" + e.getMessage());
		}
		// 返回
		byte[] res=new byte[215];
		
		mPacket = new DatagramPacket(res, 215);
		try
		{
			mSocket.receive(mPacket);
		} catch (IOException e)
		{
			Log.e(TAG, "获取设备协议 接收出错 :" + e.getMessage());
		}
		System.out.println("=====================获取设备列表cmd 输出=====================");
		//设备个数res[33]
    	int countSpeaker=0;
		//获取'P'(50):播放设备ip,状态
		//index=34-37...  n个设备
		for(int i=34;i<=34+(res[33]-1)*9;i+=9){
			System.out.println("[" + i + "]=" + String.format("%02X ", res[i]));
			//======================播放设备'P'=0x50============================
			if(String.format("%02X", res[i]).equals("50")){
			Speaker speaker=new Speaker();
			countSpeaker++;
			    //ip
				speaker.setIp(
				Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
				Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
				Integer.parseInt(String.format("%02X", res[i+3]),16)+"."+
				Integer.parseInt(String.format("%02X", res[i+4]),16)				
				);
				
				//状态
				System.out.println("播放设备"+
						Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+3]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+4]),16)				
						+" 设备状态="+res[i+5]);
				speaker.setStatus(res[i+5]);	
				speaker.setId(countSpeaker);			
				speakers.add(speaker);
			}else
			//============================录音设备'R'=0x52	============================		
			if(String.format("%02X", res[i]).equals("52")){				
				Recorder recorder=new Recorder();				
				    //ip
			    	recorder.setIp(
					Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+3]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+4]),16)				
					);
					
					//状态
					System.out.println("录音设备"+
							Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
							Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
							Integer.parseInt(String.format("%02X", res[i+3]),16)+"."+
							Integer.parseInt(String.format("%02X", res[i+4]),16)				
							+" 设备状态="+res[i+5]);
					recorder.setStatus(res[i+5]);						
					recorders.add(recorder);					
			}else 	
			//================================设备A==============================================
				if(String.format("%02X", res[i]).equals("10")){
					//状态
					System.out.println("A设备"+
							Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
							Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
							Integer.parseInt(String.format("%02X", res[i+3]),16)+"."+
							Integer.parseInt(String.format("%02X", res[i+4]),16)				
							+" 设备状态="+res[i+5]);
			}else if(String.format("%02X", res[i]).equals("93")){
				//状态
				System.out.println("M设备"+
						Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+3]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+4]),16)				
						+" 设备状态="+res[i+5]);
			}			
		}
		Log.d(TAG, "recorder size="+recorders.size());
		return speakers;
	}
	
	
	/**true:连接正常，false:连接断开*/
	public static boolean isAlive;
	private DatagramPacket resPacket;

	public void androidToServerKeepAlive(String ipStr)
	{
		Log.w(TAG, "Android--->Server cmd=0x99");
		byte[] reqByte = new byte[44];
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { KEEP_ALIVE }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4); // 33

		System.arraycopy(year, 0, reqByte, 33, 2);
		System.arraycopy(month, 0, reqByte, 35, 1);
		System.arraycopy(day, 0, reqByte, 36, 1);
		System.arraycopy(hour, 0, reqByte, 37, 1);
		System.arraycopy(minute, 0, reqByte, 38, 1);
		System.arraycopy(second, 0, reqByte, 39, 1);
		System.arraycopy(mSecond, 0, reqByte, 40, 2);

		System.arraycopy(end, 0, reqByte, 42, 2);
		try
		{
			mPacket = new DatagramPacket(reqByte, reqByte.length, InetAddress.getByName(ipStr), 9200);
		} catch (UnknownHostException e1)
		{
			Log.e(TAG, "keep alive cmd error:" + e1.getMessage());
		}
		try
		{
			mSocket.send(mPacket);
		} catch (IOException e1)
		{
			Log.e(TAG, "keep alive cmd send:" + e1.getMessage());
		}
		// 返回
		byte[] res = new byte[50];

		resPacket = new DatagramPacket(res, res.length);
		try
		{
			mSocket.receive(resPacket);
			if (String.format("%02X", resPacket.getData()[11]).equals("99"))
			{
				System.out.println("alive");
			} else
			{
				System.out.println("not alive");
			}
		} catch (IOException e)
		{
			Log.e(TAG, "keep alive cmd receive:" + e.getMessage());
		}
	}
	// 心跳包,连续5秒没有收到回应，就认为已断开
	public /*boolean*/void isAlive(String ipStr)
	{
		Log.w(TAG, "Android--->Server cmd=0x99");
		int count = 0;
		byte[] reqByte = new byte[44];
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { KEEP_ALIVE }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4); // 33

		System.arraycopy(year, 0, reqByte, 33, 2);
		System.arraycopy(month, 0, reqByte, 35, 1);
		System.arraycopy(day, 0, reqByte, 36, 1);
		System.arraycopy(hour, 0, reqByte, 37, 1);
		System.arraycopy(minute, 0, reqByte, 38, 1);
		System.arraycopy(second, 0, reqByte, 39, 1);
		System.arraycopy(mSecond, 0, reqByte, 40, 2);

		System.arraycopy(end, 0, reqByte, 42, 2);
		try
		{
			mPacket = new DatagramPacket(reqByte, reqByte.length, InetAddress.getByName(ipStr), 9200);
		} catch (UnknownHostException e1)
		{
			Log.e(TAG, "keep alive cmd error:" + e1.getMessage());
		}

		while (true && GlobalValue.isAppOpen)
		{
			try
			{
				mSocket.send(mPacket);
			} catch (IOException e1)
			{			
				Log.e(TAG, "keep alive cmd send:" + e1.getMessage());
			}
			// 返回
			byte[] res = new byte[50];

			resPacket = new DatagramPacket(res, res.length);
			if (count == 5)
			{
				System.out.println("not alive");
				isAlive = false;
				count=0;
			}
			try
			{
				mSocket.receive(resPacket);
				if (String.format("%02X", resPacket.getData()[11]).equals("99"))
				{
					System.out.println("alive");
					isAlive = true;
				} else
				{
					System.out.println("not alive");
					count++;
				}
			} catch (IOException e)
			{
				count++;
				Log.e(TAG, "keep alive cmd receive:" + e.getMessage());
			}
			Log.d(TAG, "sleep 1000ms");
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				Log.e(TAG, "keep alive cmd error:" + e.getMessage());
			}
		}
	}


	/**设置录音设备的播放喇叭
	 * @param playerId  id[0,1,2...] 设置的播放设备信息：ip,id,声源类型
	 * @param soundSrc 声源'R' ,'L'
	 */
	public void setDevicesStatus(int []playerId,char soundSrc,String deviceIp)
	{
		//TODO:不是空闲 return 	
		
		/*=========================0x41查询状态==========================*/
		byte[] res=getDevicesStatusBytes("192.168.1.99");			 
		if (deviceIp == null)
		{
			Log.e(TAG, "deviceIp==null");
			return;
		}
		//0x42设置状态
		//本机IP地址转4个byte/
		String literals[] = deviceIp.split("\\.", 4);
		byte ip[] = ipStr2Byte(deviceIp)/*new byte[4]*/, c = 0;
		/*for (String d : literals)
		{
			ip[c++] = (byte) Short.parseShort(d);		
		}
		
		System.out.println("转换后字节:" + Arrays.toString(ip));	*/
		
		for(int i=0;i<playerId.length;i++){
			//由playerId得设置信息的位置			
			int startIndex=38+(playerId[i]/2)*14;//[38,41]录音ip		
			//左喇叭
			if (playerId[i] == 0 || playerId[i] == 2 || playerId[i] == 4 || playerId[i] == 6)
			{
				/**设备左声道 录音ip位置*/	
				res[startIndex]=ip[0];
				res[startIndex+1]=ip[1];
				res[startIndex+2]=ip[2];
				res[startIndex+3]=ip[3];		
				/**设备左声道  声道来源位置*/
				res[startIndex+4]=(byte)soundSrc;
			}
			//右喇叭
			if (playerId[i] == 1 || playerId[i] == 3 || playerId[i] == 5 || playerId[i] == 7)
			{
				/**设备右声道 录音ip位置*/
				res[startIndex+5]=ip[0];
				res[startIndex+6]=ip[1];
				res[startIndex+7]=ip[2];
				res[startIndex+8]=ip[3];
				/**设备右声道  声道来源位置*/
				res[startIndex+9]=(byte)soundSrc;
			}		
		}	
		
		//SETTING_DEVICE_INFO	
		try
		{
			res[11]=0x42;
			// 发送0x42	
			mPacket = new DatagramPacket(res, res.length, InetAddress.getByName("192.168.1.99"), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "setDevicesStatus error:"+e.getMessage());
		}		
	}
	/**
	 * ip字符串转成4个字节
	 * @param ipStr ip字符串
	 * @return
	 */
	public byte[] ipStr2Byte(String ipStr){
		String literals[] = (ipStr).split("\\.", 4);
		byte ip[] = new byte[4], c = 0;
		for (String d : literals)
		{
			ip[c++] = (byte) Short.parseShort(d);		
		}
		return ip;	
	}
	/**
	 * 清除指定喇叭的设备信息
	 * 
	 * @param playerId  id[0,1,3...]
	 * @param soundSrc 声源'R' ,'L'
	 */
	public void clearDevicesStatus(int []playerId)
	{
		//TODO:不是空闲 return 	
		
		/*=========================0x41查询状态==========================*/
		byte[] res=getDevicesStatusBytes("192.168.1.99");			 
		if (GlobalValue.localIp == null)
		{
			Log.e(TAG, "GlobalValue.localIp==null");
			return;
		}
		//0x42设置状态
		//本机IP地址转4个byte/
		String literals[] = (GlobalValue.localIp).split("\\.", 4);
		byte ip[] = new byte[4], c = 0;
		for (String d : literals)
		{
			ip[c++] = (byte) Short.parseShort(d);		
		}
		System.out.println("转换后字节:" + Arrays.toString(ip));	
		
		for(int i=0;i<playerId.length;i++){
			//由playerId得设置信息的位置			
			int startIndex=38+(playerId[i]/2)*14;//[38,41]录音ip		
			//左喇叭
			if (playerId[i]%2==0 /*== 0 || playerId[i] == 2 || playerId[i] == 4 || playerId[i] == 6*/)
			{
				/**设备左声道 录音ip位置*/	
				res[startIndex]=0;
				res[startIndex+1]=0;
				res[startIndex+2]=0;
				res[startIndex+3]=0;		
				/**设备左声道  声道来源位置*/
				res[startIndex+4]=0;
			}else
			//右喇叭
			/*if (playerId[i] == 1 || playerId[i] == 3 || playerId[i] == 5 || playerId[i] == 7)*/
			{
				/**设备右声道 录音ip位置*/
				res[startIndex+5]=0;
				res[startIndex+6]=0;
				res[startIndex+7]=0;
				res[startIndex+8]=0;
				/**设备右声道  声道来源位置*/
				res[startIndex+9]=0;
			}		
		}	
		
		//SETTING_DEVICE_INFO	
		try
		{
			res[11]=0x42;
			// 发送0x42	
			mPacket = new DatagramPacket(res, res.length, InetAddress.getByName("192.168.1.99"), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "setDevicesStatus error:"+e.getMessage());
		}		
	}
	
	/**
	 * 获取设备列表状态
	 * 
	 * 空闲:无 声道来源IP地址 0.0.0.0
	 * 在用：有 声道来源IP地址
	 * 
	 * TODO:用0x42前先用0x41
	 */
	public List<Speaker> getDevicesStatus(String ip/*,List<Speaker> speakers*/){	
		Log.d(TAG, "获取设备列表状态 send 0x41 cmd");
		List<Speaker> speakers=new ArrayList<Speaker>();
		// 发送
		byte[] reqByte = new byte[35/* 47 */];
		System.arraycopy(getReqHead(0x41), 0, reqByte, 0, 33); ;
	   /*	
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { (byte) DEVICES_STATUS }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4);*/
		
		System.arraycopy(end, 0, reqByte, 33, 2);
		try
		{			
			mPacket = new DatagramPacket(reqByte, 35, InetAddress.getByName(ip), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "查询设备状态出错 error:" + e.getMessage());
		}
		
		// 返回
		byte[] res = new byte[200];

		mPacket = new DatagramPacket(res, 200);
		try
		{
			mSocket.receive(mPacket);
		} catch (IOException e)
		{
			Log.e(TAG, "接收·查询设备状态出错  出错 :" + e.getMessage());
		}
		System.out.println("=====================查询设备状态  设备总数["+res[33]+"]=====================");

	/*for (int i = 0; i < res.length; i++)
		{
			System.out.println(res[i]+" [" + i + "]=" + String.format("%02X ", res[i])+"十进制="+Integer.parseInt(String.format("%02X", res[i]),16));
		}*/
	 
		int count=0;
		for (int i = 34; i <= 34+(res[33]-1)*14/*&&res[i]!=0*/; i+=14)
		{
			/*===================测试===================*/						
			if(count==5/*res[33]*/){break;}
			count++;
			/*======================================*/			
			
		/*	IP地址	整型		4字节	播放器IP地址
			播放设备左声道的来源IP地址	整型		4字节	录音设备IP地址
			播放设备左声道的来源声道	字符		1字节	L':左声道, 'R':右声道
			播放设备右声道的来源IP地址	整型		4字节	录音设备IP地址
			播放设备右声道的来源声道	字符		1字节	L':左声道, 'R':右声道
 		*/
			Speaker speaker=new Speaker();			
			//设备（喇叭）ip
			speaker.setIp(Integer.parseInt(String.format("%02X", res[i]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
						Integer.parseInt(String.format("%02X", res[i+3]),16)				
						);
			
			Log.w(TAG, "设备IP  "+Integer.parseInt(String.format("%02X", res[i]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+3]),16)				
					);			
		
			//左声道来源IP  38
			speaker.getLeftChannel().setFromIP(Integer.parseInt(String.format("%02X", res[i+4]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+5]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+6]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+7]),16));
			System.out.println("左声道来源IP  "+Integer.parseInt(String.format("%02X", res[i+4]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+5]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+6]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+7]),16));
			
			//左声道来源声道类型 42
			speaker.getLeftChannel().setFromChanneal(res[i+8]);			
			System.out.println("左声道声道来源声道   "+(char)res[i+8]);
			//右声道的来源IP
			speaker.getRightChannel().setFromIP(Integer.parseInt(String.format("%02X", res[i+9]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+10]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+11]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+12]),16));
			System.out.println("右声道来源IP  "+Integer.parseInt(String.format("%02X", res[i+9]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+10]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+11]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+12]),16));
			
			//右声道的来源声道	
			speaker.getRightChannel().setFromChanneal(res[i+13]);
			System.out.println("右声道的来源声道   "+(char)res[i+13]);
			speakers.add(speaker);
		}
		return speakers;
	}

	/**
	 * 获取指令头
	 * @param cmdNo 指令号
	 * @return 指令头数组
	 */
	public byte[] getReqHead(int cmdNo)
	{
		byte[] req = new byte[33];
		System.arraycopy(head, 0, req, 0, 6);
		//System.arraycopy(cmdLength, 0, req, 6, 2);
		//System.arraycopy(checkSum, 0, req, 8, 2);
		//System.arraycopy(protocalVersion, 0, req, 10, 1);
		System.arraycopy(new byte[] { (byte) cmdNo }, 0, req, 11, 1);
		//System.arraycopy(cmdType, 0, req, 12, 1);
		//System.arraycopy(localIP, 0, req, 13, 4);
		//System.arraycopy(localPort, 0, req, 17, 2);
		//System.arraycopy(destIP, 0, req, 19, 4);
		//System.arraycopy(destPort, 0, req, 23, 2);
		//System.arraycopy(localId, 0, req, 25, 4);
		//System.arraycopy(sessionId, 0, req, 29, 4);
		return req;
	}
	/**
	 * 查询设备列表状态
	 * <p>
	 * 空闲:无 声道来源IP地址 0.0.0.0
	 * 在用：有 声道来源IP地址
	 * 
	 * 
	 * TODO:用0x42前先用0x41
	 */
	public byte[]  getDevicesStatusBytes(String ip){	
		Log.d(TAG, "设备列表的状态 send 0x41 cmd");
		// 发送
		byte[] reqByte = new byte[35/* 47 */];
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { (byte) DEVICES_STATUS }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4);
		
		System.arraycopy(end, 0, reqByte, 33, 2);
		try
		{			
			mPacket = new DatagramPacket(reqByte, 35, InetAddress.getByName(ip), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "查询设备状态出错 error:" + e.getMessage());
		}
		
		// 返回
		byte[] res = new byte[200];

		mPacket = new DatagramPacket(res, 200);
		try
		{
			mSocket.receive(mPacket);
		} catch (IOException e)
		{
			Log.e(TAG, "接收·查询设备状态出错  出错 :" + e.getMessage());
		}
		System.out.println("=====================查询设备状态  设备总数["+res[33]+"]=====================");

	/*    for (int i = 0; i < res.length; i++)
		{
			System.out.println(res[i]+" [" + i + "]=" + String.format("%02X ", res[i])+"十进制="+Integer.parseInt(String.format("%02X", res[i]),16));
		}*/
	 
		int count=0;
		for (int i = 34; i <= 34+(res[33]-1)*14/*&&res[i]!=0*/; i+=14)
		{
			/*===================测试===================*/						
			if(count==5/*res[33]*/){break;}
			count++;
			/*======================================*/				
			Log.w(TAG, "设备IP  "+Integer.parseInt(String.format("%02X", res[i]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+1]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+2]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+3]),16)				
					);			
		
		
			System.out.println("左声道来源IP  "+Integer.parseInt(String.format("%02X", res[i+4]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+5]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+6]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+7]),16));
			
			//左声道来源声道类型 42	
			System.out.println("左声道声道来源声道   "+(char)res[i+8]);
			//右声道的来源IP
			System.out.println("右声道来源IP  "+Integer.parseInt(String.format("%02X", res[i+9]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+10]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+11]),16)+"."+
					Integer.parseInt(String.format("%02X", res[i+12]),16));
			
			//右声道的来源声道	
			System.out.println("右声道的来源声道   "+(char)res[i+13]);
		}
		return res;
	}
	
	
	
	
	/**
	 * @param playIp
	 *            播放设备IP
	 * @param playChannel
	 *            播放设备声道
	 */
	public void swapChannel(String playIp, int playChannel)
	{
		//ip转换为4字节，"."为拆分符
		byte[]reqByte=new byte[69];
		
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { (byte)0x35 }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4);	//33	
		//TODO:...	
	}
	
	 DatagramPacket packet;	

	 DatagramSocket socket;
	
	byte[] uuid=new byte[36];
	/**
	 * 注册设备
	 * isLogin=0:login ,isLogin=1:logout
	 * @return
	 * 搜索服务器时，注册手机
	 */
	/**
	 * @param isLogin 0:login ,1:logout
	 * @param serverIp
	 * @return
	 */
	public boolean  isRegisterDeviceOK(byte isLogin,String serverIp){
		//注册是否成功
		boolean isSuccess;
		//本机IP
		localIP=ipStr2Byte(GlobalValue.localIp);
		//本机mac uuid
		String[] dst=PhoneInfoHelp.getMac().split("\\:", 6);
		//填最后6字节
		int index=30;	   
		for (String str : dst)
		{
			// 16进制字符串转成整数
			int integer =12/* Integer.valueOf(str, 16)*/;//132 122 114
			uuid[index] = (byte) integer;
			index++;
		}	
		
		//req
		byte[]reqByte=new byte[76];			
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { (byte)0x11 }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4);	//33
		
		System.arraycopy(uuid, 0, reqByte, 33, 36);
		System.arraycopy(new byte[]{isLogin}, 0, reqByte, 69, 1);
		System.arraycopy(other, 0, reqByte, 70, 4);
		System.arraycopy(end, 0, reqByte, 74, 2);
		
		try
		{			
			mPacket = new DatagramPacket(reqByte, reqByte.length, InetAddress.getByName(serverIp), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "注册设备 send error:" + e.getMessage());
		}		
		// 返回
		byte[] res = new byte[42];

		mPacket = new DatagramPacket(res, res.length);
		try
		{
			mSocket.receive(mPacket);

			for (int i = 0; i < res.length; i++)
			{
				System.out.println("byte=" + res[i] + " [" + i + "]=0x" + String.format("%02X ", res[i]) + "十进制=" + Integer.parseInt(String.format("%02X", res[i]), 16));
			}
		} catch (IOException e)
		{
			Log.e(TAG, "注册设备  receive 出错 :" + e.getMessage());
		}	
	
			
		//注册是否成功  1：成功，0：失败 
		//res[34]		
		if (res[34] == 1)
		{			
		
			Log.d(TAG, "注册成功");
			isSuccess=true;
			//服务器分配的角色（设备类型）
			//A控制手机65，M音乐播放手机77
		    //res[35]
			if (res[35] == 'A')
			{
				Log.d(TAG, "A控制手机");
			} 
			else if (res[35] == 'M')
			{
				Log.d(TAG, "M音乐播放手机 ");
			}
			
			//成功后，接收服务器心跳包
			try
			{
				//socket = new DatagramSocket();
				//设置阻塞时间 
				//socket.setSoTimeout(1500);
				packet=new DatagramPacket(new byte[100], 100,InetAddress.getByName("192.168.1.100")/* InetAddress.getByName(GlobalValue.localIp)*/, 9200);
		    } catch (Exception e)
			{			
				e.printStackTrace();
			}		
			
		} else
		{			
			Log.d(TAG, "注册失败 ");
			isSuccess=false;
		}		
		return isSuccess;
	}

	/**
	 * 控制AllsIn 录音设备
	 * 
	 * 播放管理
	 * @param opCode 0:暂停, 1:恢复, 2:停止
	 * @param playIP 播放设备IP
	 * @param channelType L:Left Channel, R:Right Channel, A:ALL Channel
	 */
	public void playManage(byte opCode, String playIP, char channelType,String serverIp)
	{
		Log.w(TAG, "send 0x36 cmd");
		//===========================发送========================
		// TODO ipStr to ip byte
		byte ip[] = ipStr2Byte(playIP);
		// req
		byte[] reqByte = new byte[41];
		System.arraycopy(head, 0, reqByte, 0, 6);
		System.arraycopy(cmdLength, 0, reqByte, 6, 2);
		System.arraycopy(checkSum, 0, reqByte, 8, 2);
		System.arraycopy(protocalVersion, 0, reqByte, 10, 1);
		System.arraycopy(new byte[] { (byte) 0x36 }, 0, reqByte, 11, 1);
		System.arraycopy(cmdType, 0, reqByte, 12, 1);
		System.arraycopy(localIP, 0, reqByte, 13, 4);
		System.arraycopy(localPort, 0, reqByte, 17, 2);
		System.arraycopy(destIP, 0, reqByte, 19, 4);
		System.arraycopy(destPort, 0, reqByte, 23, 2);
		System.arraycopy(localId, 0, reqByte, 25, 4);
		System.arraycopy(sessionId, 0, reqByte, 29, 4); // 33
		// 播放设备IP
		System.arraycopy(ip, 0, reqByte, 33, 4);
		// 播放设备声道
		System.arraycopy(new byte[] { (byte) channelType }, 0, reqByte, 37, 1);
		// 指令
		System.arraycopy(new byte[] { opCode }, 0, reqByte, 38, 1);
		System.arraycopy(end, 0, reqByte, 39, 2);
		try
		{			
			mPacket = new DatagramPacket(reqByte, reqByte.length, InetAddress.getByName(serverIp), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "播放控制协议 send error:" + e.getMessage());
		}	
		//===================================返回===========================
		Log.w(TAG, "receive 0x36 cmd");
		byte[] res=new byte[60];
		try
		{			
			mPacket = new DatagramPacket(res, res.length);
			mSocket.receive(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "播放控制协议 send error:" + e.getMessage());
		}	
		printRes(res);		
	}
	/**
	 * 输出返回内容
	 */
	public void printRes(byte[]res){
		for (int i = 0; i < res.length; i++)
		{
			System.out.println("byte=" + res[i] + " [" + i + "]=0x" + String.format("%02X ", res[i]) + "十进制=" + Integer.parseInt(String.format("%02X", res[i]), 16));
		}
	}

	/**
	 * 切换录音设备的喇叭
	 * @param srcNo 当前喇叭的ip
	 * @param dstIp 切换到的喇叭ip
	 */
	
	/**
	 * 切换录音设备的喇叭
	 * @param deviceIp 设备Ip
	 * @param srcNo 源喇叭序号
	 * @param dstNo 目标喇叭序号
	 * @param channelType 声道类型
	 */
	public void changeChanel(String deviceIp, int srcNo, int dstNo, char channelType)
	{
		// 0x41查询状态
		byte[] res = getDevicesStatusBytes("192.168.1.99");
		int startIndex = 38 + (srcNo / 2) * 14;
		/***********************step1  原喇叭信息清空***********************/
		if (srcNo % 2 == 0)
		{
			// 设备左声道 录音ip
			res[startIndex] = 0;
			res[startIndex + 1] = 0;
			res[startIndex + 2] = 0;
			res[startIndex + 3] = 0;
			// 设备左声道 声道来源
			res[startIndex + 4] = 0;
		} else
		{
			// 设备右声道 录音ip
			res[startIndex + 5] = 0;
			res[startIndex + 6] = 0;
			res[startIndex + 7] = 0;
			res[startIndex + 8] = 0;
			// 设备右声道 声道来源
			res[startIndex + 9] = 0;
		}
		/***********************step2  设置新喇叭信息***********************/
		byte ip[] = ipStr2Byte(deviceIp);
		int startIndexDst = 38 + (dstNo / 2) * 14;
		if (dstNo % 2 == 0)
		{
			/** 设备左声道 录音ip位置 */
			res[startIndexDst] = ip[0];
			res[startIndexDst + 1] = ip[1];
			res[startIndexDst + 2] = ip[2];
			res[startIndexDst + 3] = ip[3];
			/** 设备左声道 声道来源位置 */
			res[startIndexDst + 4] = (byte) channelType;
		} else
		{
			/** 设备右声道 录音ip位置 */
			res[startIndexDst + 5] = ip[0];
			res[startIndexDst + 6] = ip[1];
			res[startIndexDst + 7] = ip[2];
			res[startIndexDst + 8] = ip[3];
			/** 设备右声道 声道来源位置 */
			res[startIndexDst + 9] = (byte) channelType;
		}
		
		// SETTING_DEVICE_INFO
		try
		{
			res[11] = 0x42;
			// 发送0x42
			mPacket = new DatagramPacket(res, res.length, InetAddress.getByName("192.168.1.99"), 9200);
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "changeChanel error:" + e.getMessage());
		}
	}
	
	public void 
}

/*
"%02X"说明：
X 表示以十六进制形式输出
02 表示不足两位，前面补0输出；出过两位，不影响
举例：
printf("%02X", 0x123);  //打印出：123
printf("%02X", 0x1); //打印出：01	
*/
