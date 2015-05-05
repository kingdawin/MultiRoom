package com.homni.multiroom.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.UUID;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
/**手机设备信息工具类*/
public class PhoneInfoHelp
{
	private final static String TAG="PhoneInfoHelp";

	/** 获取IP地址 */
	public static String getLocalIP(Context context)
	{
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled())
		{
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String localIP = intToIp(ipAddress);
		Log.e(TAG, "Phone local IP:" + localIP);
		return localIP;
	}
	
	private static String intToIp(int i)
	{
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}  
	
	public static String getMac()
	{
		String macSerial = null;
		String str = "";
		try
		{
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;)
			{
				str = input.readLine();
				if (str != null)
				{// 去空格
					macSerial = str.trim();
					break;
				}
			}
		} catch (IOException ex)
		{
			// 赋予默认值
			ex.printStackTrace();
		}
		// 84:7a:88:72:97:d1
		return macSerial;
	}	
	
	/*	public void getDeviceId(Context context)
	{
		// 想得到设备的唯一序号， TelephonyManager.getDeviceId() 就足够了。
		// 但很明显暴露了DeviceID会使一些用户不满，所以最好把这些id加密了。
		// 实际上加密后的序号仍然可以唯一的识别该设备，并且不会明显的暴露用户的特定设备，
		// 例如，使用 String.hashCode() ，结合UUID：
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();// htc one 99000146898635 meid
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		System.out.println("uniqueId=" + uniqueId);
	}*/
	
}
