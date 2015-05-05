package com.homni.multiroom.command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.homni.multiroom.command.ControlCommand.ChannelType;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.AudioDecoder;
import com.homni.multiroom.util.BufferUtil;
import com.homni.multiroom.util.FileIOUtil;
import com.homni.multiroom.util.GlobalValue;


/**
 * 音频协议
 * @author Dawin
 * 
 *1启动查询缓冲区大小线程
 *2解码:边解边发
 */
public class AudioCommand extends SocketHelper  
{
	private static final String TAG = "AudioCommand";
	//初始状态
	public static final int IDLE=-1;
	public static final int PLAY=1;
	public static final int PAUSE=2;
	public static final int STOP=3;
	//播放状态
	private int state=IDLE;
	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public static boolean isSend;
	private AudioDecoder mAudioDecode;
	private ControlCommand controlCommand;
	private final int LEFT = 0;
	/** 缓冲区大小 */
	public static int bufferSize;
	/* ==================================Music Command================================== */
	/** 协议头部 */
	byte[] head = new byte[] { 'X', 'X', 'X', 'C', 'M', 'D' };
	/** 校验和 */
	byte[] checkSum = new byte[2];
	/** 协议版本 */
	byte[] protocalVersion = new byte[1];
	/** 命令 */
	byte[] cmd = new byte[1];
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
	/** 封包序号 */
	byte[] packetNo = new byte[4];
	/** 采样率 */
	byte[] sample = new byte[1];
	/** 采样精度 */
	byte[] bitDepth = new byte[1];
	/** 时间戳 */
	byte[] timeStamp = new byte[4];
	/** 通信数据 */
	byte[] data = new byte[1200];
	/** 结束符 */
	byte[] end = new byte[] { (byte) 0xff, (byte) 0xff };

	public AudioCommand()
	{ 
		//super();
		if (mSocketHelper == null)
		{
			mSocketHelper = new SocketHelper();
		}
		controlCommand = new ControlCommand();
		mAudioDecode = new AudioDecoder();
	}

	/**
	 * @param path
	 * @param speaker
	 * @param channelType
	 * @param channelID 喇叭ID,
	 */
	public void play(String path, Speaker speaker, ChannelType channelType,int channelID)
	{		
		isSend = true;
		state=PLAY;
		String fileFormat = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
		//播放MP3,WMA
		if ("MP3".equals(fileFormat) || "WMA".equals(fileFormat))
		{
			playMP3WMA(path, speaker, channelType,channelID);
		} 
		//播放WAV
		else if ("WAV".equals(fileFormat))
		{
			playWAV(path, speaker, channelType);
		}
		state=IDLE;
	}

	public void pause()
	{
		state=PAUSE;
	}
	public void stop()
	{
		isSend = false;
		state=STOP;
	}
	public void decode()
	{		
	}
	
	public void playWAV(String path, Speaker speaker, ChannelType channelType)
	{
		byte[] fileData = null;
		try
		{
			fileData = FileIOUtil.read(path);
		} catch (IOException e)
		{
			Log.e(TAG, "读取文件失败： " + e.getMessage());
			return;
		}
		int capacity = BufferUtil.countPacketSize(fileData.length, 1200);
		for (int i = 0; i < capacity && isSend; i++)
		{
			if (i * 1200 + 1200 >= fileData.length)
			{
				return;
			}
			data = Arrays.copyOfRange(fileData, i * 1200, i * 1200 + 1200);
			byte[] datas = toMusicCommand(data);
			// 封包成udp,并发送
			try
			{
				sendAudio(datas, 8900, speaker.getIp());
			} catch (Exception e1)
			{
				Log.e(TAG, "发送音频出错:" + e1.getMessage());
			}

			// 隔9ms发一个udp包
			try
			{
				Thread.sleep(9);
			} catch (InterruptedException e)
			{
				Log.e(TAG, "延迟出错 error:" + e.getMessage());
			}
		}

	}

