package com.homni.multiroom.adapter;

import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.model.Channel;
import com.homni.multiroom.model.CurrentSelectedChannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 可切声道
 * 
 * 下拉选项 spinner
 * @author Dawin
 *
 */
public class ChannelAdapter extends BaseAdapter
{
	private List<CurrentSelectedChannel> mList;
	private Context mContext;

	public ChannelAdapter(Context context, List<CurrentSelectedChannel> list)
	{
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount()
	{
		return mList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mList;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.swap_channel, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
			viewHolder.channelName = (TextView) convertView.findViewById(R.id.tv_channel_name);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.deviceName.setText(mList.get(position).getDeviceName());
		viewHolder.channelName.setText(mList.get(position).getChannelName());
		return convertView;
	}

	private static class ViewHolder
	{
		TextView deviceName;
		TextView channelName;
	}
}
