package com.homni.multiroom.adapter;

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
 * 播放设备
 * 
 * 喇叭多选
 * 
 * @author Dawin
 *
 */
public class PlayerAdapter extends BaseAdapter
{
	private List<Speaker> mList;
	private List<Speaker> speakersStatus;
	private Context mContext;
	private CheckClickHelp callback;
	String deviceIp;
	public PlayerAdapter(Context context, List<Speaker> list, List<Speaker> speakersStatus,CheckClickHelp callback,String deviceIp)
	{
		mContext = context;
		mList = list;
		this.speakersStatus = speakersStatus;
		this.callback=callback;
		this.deviceIp=deviceIp;
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
			viewHolder.playerTv = (TextView) convertView.findViewById(R.id.player_ip_tv);
			viewHolder.leftCheckBox = (CheckBox) convertView.findViewById(R.id.player_left);
			viewHolder.rightCheckBox = (CheckBox) convertView.findViewById(R.id.player_right);
			viewHolder.leftStatusTextView = (TextView) convertView.findViewById(R.id.left_status);
			viewHolder.rightStatusTextView = (TextView) convertView.findViewById(R.id.right_status);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}	
		
		final int p = position;
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
		});		
		
		//==========================判断是否已被用户设置================================
		/*if(GlobalValue.currentPickPlay.contains(Integer.valueOf(position*2))){
			//左
			viewHolder.leftCheckBox.setChecked(true);
		}else
		{
			viewHolder.leftCheckBox.setChecked(false);
		}
		if(GlobalValue.currentPickPlay.contains(Integer.valueOf(position*2+1))){
			//右
			viewHolder.rightCheckBox.setChecked(true);
		}else {
			viewHolder.rightCheckBox.setChecked(false);
		}
		*/
		// 设备状态1:在线，0：离线
		int status = mList.get(position).getStatus();
		
		 //离线，禁止操作
		if(status==0){
			viewHolder.leftCheckBox.setEnabled(false);
			viewHolder.rightCheckBox.setEnabled(false);
			viewHolder.leftCheckBox.setTextColor(0xABABAB);
			viewHolder.rightCheckBox.setTextColor(0xABABAB);
		}
		//在线
		else {
			viewHolder.leftCheckBox.setEnabled(true);
			viewHolder.rightCheckBox.setEnabled(true);			
			viewHolder.leftCheckBox.setTextColor(0xFFFFFFFF);
			viewHolder.rightCheckBox.setTextColor(0xFFFFFFFF);			
		}
		if(speakersStatus.size()>0){
			// 左喇叭
			if (speakersStatus.get(position).getLeftChannel().getFromIP().length() == NONE)
			{
				viewHolder.leftStatusTextView.setText("空闲");
				viewHolder.leftStatusTextView.setTextColor(Color.WHITE);	
				viewHolder.leftCheckBox.setChecked(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getLeftChannel().getFromIP()) ? true :false);
			} else
			{
				viewHolder.leftStatusTextView.setText(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getLeftChannel().getFromIP()) ? "在用" : "占用");
				viewHolder.leftCheckBox.setChecked(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getLeftChannel().getFromIP()) ? true :false);
				viewHolder.leftStatusTextView.setTextColor(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getLeftChannel().getFromIP()) ? Color.GREEN : Color.RED);
			}
			// 右喇叭
			if (speakersStatus.get(position).getRightChannel().getFromIP().length() == NONE)
			{
				viewHolder.rightStatusTextView.setText("空闲");
				viewHolder.rightCheckBox.setChecked(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getRightChannel().getFromIP()) ? true :false);
				viewHolder.rightStatusTextView.setTextColor(Color.WHITE);
			} else
			{
				viewHolder.rightStatusTextView.setText(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getRightChannel().getFromIP()) ? "在用" : "占用");
				viewHolder.rightCheckBox.setChecked(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getRightChannel().getFromIP()) ? true :false);
				viewHolder.rightStatusTextView.setTextColor(deviceIp/*GlobalValue.localIp*/.equals(speakersStatus.get(position).getRightChannel().getFromIP()) ? Color.GREEN : Color.RED);
			}
		}
		
		
		viewHolder.playerTv.setText(mList.get(position).getIpStr() + " " + (status == 1 ? "在线" : "离线"));
        
		return convertView;
	}

	private static class ViewHolder
	{
		TextView playerTv;
		TextView leftStatusTextView;
		TextView rightStatusTextView;
		CheckBox leftCheckBox;
		CheckBox rightCheckBox;
	}
}
