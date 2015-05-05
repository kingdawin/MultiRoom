package com.homni.multiroom.model;

import java.io.Serializable;

/**
 * Â¼ÒôÉè±¸
 * @author Dawin
 *
 */
public class Recorder implements Serializable
{
	private final int ON = 1;
	private final int OFF = 0;
	private String ip;
	private int status;
	
	public String getIp()
	{
		return ip;
	}
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	public int getStatus()
	{
		return status;
	}
	public void setStatus(int status)
	{
		this.status = status;
	}
}