	/**
	 * 时钟线程，监听缓冲区大小
	 * 
	 * @author Dawin
	 *
	 */
	class BufferThread implements Runnable
	{
	    //	Speaker speaker;
		ChannelType channelType;
		int channelID;
		public BufferThread(/*Speaker speaker,*/ ChannelType channelType,int channelID)
		{
			this.channelID=channelID;
		  //	this.speaker = speaker;
			this.channelType = channelType;
		}

		@Override
		public void run()
		{
			while (isSend)
			{
				bufferSize = controlCommand.getCurrentBufferNum(/*GlobalValue.speakerSelect1,*/channelType,channelID);
			
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					Log.e(TAG, "缓冲区时钟线程sleep失败" + e.getMessage());
				}
			}
		}
	}

	/**
	 * 播放MP3、WMA
	 * @param path
	 * @param speaker
	 * @param channelType 声道类型：左声道，右声道
	 */
	public void playMP3WMA(String path, Speaker speaker, ChannelType channelType,int channelID)
	{
		new Thread(new BufferThread(/*speaker, */channelType,channelID)).start();
		
		mAudioDecode.decode(path,speaker,channelType,channelID);
		//结束发送
		isSend = false;
		state=IDLE;	
	}

	/**
	 * 暂停 
	 * A pause mechanism that would block current thread when pause flag is set
	 */
	public synchronized void waitPlay()
	{
		while (state == PAUSE)
		{			
			try
			{	
				Log.e(TAG, " waitPlay() ...");	
				wait();
			} catch (InterruptedException e)
			{
				Log.e(TAG, " waitPlay() error:"+e.getMessage());		
			}
		}
	}
	public synchronized void wakeUp(){
		notifyAll();
	}
    
	/**
	 * 发送音频
	 * 
	 */
	private void sendAudio(byte[] audioData, int port, InetAddress ip)
	{
		try
		{
			// 将发送的byte数组封包
			mPacket = new DatagramPacket(audioData, audioData.length, ip, port);
			// 发送
			mSocket.send(mPacket);
		} catch (Exception e)
		{
			Log.e(TAG, "sendData() error:" + e.getMessage());
		}
	}

	/**
	 * 合并Byte[]数组，组装成音频协议
	 * 
	 * @param datas
	 *            最后发送的音频协议
	 * @param audioData
	 *            分包的音频数据
	 * @return
	 */
	public byte[] toMusicCommand(byte[] audioData)
	{
		byte[] datas = new byte[1252];
		// ===================组装成音频协议=================
		System.arraycopy(head, 0, datas, 0, 6);
		//cmdLength 2
		System.arraycopy(checkSum, 0, datas, 6, 2);
		System.arraycopy(protocalVersion, 0, datas, 8, 1);
		//TODO:协议ID 0x40
		System.arraycopy(cmd, 0, datas, 9, 1);
		System.arraycopy(localIP, 0, datas, 10, 4);
		System.arraycopy(localPort, 0, datas, 14, 2);
		System.arraycopy(destIP, 0, datas, 16, 4);
		System.arraycopy(destPort, 0, datas, 20, 2);
		System.arraycopy(localId, 0, datas, 22, 4);
		System.arraycopy(sessionId, 0, datas, 26, 4);
		System.arraycopy(packetNo, 0, datas, 30, 4);
		System.arraycopy(sample, 0, datas, 34, 1);
		System.arraycopy(bitDepth, 0, datas, 35, 1);
		System.arraycopy(timeStamp, 0, datas, 36, 4);
		System.arraycopy(audioData, 0, datas, 40, 1200);// 下标40开始
		System.arraycopy(end, 0, datas, 1240, 2);
		return datas;
	}
