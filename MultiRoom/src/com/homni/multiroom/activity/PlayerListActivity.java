package com.homni.multiroom.activity;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.adapter.PlayerAdapter;
import com.homni.multiroom.command.AudioCommand;
import com.homni.multiroom.command.ControlCommand;
import com.homni.multiroom.command.MusicPlayer;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.CheckClickHelp;
import com.homni.multiroom.util.GlobalValue;

import android.R.array;
import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.homni.multiroom.model.Device;
/**
 * 播放设备列表
 * 
 * @author Dawin
 *包含播放-停止-切换操作
 */
public class PlayerListActivity extends BaseActivity implements CheckClickHelp
{
	private String TAG = "PlayerListActivity";
	private static final int SHOW_PLAYER = 0;
	private ListView playerList;
	private PlayerAdapter adapter;
	private ControlCommand controlCommand;
	private List<Speaker> speakers;
	private List<Speaker> speakersStatus;
	private MusicPlayer musicPlayer;
	/**录音设备的Ip*/
	private String deviceIp;
	/**设备类型*/
	private String deviceType;
	/** 选择的喇叭id集合 */
	private List<Integer> selects = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_list);
		initView();
		GlobalValue.nowDevicePlayer=new ArrayList<Device>();
		controlCommand = new ControlCommand();
		//当前在播放的设备
		//selects = GlobalValue.currentPickPlay;
		showPlayerList();
		deviceIp=getIntent().getStringExtra("deviceIp");
		deviceType=getIntent().getStringExtra("deviceType");
		Log.w(TAG, "deviceIp="+deviceIp+" deviceType="+deviceType);	
	}

	/**
	 * 显示播放设备列表
	 */
	public void showPlayerList()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				// 设备列表
				speakers = controlCommand.getDevices("192.168.1.99");
				// 设别的喇叭状态：在线，离线
				speakersStatus = controlCommand.getDevicesStatus("192.168.1.99");
				handler.sendEmptyMessage(SHOW_PLAYER);
			}
		}.start();
	}

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{			
			switch (msg.what){
			case SHOW_PLAYER:
				adapter = new PlayerAdapter(context, speakers, speakersStatus, PlayerListActivity.this,deviceIp);
				/*for (int i = 0; i < speakersStatus.size(); i++)
				{
					if (deviceIp.equals(speakersStatus.get(i).getLeftChannel().getFromIP()))
					{
						Device device=new Device();
						device.setIp(speakers.get(i).getIpStr());
						device.setChannelType('L');
						GlobalValue.nowDevicePlayer.add(device);
					}
					if (deviceIp.equals(speakersStatus.get(i).getRightChannel().getFromIP()))
					{
						Device device=new Device();
						device.setIp(speakers.get(i).getIpStr());
						device.setChannelType('R');
						GlobalValue.nowDevicePlayer.add(device);
					}
				}*/				
				playerList.setAdapter(adapter);
				break;
			}
		}
	};

	/*************************Socket接收Server数据**************************/
	//	private DatagramSocket socket;
	//	private DatagramPacket packet;
	public void initSocket()
	{
		try
		{
			if (MainActivity.socket == null)
			{
				MainActivity.socket = new DatagramSocket(9200);
				MainActivity.socket.setSoTimeout(15000);
				MainActivity.packet = new DatagramPacket(new byte[100], 100, InetAddress.getByName("192.168.1.99"), 9200);
			}
		} catch (Exception e)
		{
			Log.e(TAG, "initSocket error" + e.getMessage());
		}
	}
	long startTime;
	long leftTime;
	public void clickView(View view)
	{
		switch (view.getId())
		{
		// 取消
		case R.id.cancel:
			AudioCommand.isSend = false;
			Log.e(TAG, "cancel selects.size()=" + selects.size());
			// 清空设备设置
			if (selects.size() > 0)
			{
				// 设置设备信息
				new Thread()
				{
					public void run()
					{
						controlCommand.clearDevicesStatus(listToItegerArray(selects));
					};
				}.start();
				GlobalValue.currentPickPlay = new ArrayList<Integer>();
				//============================停止录音设备============================
				if ("recorder".equals(deviceType))
				{
					final int[] selet = listToItegerArray(selects);
					for (int i = 0; i < selet.length; i++)
					{
						// 左喇叭放左声源，右喇叭放右声源
						final char channelType = selet[i] % 2 == 0 ? 'R' : 'L';
						final String ip = speakers.get(selet[i] / 2).getIpStr();
						new Thread()
						{
							public void run()
							{
								controlCommand.playManage((byte) 2, ip, channelType, "192.168.1.99");
							};
						}.start();						
					}
				}
			} else
			{
				Toast.makeText(context, "没有可取消的设备", 1).show();
			}
			finish();
			break;
		// 播放
		case R.id.play:
			if (GlobalValue.musics == null)
			{
				Toast.makeText(context, "未选择歌曲", 1).show();
				return;
			}

			if (selects.size() > 0)
			{
				GlobalValue.currentPickPlay = selects;
				final int[] selet = listToItegerArray(selects);
				//要多线程播放
				new Thread()
				{
					public void run()
					{
						// ==========================控制录音设备:0x41-->0x42==========================
						if ("recorder".equals(deviceType))
						{
							// 设置设备状态
							controlCommand.setDevicesStatus(selet, 'L', deviceIp);
						} else
						// ==========================控制手机==========================
						if ("phone".equals(deviceType))
						{
							controlCommand.setDevicesStatus(selet, 'L', deviceIp);
							AudioCommand.isSend = true;
							for (int i = 0; i < selet.length; i++)
							{
								// 左喇叭放左声源，右喇叭放右声源
								int channelType = selet[i] % 2 == 0 ? RIGHT_CHANNEL : LEFT_CHANNEL;
								// 只发左声道 send ip channelType port
								musicPlayer = new MusicPlayer(getIpById(selet[i]), channelType, getPortById(selet[i]));
								musicPlayer.play(GlobalValue.musics.get(GlobalValue.currentMusicId).getUrl());
							}
						}
					};
				}.start();
			} else
			{
				Toast.makeText(context, "请选择喇叭", 1).show();
			}
			break;
			//切换喇叭：当前播放的喇叭切换到空闲喇叭
		case R.id.change_channel_btn:
			if ("recorder".equals(deviceType))
			{
				// TODO:只更改设备状态表
				// TODO:选择当前在播放的喇叭，显示可切换的空闲喇叭。切换播放喇叭 ip ,port，清空当前播放的喇叭
				// musicPlayer.changeChannel(deviceIp, srcChannelType, dstPort);
			} else if ("phone".equals(deviceType))
			{
				// 手机端处理
			}		
			break;
			/*case R.id.stop_btn:
			//TODO:选择需要停止的播放设备
			if (GlobalValue.nowDevicePlayer.size() > 0)
			{
				Intent intent = new Intent(context, StopMusicActivity.class);
				//intent.putExtra("speakers", (Serializable) speakersStatus);
				startActivity(intent);
			} else
			{
				Toast.makeText(context, "没有播放的喇叭", 1).show();
			}
			
			break;
			*/
		}
	}

	/**List转int数组*/ 
	public int[] listToItegerArray(List<Integer> list)
	{			
		int[] selet = new int[selects.size()];
		for (int i = 0; i < selects.size(); i++)
		{
			selet[i] = selects.get(i);
		}
		return selet;
	}

	private final int LEFT_CHANNEL = 0;
	private final int RIGHT_CHANNEL = 1;

	public int getPortById(int playerId)
	{
		Log.d(TAG, "port=" + (playerId % 2 == 0 ? 8900 : 8901));
		return playerId % 2 == 0 ? 8900 : 8901;
	}

	public String getIpById(int playerId)
	{
		Log.d(TAG, "ip=" + speakers.get(playerId / 2).getIpStr());
		return speakers.get(playerId / 2).getIpStr();
	}

	// 监听 播放设备列表的checkbox
	@Override
	public void onClick(int position, boolean isChecked,char channelType)
	{
		Log.w(TAG, "click check box");
		Log.d(TAG, "check box id=" + position);
		if (!selects.contains(position) && isChecked)
		{
			selects.add(position);
		} else if (selects.contains(position) && !isChecked)
		{
			selects.remove(Integer.valueOf(position));
		}	
	}

	@Override
	public void initView()
	{
		playerList = (ListView) findViewById(R.id.play_listview);
	}
}
