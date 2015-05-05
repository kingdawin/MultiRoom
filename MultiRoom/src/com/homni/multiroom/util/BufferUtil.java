package com.homni.multiroom.util;
/**
 * 缓冲区
 * @author admin
 *
 */
public class BufferUtil
{

	/**
	 * 分离左右声道
	 * 
	 * @param audio
	 *            音频,PCM数据
	 * @return byte[LEFT][] byte[RIGHT][]
	 */
	public static byte[][] getLeftChannel(byte[] audio)
	{
		// 定义2行（audio.length / 2）列数组
		byte[][] leftRight = new byte[2][audio.length / 2];
		int count = 0;
		int leftIndex = 0;
		int rightIndex = 0;
		// 分离声道:前两字节左声道，后两字节右声道，依次重复。
		for (int i = 0; i < audio.length; i++)
		{
			count++;
			// 左声道
			if (count <= 2)
			{
				leftRight[0][leftIndex] = audio[i];
				leftIndex++;
			} else
			{
				// 右声道
				leftRight[1][rightIndex] = audio[i];
				rightIndex++;

				if (count == 4)
				{
					count = 0;
				}
			}
		}
		return leftRight;
	}
	/**
	 * 计算数据拆分成几分
	 * 
	 * @param totalLength
	 *            数据总长
	 * @param toLength
	 *            拆分的长度
	 * @return
	 */
	public static int countPacketSize(int totalLength, int toLength)
	{
		int capacity = 0;
		// 刚好length的倍数
		if (totalLength % toLength == 0)
		{
			capacity = totalLength / toLength;
		} else
		{
			// 有多
			if (totalLength > toLength)
			{
				capacity = totalLength / toLength + 1;
			} else
			{
				// 小于length
				capacity = 1;
			}
		}
		return capacity;
	}
}