/*=================================================解码============================================================*/	
		private MediaCodec mMediaCodec;
		/** 用来读取音频文件 */
		private MediaExtractor extractor;
		private MediaFormat format;
		private String mime = null;
		private int sampleRate = 0, channels = 0, bitrate = 0;
		private long presentationTimeUs = 0, duration = 0;
		//private final long timeoutUs = 1000;   
		private final int lEFT=0;
		private final int RIGHT=1;
		/**解码缓冲区 */
		byte[] decodeBuffer=new byte[0];


		/**
		 * 解码音频并发送
		 * @param path  文件路径
		 * @param speaker 喇叭
		 * @param channelType 声道类型
		 * @param channelID 喇叭ID
		 */
		public void decode(String path,Speaker speaker,ChannelType channelType,int channelID)
		{		
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);		
			extractor = new MediaExtractor();
			// 根据路径获取源文件
			try
			{
				extractor.setDataSource(path);
			} catch (Exception e)
			{
				Log.e("MainActivity", "[MediaCodec run() 设置文件路径错误]" + e.getMessage());
			}
			try
			{
				// 音频文件信息
				format = extractor.getTrackFormat(0);
				mime = format.getString(MediaFormat.KEY_MIME);
				sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
				// 声道个数：单声道或双声道
				channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
				// if duration is 0, we are probably playing a live stream
				duration = format.getLong(MediaFormat.KEY_DURATION);
				//System.out.println("歌曲总时间秒:"+duration/1000000);
				bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
			} catch (Exception e)
			{
				Log.e(TAG, "音频文件信息读取出错：" + e.getMessage());
				// 不要退出，下面进行判断
			}
			Log.d(TAG, "Track info: mime:" + mime + " 采样率sampleRate:" + sampleRate + " channels:" + channels + " bitrate:" + bitrate + " duration:" + duration);
			// 检查是否为音频文件
			if (format == null || !mime.startsWith("audio/"))
			{
				Log.e(TAG, "不是音频文件！");
				return ;
			}

			// 实例化一个指定类型的解码器,提供数据输出
			// Instantiate an encoder supporting output data of the given mime type
			mMediaCodec = MediaCodec.createDecoderByType(mime);

			if (mMediaCodec == null)
			{
				Log.e(TAG, "创建解码器失败！");
				return ;
			}
			mMediaCodec.configure(format, null, null, 0);

			mMediaCodec.start();
			// 用来存放目标文件的数据
			ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
			// 解码后的数据
			ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
			// 设置声道类型:AudioFormat.CHANNEL_OUT_MONO单声道，AudioFormat.CHANNEL_OUT_STEREO双声道
			int channelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
			Log.i(TAG, "channelConfiguration=" + channelConfiguration);	
			extractor.selectTrack(0);
			//存储音频左右声道
			byte[][] leftRightChannel;
			//声道类型
			int channelTypeInt=channelType==ChannelType.LEFT?lEFT:RIGHT;
			//声道端口
			int port=channelType==ChannelType.LEFT?8900:8901;
			/* ==================================================================开始编码==================================================================*/
			boolean sawInputEOS = false;
			boolean sawOutputEOS = false;
			final long kTimeOutUs = 10/*1000*/;
			MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
			while (!sawOutputEOS && AudioCommand.isSend)
			{
				try
				{
					if (!sawInputEOS)
					{
						int inputBufIndex = mMediaCodec.dequeueInputBuffer(kTimeOutUs);
						if (inputBufIndex >= 0)
						{
							ByteBuffer dstBuf = inputBuffers[inputBufIndex];
							
							int sampleSize = extractor.readSampleData(dstBuf, 0);
							if (sampleSize < 0)
							{
								Log.d(TAG, "saw input EOS. Stopping playback");
								sawInputEOS = true;
								sampleSize = 0;
							} else
							{
								presentationTimeUs = extractor.getSampleTime();
							}

							mMediaCodec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

							if (!sawInputEOS){
								extractor.advance();
							}							

						} else
						{
							Log.e(TAG, "inputBufIndex " + inputBufIndex);
						}
					} // !sawInputEOS

					// decode to PCM and push it to the AudioTrack player
					int res = mMediaCodec.dequeueOutputBuffer(info, kTimeOutUs);

					if (res >= 0)
					{
						int outputBufIndex = res;
						ByteBuffer buf = outputBuffers[outputBufIndex];
						final byte[] chunk = new byte[info.size];
						System.out.println("info.size="+info.size);
						buf.get(chunk);
						buf.clear();
						if (chunk.length > 0)
						{								
							//获取音频左右声道
						    leftRightChannel =BufferUtil.getLeftChannel(chunk);
							
							// 边解边发送
							if (decodeBuffer.length ==0)
							{
								//发送数据报数目
								int num = leftRightChannel[channelTypeInt].length / 1200;
								decodeBuffer = new byte[leftRightChannel[channelTypeInt].length - num * 1200];
								//将多出的数据存到缓冲区，待下次解码拼接
								System.arraycopy(leftRightChannel[channelTypeInt], num * 1200, decodeBuffer, 0, decodeBuffer.length);

								/*=======================send=======================*/ 
								for (int i = 0; i < num && AudioCommand.isSend; i++)
								{
									Log.w(TAG, "bufferSize=" + AudioCommand.bufferSize);
									// 1M,服务器缓冲区大于1m,停止发送数据，等待缓冲区数据减少
									if (AudioCommand.bufferSize > 1048576)
									{
										try
										{
											// sleep 10s,只要保证服务器缓冲区有数据
											Thread.sleep(10000);
										} catch (InterruptedException e)
										{
											Log.w(TAG, "sleep error:" + e.getMessage());
										}
									}

									byte[] datas = byte2MusicCommand(Arrays.copyOfRange(leftRightChannel[channelTypeInt], i * 1200, i * 1200 + 1200));
									// 封包成udp,并发送
									try
									{								
										switch (channelID)
										{
										case 1:
											sendAudio(datas,  port,GlobalValue.ip1);
											break;
										case 2:
											sendAudio(datas,  port,GlobalValue.ip2);
											break;
										case 3:
											sendAudio(datas,  port,GlobalValue.ip3);
											break;
										case 4:
											sendAudio(datas,  port,GlobalValue.ip4);
											break;
										case 5:
											sendAudio(datas,  port,GlobalValue.ip5);
											break;
										case 6:	
											sendAudio(datas,  port,GlobalValue.ip6);

											break;
										case 7:
											sendAudio(datas,  port,GlobalValue.ip7);
											break;
										case 8:
											sendAudio(datas,  port,GlobalValue.ip8);
											break;
										}
																												
									} catch (Exception e1)
									{
										Log.e(TAG, "发送音频出错:" + e1.getMessage());
									}
									// 隔10ms发一个udp包
									try
									{
										Thread.sleep(7);
									} catch (InterruptedException e)
									{
										Log.e(TAG, "延迟出错 error:" + e.getMessage());
									}
								}

							} else 
							{
								byte[] add = new byte[decodeBuffer.length + leftRightChannel[channelTypeInt].length];
								System.arraycopy(decodeBuffer, 0, add, 0, decodeBuffer.length);
								System.arraycopy(leftRightChannel[channelTypeInt], 0, add, decodeBuffer.length, leftRightChannel[channelTypeInt].length);

								int num = add.length / 1200;
								decodeBuffer = new byte[add.length - num * 1200];
								System.arraycopy(add, num * 1200, decodeBuffer, 0, decodeBuffer.length);
								// send

								for (int i = 0; i < num && AudioCommand.isSend; i++)
								{
									Log.w(TAG, "bufferSize=" + AudioCommand.bufferSize);
									// 1M
									if (AudioCommand.bufferSize > 1048576)
									{
										try
										{
											// sleep 10s,只要保证服务器缓冲区有数据
											Thread.sleep(10000);
										} catch (InterruptedException e)
										{
											Log.w(TAG, "sleep error:" + e.getMessage());
										}
									}

									byte[] datas = byte2MusicCommand(Arrays.copyOfRange(add, i * 1200, i * 1200 + 1200));
									// 封包成udp,并发送
									try
									{
										switch (channelID)
										{
										case 1:
											sendAudio(datas,  port,GlobalValue.ip1/* GlobalValue.speakerSelect1.getIp()*/);
											break;
										case 2:
											sendAudio(datas,  port,GlobalValue.ip2/* GlobalValue.speakerSelect1.getIp()*/);
											break;
										case 3:
											sendAudio(datas,  port,GlobalValue.ip3/* GlobalValue.speakerSelect1.getIp()*/);
											break;
										case 4:
											sendAudio(datas,  port,GlobalValue.ip4/* GlobalValue.speakerSelect1.getIp()*/);
											break;
										case 5:
											sendAudio(datas,  port,GlobalValue.ip5/* GlobalValue.speakerSelect1.getIp()*/);
											break;
										case 6:	
											sendAudio(datas,  port,GlobalValue.ip6/* GlobalValue.speakerSelect1.getIp()*/);

											break;
										case 7:
											sendAudio(datas,  port,GlobalValue.ip7/* GlobalValue.speakerSelect1.getIp()*/);
											break;
										case 8:
											sendAudio(datas,  port,GlobalValue.ip8/* GlobalValue.speakerSelect1.getIp()*/);
											break;
										}														
									} catch (Exception e1)
									{
										Log.e(TAG, "发送音频出错:" + e1.getMessage());
									}
									// 隔10ms发一个udp包
									try
									{
										Thread.sleep(7);
									} catch (InterruptedException e)
									{
										Log.e(TAG, "延迟出错 error:" + e.getMessage());
									}
								}

							}
						}
						mMediaCodec.releaseOutputBuffer(outputBufIndex, false);
						if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
						{
							Log.d(TAG, "saw output EOS.");
							sawOutputEOS = true;
						}

					} else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
					{
						outputBuffers = mMediaCodec.getOutputBuffers();
						Log.w(TAG, "[AudioDecoder]output buffers have changed.");
					} else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
					{
						MediaFormat oformat = mMediaCodec.getOutputFormat();
						Log.w(TAG, "[AudioDecoder]output format has changed to " + oformat);
					} else
					{
						Log.w(TAG, "[AudioDecoder] dequeueOutputBuffer returned " + res);
					}

				} catch (RuntimeException e)
				{
					Log.e(TAG, "[decodeMP3] error:" + e.getMessage());
				}
			}    	 
	    	 
			// =================================================================================
			if (mMediaCodec != null)
			{
				mMediaCodec.stop();
				mMediaCodec.release();
				mMediaCodec = null;
			}
			if (extractor != null)
			{
				extractor.release();
				extractor = null;
			}
			// clear source and the other globals
			duration = 0;
			mime = null;
			sampleRate = 0;
			channels = 0;
			bitrate = 0;
			presentationTimeUs = 0;
			duration = 0;
		}
		
		/* ==================================Music Command================================== */

		   
		/**
		 * 发送音频
		 * 
		 */
		private void sendAudio(byte[] audioData, int port,String /*InetAddress*/ ip)
		{
			try
			{
				// 将发送的byte数组封包
				mPacket = new DatagramPacket(audioData, audioData.length, InetAddress.getByName(ip), port);
				// 发送
				mSocket.send(mPacket);
				Log.i(TAG, "成功发送");
			} catch (Exception e)
			{
				Log.e(TAG, "sendData() error:" + e.getMessage());
			}
		}

		/**
		 * 合并Byte[]数组，组装成音频协议
		 * 
		 * @param datas
		 *            最后发送的音频协议
		 * @param audioData
		 *            分包的音频数据
		 * @return
		 */
		public byte[] byte2MusicCommand(byte[] audioData)
		{
			byte[] datas = new byte[1242];
			// ===================组装成音频协议=================
			System.arraycopy(head, 0, datas, 0, 6);
			System.arraycopy(checkSum, 0, datas, 6, 2);
			System.arraycopy(protocalVersion, 0, datas, 8, 1);
			System.arraycopy(cmd, 0, datas, 9, 1);
			System.arraycopy(localIP, 0, datas, 10, 4);
			System.arraycopy(localPort, 0, datas, 14, 2);
			System.arraycopy(destIP, 0, datas, 16, 4);
			System.arraycopy(destPort, 0, datas, 20, 2);
			System.arraycopy(localId, 0, datas, 22, 4);
			System.arraycopy(sessionId, 0, datas, 26, 4);
			System.arraycopy(packetNo, 0, datas, 30, 4);
			System.arraycopy(sample, 0, datas, 34, 1);
			System.arraycopy(bitDepth, 0, datas, 35, 1);
			System.arraycopy(timeStamp, 0, datas, 36, 4);
			System.arraycopy(audioData, 0, datas, 40, 1200);// 下标40开始
			System.arraycopy(end, 0, datas, 1240, 2);
			return datas;
		}
		
}
