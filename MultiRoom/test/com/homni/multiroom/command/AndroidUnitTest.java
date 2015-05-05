package com.homni.multiroom.command;

import com.homni.multiroom.util.GlobalValue;
import com.homni.multiroom.util.PhoneInfoHelp;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * 单元测试
 * @author admin
 *
 */
public class AndroidUnitTest extends AndroidTestCase
{
	private final String TAG="AndroidUnitTest";
	ControlCommand controlCommand = new ControlCommand();

	public void testGetServerIP()
	{
		System.out.println("R ascii=" + (int) 'R');// 82
		System.out.println("0.0.0.0.length()=" + "0.0.0.0".length());// 7

		byte b = (byte) 192;
		b = (byte) 168;
		b = (byte) 223;
		String.format("%02X", b);
		Byte bytes = -42;
		int result = bytes & 0xff;
		System.out.println("无符号数: \t" + result);
		System.out.println("2进制bit位: \t" + Integer.toBinaryString(result));
		// controlCommand.getServerIP();	
	}
	
	public void testGetDevicesStatus()
	{
		//0x41 设备状态，有来源ip=占用，0没有来源=空闲
		controlCommand.getDevicesStatus("192.168.1.99");
	}
	
	public void testKeepAlive()
	{
		controlCommand.isAlive("192.168.1.99");
	}
	public void testGetDevices()
	{
		//0x40
		controlCommand.getDevices("192.168.1.99");
	}
	
	public void testSetDevicesStatus()
	{
		//0x42
		controlCommand.setDevicesStatus(new int[]{0},'L',GlobalValue.localIp);
	}
	//注册手机
	public void testRegisterDevice(){
		//0x11
		if(controlCommand.isRegisterDeviceOK((byte)0,"192.168.1.99")){
		//TODO: debug=true,please delete when code ok
		GlobalValue.isAppOpen=true;
	    new Thread()
		{
			public void run()
			{
				System.out.println("==========================new Thread()============================");
				while (true)
				{
					try
					{
						Log.d(TAG, "receive...");
					//	mSocket.receive(packet);
					//	Log.e(TAG, "服务器返回的数据" + packet.getData()[11]);
						Thread.sleep(200);
					} catch (Exception e)
					{
						Log.e(TAG, "接收服务器心跳包 error：" + e.getMessage());
					}				
				}
			};
		}.start();
		}
	}
	public void testGetMac(){
		PhoneInfoHelp.getMac();
	}
	
	public void test16Str2Byte()
	{
		// 本机Ip转成4字节
		byte[] localId = ipStr2Byte("192.168.1.100"/* GlobalValue.localIp */);
		// uuid
		String[] dst = PhoneInfoHelp.getMac().split("\\:", 6);
		int index = 0;
		for (String str : dst)
		{
            //0x84=4*+8*16=132
			/* uuid[index]= (byte) */System.out.println(Integer.valueOf(str, 16));
			index++;
		}
	}

	/**
	 * ip字符串转成4个字节
	 * 
	 * @param ipStr
	 *            ip字符串
	 * @return
	 */
	public byte[] ipStr2Byte(String ipStr)
	{
		String literals[] = (ipStr).split("\\.", 4);
		byte ip[] = new byte[4], c = 0;
		for (String d : literals)
		{
			ip[c++] = (byte) Short.parseShort(d);
		}
		return ip;
	}

	public void testStopRecorder()
	{
		// 0:暂停, 1:恢复, 2:停止
		controlCommand.playManage((byte) 2, "192.168.1.201", 'L', "192.168.1.99");
	}
	
	public void testChangeChannel(){
		//改变录音设备播放的喇叭。
		//No:from 0 to 2n
		controlCommand.changeChanel("192.168.1.201",3, 0, 'L');
	}
}
