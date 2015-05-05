package com.homni.multiroom.model;

/**
 * 解码model
 * 
 * @author Dawin
 *
 */
public class Decode
{

	// 解码后的数据
	private byte[] data;
	// 解码次数
	private int decodeCount;

	public Decode(byte[] data, int decodeCount)
	{
		this.data = data;
		this.decodeCount = decodeCount;
	}

	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}

	public int getDecodeCount()
	{
		return decodeCount;
	}

	public void setDecodeCount(int decodeCount)
	{
		this.decodeCount = decodeCount;
	}
}
