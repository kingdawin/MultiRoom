package com.homni.multiroom.activity;

import com.homni.multiroom.R;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.GlobalValue;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

/**
 *设置界面
 * @author Dawin
 *
 */
public class SettingActivity extends BaseActivity
{
	private final String TAG="SpeakerSettingActivity";
	private CheckBox leftChannelCb;
	private CheckBox rightChannelCb;
	//设置歌曲
	private Button musicBtn;
	//用户选择要设置的喇叭
	private Speaker userPickSpeaker;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speaker_setting);
		initView();
		//接收设备列表传递的喇叭对象
		userPickSpeaker = (Speaker) getIntent().getSerializableExtra("speakerObj");
		//当前选择的歌曲
		if (GlobalValue.musics != null)
		{
			musicBtn.setText(GlobalValue.musics.get(GlobalValue.currentMusicId).getMusicName());
		}
	}
	
	public void initView()
	{
		musicBtn = (Button) findViewById(R.id.music_button);
		leftChannelCb = (CheckBox) findViewById(R.id.left_channel);
		rightChannelCb = (CheckBox) findViewById(R.id.right_channel);
	}

	
   /**
	* 选择播放的喇叭声道
	* 
	* @param view
	* 
	*/
	public void selectChannel(View view)
	{
		switch (view.getId())
		{
		//选择左声道
		case R.id.left_channel:
			if (leftChannelCb.isChecked())
			{
				userPickSpeaker.setLeftStatus(true);
				userPickSpeaker.getLeftChannel().setSelect(true);
			} else
			{
				userPickSpeaker.setLeftStatus(false);
				userPickSpeaker.getLeftChannel().setSelect(false);
			}
			break;
		//选择右声道
		case R.id.right_channel:
			if (rightChannelCb.isChecked())
			{
				userPickSpeaker.setRightStatus(true);
				userPickSpeaker.getRightChannel().setSelect(true);
			} else
			{
				userPickSpeaker.setRightStatus(false);
				userPickSpeaker.getRightChannel().setSelect(false);
			}
			break;
		}
	}

	/**
	 * 切换到歌曲列表,选择歌曲
	 * @param view
	 */
	public void selectMusic(View view)
	{
		Intent intent = new Intent(context, SongsActivity.class);		
		// 请求码 >=0 this code will be returned in onActivityResult() when the activity exits
		startActivityForResult(intent, 1);		
	}
	
	/**
	 * 保存用户设置
	 */
	public void confirm(View view)
	{
		int defaultValue=0;
		GlobalValue.currentSpeakerId=getIntent().getIntExtra("currentSpeakerId", defaultValue);
		Log.d(TAG, "GlobalValue.currentSpeakerId="+GlobalValue.currentSpeakerId);			
		
		GlobalValue.speakers.get(GlobalValue.currentSpeakerId).setRightStatus(userPickSpeaker.isRightStatus());		
		GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getRightChannel().setSelect(userPickSpeaker.getRightChannel().isSelect());
		GlobalValue.speakers.get(GlobalValue.currentSpeakerId).setLeftStatus(userPickSpeaker.isLeftStatus());
		GlobalValue.speakers.get(GlobalValue.currentSpeakerId).getLeftChannel().setSelect(userPickSpeaker.getLeftChannel().isSelect());
		Intent intent = new Intent();			
		setResult(1, intent);		
		finish();
	}

	/**
	 * 当跳转的activity(被激活的activity)使用完毕, 销毁的时候调用该方法
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 1)
		{
			// 显示选择的歌曲名
			musicBtn.setText(GlobalValue.musics.get(GlobalValue.currentMusicId).getMusicName());
		}
	}
    
}
