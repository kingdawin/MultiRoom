package com.homni.multiroom.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * 喇叭
 * 
 * @author Dawin
 *
 */
public class Speaker implements Serializable {	
	private static final String TAG="Speaker";
	/**用来标识每个喇叭 */	
	private int id;
	//在线
	private final int ON = 1;
	//离线
	private final int OFF = 0;	
	// 左声道
	private Channel leftChannel;
	// 右声道
	private Channel rightChannel;
	//当前播放端口{左 8900,右 8901}
	//private int currentPort;
	private InetAddress ip;
	private String ipString;
	//状态:在线1，离线0
	private int status;
	//左声道状态：开，关
	private boolean leftStatus;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	public boolean isLeftStatus()
	{
		return leftStatus;
	}

	public void setLeftStatus(boolean leftStatus)
	{
		this.leftStatus = leftStatus;
	}

	public boolean isRightStatus()
	{
		return rightStatus;
	}

	public void setRightStatus(boolean rightStatus)
	{
		this.rightStatus = rightStatus;
	}

	private boolean rightStatus;
	
	public Speaker() {
		leftChannel = new Channel();
		rightChannel = new Channel();
		leftChannel.setPort(8900);
		rightChannel.setPort(8901);
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public Channel getLeftChannel() {
		return leftChannel;
	}

	public Channel getRightChannel() {
		return rightChannel;
	}

	public InetAddress getIp() {

		return ip;
	}


	public String getIpStr() {

		return ipString;
	}
	
	public void setIp(String ip) {
		ipString=ip;
		try {
			this.ip = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			Log.e(TAG,"setIp "+e.getMessage());	
		}
	}

}
