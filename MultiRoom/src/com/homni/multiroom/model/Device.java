package com.homni.multiroom.model;

/**
 * …Ë±∏model
 * 
 * @author Dawin
 *
 */
public class Device
{
	private char channelType;
	private String ip;
	private int id;
	public char getChannelType()
	{
		return channelType;
	}
	public void setChannelType(char channelType)
	{
		this.channelType = channelType;
	}
	public String getIp()
	{
		return ip;
	}
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	
}
