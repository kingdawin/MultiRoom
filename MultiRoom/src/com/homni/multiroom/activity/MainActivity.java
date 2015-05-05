package com.homni.multiroom.activity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.adapter.RecorderAdapter;
import com.homni.multiroom.command.ControlCommand;
import com.homni.multiroom.model.Recorder;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.GlobalValue;
import com.homni.multiroom.util.ListItemClickHelp;
import com.homni.multiroom.util.MusicLoader;
import com.homni.multiroom.util.PhoneInfoHelp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 主界面
 * 
 * @author Dawin
 * 搜索录音设备流程：
 * 查找服务器IP--->注册手机--->用服务器IP查找设备列表--->利用GridView和BaseAdapter显示设备列表
 */
public class MainActivity extends BaseActivity implements ListItemClickHelp 
{
	private final String TAG = "GridViewActivity";	
	private Animation animation;
	/*************************View*************************/
	// 可用喇叭--改为-->录音设备
	private GridView recorderDeviceGridView;
	//左右声道控制按钮
	private Button leftChannelBtn;
	private Button rightChannelBtn;
	private ControlCommand controlCommand;
	/** 服务器Ip*/
	private String serverIp;
	// 喇叭适配器
	//DeviceAdapter deviceAdapter;
	private RecorderAdapter recorderAdapter;
	/*************************Socket接收Server数据**************************/
	public static DatagramSocket socket;
	public static DatagramPacket packet;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speakers);
		GlobalValue.isAppOpen = true;
		initView();
		controlCommand = new ControlCommand();
		//设置本机IP
		GlobalValue.localIp=PhoneInfoHelp.getLocalIP(context);
		// 加载手机歌曲列表
		if (GlobalValue.musics == null)
		{
			GlobalValue.musics = MusicLoader.getInstance(getContentResolver()).getMusicList();
		}
		// 透明度动画
		animation = AnimationUtils.loadAnimation(this, R.anim.pulse);
		recorderDeviceGridView.setOnItemClickListener(recorderItemClickListener);
		//注册手机
		//registerDevice();
		
		// new Thread()
		// {
		// public void run()
		// {
		// // 启动心跳
		// controlCommand.isAlive(/*serverIp*/"192.168.1.99");
		// };
		// }.start();
	}
	
	/**注册手机*/ 
	public void registerDevice()
	{
		initSocket();
		new Thread()
		{
			public void run()
			{
				if (controlCommand.isRegisterDeviceOK((byte) 0, "192.168.1.99"))
				{
					new Thread()
					{
						public void run()
						{
							while (true && GlobalValue.isAppOpen)
							{
								try
								{
									Log.d(TAG, "receive...");
									socket.receive(packet);
									Log.w(TAG, "Server--->Android cmd=0x" + String.format("%02X", packet.getData()[11]));
									if (String.format("%02X", packet.getData()[11]).equals("99"))
									{
										// 回复server
										controlCommand.androidToServerKeepAlive("192.168.1.99");
									}
								} catch (Exception e)
								{
									Log.e(TAG, "接收Server心跳包 error break!!：" + e.getMessage());
									break;
									// TODO:退出注册
								}
							}
						};
					}.start();
				}
			};
		}.start();
	}
	public void initSocket()
	{
		try
		{
			socket = new DatagramSocket(9200);
			socket.setSoTimeout(15000);
			packet = new DatagramPacket(new byte[100], 100, InetAddress.getByName("192.168.1.99"), 9200);
		} catch (Exception e)
		{
			Log.e(TAG, "initSocket error" + e.getMessage());
		}
	}
	
    /**点击录音设备事件*/
	private OnItemClickListener recorderItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{	
			if (ControlCommand.recorders.size() > 0)
			{
				Intent intent = new Intent(context, PlayerListActivity.class);
				// 传设备Ip
				intent.putExtra("deviceIp", ControlCommand.recorders.get(position).getIp());
				// 传设备类型：录音设备|手机
				intent.putExtra("deviceType", "recorder");
				startActivityForResult(intent, 1);
			} else
			{
				Toast.makeText(context, "没有录音设备，重新搜索", 1).show();
			}					
		}
	};
	//设置退出后，更新设备列表。
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(recorderAdapter/*deviceAdapter*/==null)return;
		
		if (resultCode == 1)
		{
			Log.d(TAG, "device update");			
			//recorderAdapter/*deviceAdapter*/.setSelectSpeakerId(GlobalValue.currentSpeakerId);
			recorderAdapter/*deviceAdapter*/.notifyDataSetChanged();
		}
	}
	
	public void initView()
	{		
		leftChannelBtn = (Button) findViewById(R.id.left_channel_button);
		rightChannelBtn = (Button) findViewById(R.id.right_channel_button);
		recorderDeviceGridView = (GridView) findViewById(R.id.gridView1);
	}
	/** 查找服务器 */
	private static final int SEARCH_SERVER = 0;
	/** 显示设备列表 */
	private static final int SHOW_DEVICES = 1;
	private List<Recorder> recorders;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			// 查询服务器，获取设备列表
			case SEARCH_SERVER:
				//找到服务器
				if (serverIp.length() > 0)
				{
					new Thread()
					{
						public void run()
						{							
							controlCommand = new ControlCommand();
							//用ip找设备列表
							GlobalValue.speakers = controlCommand.getDevices(serverIp);		
							recorders=ControlCommand.recorders;
							handler.sendEmptyMessage(SHOW_DEVICES);
						};
					}.start();

				} else
				{
					//未找到服务器，清空设备列表
					GlobalValue.speakers = new ArrayList<Speaker>();
					recorders=new ArrayList<Recorder>();
					recorderAdapter=new RecorderAdapter(recorders,context);
					recorderDeviceGridView.setAdapter(recorderAdapter);
					
					//deviceAdapter = new DeviceAdapter(GlobalValue.speakers, context,SpeakersActivity.this);
					//loudSpeakerGv.setAdapter(deviceAdapter);
				}
				break;
			case SHOW_DEVICES:
				//deviceAdapter = new DeviceAdapter(GlobalValue.speakers, context,SpeakersActivity.this);			
				//loudSpeakerGv.setAdapter(deviceAdapter);
				recorderAdapter=new RecorderAdapter(recorders,context);
				recorderDeviceGridView.setAdapter(recorderAdapter);
				break;		
			}
		}
	};

	/**点击设备的声道*/ 
	@Override
	public void onClick(View item, View widget, int position, int which)
	{
		Log.d(TAG, "list button position=" + position);
		// TODO:判断声道是否已开启
		if (position == GlobalValue.currentSpeakerId)
		{
			switch (which)
			{
			case R.id.left_channel_button:
				if (GlobalValue.speakers.get(GlobalValue.currentSpeakerId).isLeftStatus())
				{
					Intent intent = new Intent(context, PlayMusicActivity.class);
					//0:左声道，1：右声道
					intent.putExtra("channelType", 0);					
					startActivity(intent);
				}
				break;
			case R.id.right_channel_button:		
				if (GlobalValue.speakers.get(GlobalValue.currentSpeakerId).isRightStatus())
				{
					Intent intent = new Intent(context, PlayMusicActivity.class);
					//0:左声道，1：右声道
					intent.putExtra("channelType", 1);
					startActivity(intent);
				}
				break;
			}
		}
	}
	
	public void clickView(View view)
	{
		switch (view.getId())
		{
		// 选择播放设备
		case R.id.pick_player_btn:
			Intent intent = new Intent(context, PlayerListActivity.class);
			intent.putExtra("deviceIp", GlobalValue.localIp);
			intent.putExtra("deviceType", "phone");
			startActivityForResult(intent, 1);
			break;
		// 搜索服务器
		case R.id.search_server_btn:
			new Thread()
			{
				@Override
				public void run()
				{
					// 查找服务器IP
					serverIp = controlCommand.getServerIP();
					handler.sendEmptyMessage(SEARCH_SERVER);
				}
			}.start();
			break;
		// 设置歌曲
		case R.id.set_music_btn:
			Intent intent0 = new Intent(context, SongsActivity.class);
			// 请求码 >=0 this code will be returned in onActivityResult() when the
			// activity exits
			startActivityForResult(intent0, 1);
			break;
		}
	}
	 
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		GlobalValue.isAppOpen=false;
	}	
}
