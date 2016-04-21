# MultiRoom
wifi play music

将手机本地歌曲解码，得到解码流后，通过tcp socket传输到wifi音箱播放

用Android SDK自带的MediaCodec解码

执行流程:
获取歌曲路径->根据路径获取歌曲格式信息->
根据歌曲格式创建解码器->解码器进行解码->
分割得到的解码流->将分割的码流喂给socket发送

如何解码:

public class DecodeAudioDemo
{
	private static final String TAG = "DecodeAudioDemo";

	/** 用来解码 */
	private MediaCodec mMediaCodec;
	/** 用来读取音频文件 */
	private MediaExtractor extractor;
	private MediaFormat format;
	private String mime = null;
	private int sampleRate = 0, channels = 0, bitrate = 0;
	private long presentationTimeUs = 0, duration = 0;
	/**
	 * decode music to stream and send to music box decode by MediaCodec
	 *
	 * url: "your music url"
	 */
	public void decode(String url)
	{

		// 截取文件后缀
		String fileFormat = url.substring(url.lastIndexOf(".") + 1)
				.toUpperCase();
		// 判断音乐格式
		if ("MP3".equals(fileFormat) || "WMA".equals(fileFormat)
				|| "AIFF".equals(fileFormat))
		{
			// android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			extractor = new MediaExtractor();
			// 根据路径获取源文件
			try
			{
				extractor.setDataSource(url);
			} catch (Exception e)
			{
				Log.e("MainActivity",
						"[MediaCodec run() 设置文件路径错误]" + e.getMessage());
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
				// System.out.println("歌曲总时间秒:"+duration/1000000);
				bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
			} catch (Exception e)
			{
				Log.e(TAG, "音频文件信息读取出错：" + e.getMessage());
				// 不要退出，下面进行判断
			}
			Log.d(TAG, "Track info: mime:" + mime + " 采样率sampleRate:"
					+ sampleRate + " channels:" + channels + " bitrate:"
					+ bitrate + " duration:" + duration);

			// 检查是否为音频文件
			if (format == null || !mime.startsWith("audio/"))
			{
				Log.e(TAG, "不是音频文件 end !");
				return;
			}

			// 实例化一个指定类型的解码器,提供数据输出
			// Instantiate an encoder supporting output data of the given mime
			// type
			mMediaCodec = MediaCodec.createDecoderByType(mime);

			if (mMediaCodec == null)
			{
				Log.e(TAG, "创建解码器失败！");
				return;
			}
			mMediaCodec.configure(format, null, null, 0);

			mMediaCodec.start();
			// 用来存放目标文件的数据
			ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
			// 解码后的数据
			ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
			// 设置声道类型:AudioFormat.CHANNEL_OUT_MONO单声道，AudioFormat.CHANNEL_OUT_STEREO双声道
			int channelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO
					: AudioFormat.CHANNEL_OUT_STEREO;
			Log.i(TAG, "channelConfiguration=" + channelConfiguration);
			extractor.selectTrack(0);
			// ==================================================================开始编码==================================================================
			boolean sawInputEOS = false;
			boolean sawOutputEOS = false;
			final long kTimeOutUs = 10;
			MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
			while (!sawOutputEOS)
			{
				try
				{
					if (!sawInputEOS)
					{
						int inputBufIndex = mMediaCodec
								.dequeueInputBuffer(kTimeOutUs);
						if (inputBufIndex >= 0)
						{
							ByteBuffer dstBuf = inputBuffers[inputBufIndex];

							int sampleSize = extractor
									.readSampleData(dstBuf, 0);
							if (sampleSize < 0)
							{
								Log.d(TAG, "saw input EOS. Stopping playback");
								sawInputEOS = true;
								sampleSize = 0;
							} else
							{
								presentationTimeUs = extractor.getSampleTime();
							}

							mMediaCodec
									.queueInputBuffer(
											inputBufIndex,
											0,
											sampleSize,
											presentationTimeUs,
											sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM
													: 0);

							if (!sawInputEOS)
							{
								extractor.advance();
							}

						} else
						{
							Log.e(TAG, "inputBufIndex " + inputBufIndex);
						}
					} // !sawInputEOS

					// decode to PCM
					int res = mMediaCodec.dequeueOutputBuffer(info, kTimeOutUs);

					if (res >= 0)
					{
						int outputBufIndex = res;
						ByteBuffer buf = outputBuffers[outputBufIndex];
						final byte[] chunk = new byte[info.size];
						buf.get(chunk);
						buf.clear();

						if (chunk.length > 0)
						{
							// TODO:send data to music box
							// chunk:解码后的数据
							// port:设备端口
							// deviceIp:设备ip地址
							// sendTOMusicBox(chunk,port,deviceIp);
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
						Log.w(TAG,
								"[AudioDecoder]output format has changed to "
										+ oformat);
					} else
					{
						Log.w(TAG,
								"[AudioDecoder] dequeueOutputBuffer returned "
										+ res);
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

		} else if ("WAV".equals(fileFormat))
		{
			Log.d(TAG, "play wav");
		} else
		{
			Log.e(TAG, "不支持 " + fileFormat + "格式文件!!!!");
		}
	}
