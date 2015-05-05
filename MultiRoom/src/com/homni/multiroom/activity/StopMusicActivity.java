package com.homni.multiroom.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.adapter.PlayerAdapter;
import com.homni.multiroom.adapter.StopPlayerAdapter;
import com.homni.multiroom.command.ControlCommand;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.CheckClickHelp;
import com.homni.multiroom.util.GlobalValue;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;

//显示正在播放的播放的喇叭
public class StopMusicActivity extends BaseActivity implements CheckClickHelp
{
	private ControlCommand controlCommand;
	private ListView playerList;
	private StopPlayerAdapter adapter;
	private final String TAG="StopMusicActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_wait_stop);
		playerList=(ListView)findViewById(R.id.play_listview);

		Log.d(TAG," GlobalValue.nowDevicePlayer.size()"+ GlobalValue.nowDevicePlayer.size());
		adapter=new StopPlayerAdapter(context, GlobalValue.nowDevicePlayer, StopMusicActivity.this);
		playerList.setAdapter(adapter);
	}


	
	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(int position, boolean isChecked,char channelType)
	{
		// TODO Auto-generated method stub
		
	}

	public void clickView(View view)
	{

	}
	// controlCommand.playManage((byte) 2, "192.168.1.220", 'L',
	// "192.168.1.99");
}