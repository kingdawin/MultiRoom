package com.homni.multiroom.activity;

import com.homni.multiroom.R;
import com.homni.multiroom.adapter.MusicAdapter;
import com.homni.multiroom.util.GlobalValue;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 歌曲列表界面
 * 
 * @author Dawin
 *
 */
public class SongsActivity extends BaseActivity
{
	private static final String TAG = "SongsActivity";
	// 歌曲适配器
	private MusicAdapter musicAdapter;
	private ListView musicListView;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);	
		initView();		
		musicAdapter = new MusicAdapter(context, GlobalValue.musics);
		musicListView.setAdapter(musicAdapter);
		topRightBtn.setVisibility(View.GONE);
		// 点击列表歌曲事件
		musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				GlobalValue.currentMusicId = position;
				titleBarNameTv.setText(GlobalValue.musics.get(position).getMusicName());
			}
		});
		// 显示已选歌曲
		if (GlobalValue.musics != null)
		{
			titleBarNameTv.setText(GlobalValue.musics.get(GlobalValue.currentMusicId).getMusicName());
		}
	}

	public void initView()
	{
		topRightBtn = (Button) findViewById(R.id.right_button);
		titleBarNameTv = (TextView) findViewById(R.id.title_bar_name);
		musicListView = (ListView) findViewById(R.id.song_listview);
	}

	// 返回
	public void return0(View v)
	{
		Intent intent = new Intent();
		// 放入返回值,返回选择的歌曲ID
		intent.putExtra("currentMusicId", GlobalValue.currentMusicId);
		// 放入回传的值,并添加一个Code,方便区分返回的数据
		setResult(1, intent);
		finish();
	}
}
