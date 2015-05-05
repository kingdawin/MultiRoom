package com.homni.multiroom.adapter;

import java.util.HashMap;
import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.model.Device;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.CheckClickHelp;
import com.homni.multiroom.util.GlobalValue;
import com.homni.multiroom.util.ListItemClickHelp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 可停止播放的喇叭
 * 
 * 喇叭多选
 * 
 * @author Dawin
 *
 */
public class StopPlayerAdapter extends BaseAdapter
{
	//playIP channelType
	private Context mContext;
	private CheckClickHelp callback;
	//List<HashMap<String, String>> list;
	List<Device> list;
	public StopPlayerAdapter(Context context,/*List<HashMap<String, String>> list*/List<Device> list,CheckClickHelp callback)
	{
		mContext = context;
		this.list=list;
		this.callback=callback;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return null;
		//return mList;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
	/**"0.0.0.0"=7，没有设备占用*/ 
	private final int NONE=7;
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_player, null);
			viewHolder = new ViewHolder();
			viewHolder.playerIpTv = (TextView) convertView.findViewById(R.id.player_ip_tv);
			viewHolder.leftCheckBox = (CheckBox) convertView.findViewById(R.id.player_left);
			viewHolder.rightCheckBox = (CheckBox) convertView.findViewById(R.id.player_right);
			viewHolder.leftStatusTextView = (TextView) convertView.findViewById(R.id.left_status);
			viewHolder.rightStatusTextView = (TextView) convertView.findViewById(R.id.right_status);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}	
		
	/*	final int p = position;
        //注册checkbox监听，传递到Activity
		viewHolder.rightCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				callback.onClick(p * 2 + 1, isChecked,'R');
			}
		});
		viewHolder.leftCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				callback.onClick(p * 2, isChecked,'L');
			}
		});	*/	
		viewHolder.playerIpTv.setText(list.get(position).getIp());
		viewHolder.leftCheckBox.setText(""+list.get(position).getChannelType());
		//==========================判断是否已被用户设置================================
	
		return convertView;
	}

	private static class ViewHolder
	{
		TextView playerIpTv;
		TextView leftStatusTextView;
		TextView rightStatusTextView;
		CheckBox leftCheckBox;
		CheckBox rightCheckBox;
	}
}
