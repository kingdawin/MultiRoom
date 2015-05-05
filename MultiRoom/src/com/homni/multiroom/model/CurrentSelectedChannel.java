package com.homni.multiroom.model;

import java.io.Serializable;

import android.R.integer;

/**
 * 被选中播放的声道
 * @author admin
 *
 */
public class CurrentSelectedChannel implements Serializable
{
	int channelID;
	public int getChannelID()
	{
		return channelID;
	}

	public void setChannelID(int channelID)
	{
		this.channelID = channelID;
	}

	final String LEFT="LEFT";
	final String RIGHT="RIGHT";
	
	// ip
	String deviceName;
	// L,R
	String channelName;

	public String getDeviceName()
	{
		return deviceName;
	}

	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}

	public String getChannelName()
	{
		return channelName;
	}

	public void setChannelName(String channel)
	{
		this.channelName = channel;
	}

}
