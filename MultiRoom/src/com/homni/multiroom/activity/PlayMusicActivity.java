package com.homni.multiroom.activity;

import java.util.ArrayList;
import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.adapter.ChannelAdapter;
import com.homni.multiroom.command.AudioCommand;
import com.homni.multiroom.command.ControlCommand.ChannelType;
import com.homni.multiroom.model.CurrentSelectedChannel;
import com.homni.multiroom.util.GlobalValue;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 音乐控制：播放、暂停、停止
 * 
 * @author Dawin
 *
 */
public class PlayMusicActivity extends BaseActivity
{
	private static final String TAG = "MusicPlayActivity";
	// 发送音频协议
	private AudioCommand audioCommand = new AudioCommand();	
	//声道切换选项
	private Spinner spnChangeChannel;
	private ChannelAdapter channelAdapter;
	// 当前可切声道
	//String[] itemsChannel = getResources().getStringArray(R.array.spinnername);
	//播放声源左或右声道
	private ChannelType channelType;
	private List<CurrentSelectedChannel> selectedChannelList;
	/**被切换喇叭在下拉框的位置，当执行切换后，根据此变量删除下拉框此条目*/
	//private int selectPosition;
	private final int SOURCE_LEFT_CHANNEL=0;
	private final int SOURCE_RIGHT_CHANNEL=1;
	//声源声道类型,默认播放声源左声道
	private int sourceChannelType=SOURCE_LEFT_CHANNEL;
	/** 喇叭ID从1,2,3...排号*/
	private int channelID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_play);		
		selectedChannelList= getChannelSelected();
		channelAdapter=new ChannelAdapter(context,selectedChannelList);
		sourceChannelType = getIntent().getIntExtra("channelType", -1);
		spnChangeChannel.setAdapter(channelAdapter);		
		spnChangeChannel.setOnItemSelectedListener(channelItemtClick);			
	}

	OnItemSelectedListener channelItemtClick=new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			Toast.makeText(context, "onItemSelected position="+position,1).show();		
			channelID=selectedChannelList.get(position).getChannelID();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{	
			
		}	
	};
	
	@Override
	public void initView()
	{
		spnChangeChannel=(Spinner)findViewById(R.id.from_channel);		
	}
	
	/** 查询所有被选择的喇叭 */
	public List<CurrentSelectedChannel> getChannelSelected()
	{
		int speakersSize = GlobalValue.speakers.size();
		List<CurrentSelectedChannel> selectedChannelList = new ArrayList<CurrentSelectedChannel>();
		for (int i = 0; i < speakersSize; i++)
		{
			// TODO:可切换喇叭选项中不显示当前喇叭
			if ( GlobalValue.speakers.get(i).getLeftChannel().isSelect()&&(GlobalValue.currentSpeakerId * 2 + 1!= i * 2 + 1) )
			{
				CurrentSelectedChannel currentSelectedChannel = new CurrentSelectedChannel();
				currentSelectedChannel.setDeviceName(GlobalValue.speakers.get(i).getIpStr());
				currentSelectedChannel.setChannelName("Left");	
				currentSelectedChannel.setChannelID(i * 2 + 1);
				selectedChannelList.add(currentSelectedChannel);
			}
			if (GlobalValue.speakers.get(i).getRightChannel().isSelect()&&(GlobalValue.currentSpeakerId * 2 + 2 != i * 2 +2)  )
			{
				CurrentSelectedChannel currentSelectedChannel = new CurrentSelectedChannel();
				currentSelectedChannel.setDeviceName(GlobalValue.speakers.get(i).getIpStr());
				currentSelectedChannel.setChannelID(i * 2 + 2);
				currentSelectedChannel.setChannelName("Right");
				selectedChannelList.add(currentSelectedChannel);
			}
		}
		return selectedChannelList;
	}
	/**
	 * 切换声道
	 * 
	 * @param view
	 * 
	 */
	public void changeChannel(View view)
	{
		switch (channelID)
		{
		/*=========================1,3,5,7左声道===========================*/
		case 1:
			GlobalValue.speakers.get(0).getLeftChannel().setSelect(false);
		    //改变ip
			GlobalValue.ip1=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			//TODO:改变port
			break;		
		case 3:
			GlobalValue.speakers.get(1).getLeftChannel().setSelect(false);
			GlobalValue.ip3=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			break;
		case 5:
			GlobalValue.speakers.get(2).getLeftChannel().setSelect(false);
			GlobalValue.ip5=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			break;
		case 7:
			GlobalValue.speakers.get(3).getLeftChannel().setSelect(false);
			GlobalValue.ip7=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			break;
			/*=========================2,4,6,8右声道===========================*/
		case 2:
			GlobalValue.speakers.get(0).getRightChannel().setSelect(false);
			GlobalValue.ip2=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			break;
		case 4:
			GlobalValue.speakers.get(1).getRightChannel().setSelect(false);
			GlobalValue.ip4=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			break;	
		case 6:
			GlobalValue.speakers.get(2).getRightChannel().setSelect(false);
			GlobalValue.ip6=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			break;	
		case 8:
			GlobalValue.speakers.get(3).getRightChannel().setSelect(false);
			GlobalValue.ip8=GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getIpStr();
			break;
		}
	}
	
	
	// 选择声源声道
	public void pickSourceChanelType(View view)
	{
		switch (view.getId())
		{
		case R.id.source_left_channel:
			sourceChannelType=SOURCE_LEFT_CHANNEL;
			break;
		case R.id.source_right_channel:
			sourceChannelType=SOURCE_RIGHT_CHANNEL;
			break;
		}
	}
	
	/** 播放控制 */
	public void playControl(View view)
	{	
		if (GlobalValue.musics == null)
		{
			Toast.makeText(context, "未选歌曲", 1).show();
			return;
		}

		if (sourceChannelType == SOURCE_LEFT_CHANNEL)
		{
			channelType = ChannelType.LEFT;
		} else if (sourceChannelType == SOURCE_RIGHT_CHANNEL)
		{
			channelType = ChannelType.RIGHT;
		}
		// TODO:判断是否已开启
		switch (view.getId())
		{
		//TODO:向服务器查询设备状态，其他设备正在播放，显示占用，禁止播放操作
		// 播放分两种情况：1继续--播放2初始状态--播放
		case R.id.play_button:
			Log.d(TAG, "PLAY");
			// 1 从未播放转到播放状态
			//if (audioCommand.getState() == AudioCommand.IDLE)
			//{
			//TODO:判断是否已经在播放
				new Thread()
				{
					public void run()
					{
						Log.d(TAG, "IDLE to PLAY");
						// 记录已选的喇叭
						if (channelType == ChannelType.LEFT)
						{
							GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getLeftChannel().setSelect(true);
							channelID=GlobalValue.currentSpeakerId*2+1;
						} else
						{
							GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getRightChannel().setSelect(true);
							channelID=GlobalValue.currentSpeakerId*2+2;
						}						
						//TODO:如果选择切换声道,将被切换的喇叭channel select改为false					
						audioCommand.play(
								GlobalValue.musics.get(GlobalValue.currentMusicId).getUrl(), 
								GlobalValue.speakers.get(GlobalValue.currentSpeakerId),
								channelType,
								channelID
								);
					};
				}.start();
		  //}
		  // 2 暂停状态转到播放
	      /*else if (audioCommand.getState() == AudioCommand.PAUSE)
			{
				Log.d(TAG, "PAUSE to PLAY");
				audioCommand.setState(AudioCommand.PLAY);
				audioCommand.wakeUp();
			}*/
			break;
		// 暂停
		case R.id.pause_button:
			audioCommand.pause();
			break;
		// 停止
		case R.id.stop_button:
			audioCommand.stop();
			break;
		}
	}
	
}
