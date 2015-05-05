package com.homni.multiroom.adapter;

import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.model.Speaker;
import com.homni.multiroom.util.ListItemClickHelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 设备列表
 * @author Dawin
 *
 */
public class DeviceAdapter extends BaseAdapter
{
	private Context context;
	private List<Speaker> speakers;
	private ListItemClickHelp callback;
	private int selectSpeakerId=-1;
	public DeviceAdapter(List<Speaker> speakers, Context context, ListItemClickHelp callback)
	{
		this.context = context;
		this.speakers = speakers;
		this.callback = callback;
	}
	
	public void setSelectSpeakerId(int selectSpeakerId)
	{
		this.selectSpeakerId = selectSpeakerId;
	}
	
	@Override
	public int getCount()
	{
		return speakers.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, final ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.item_speaker, null);
			viewHolder = new ViewHolder();
			viewHolder.ipTv = (TextView) convertView.findViewById(R.id.speaker_name);
			viewHolder.status= (TextView) convertView.findViewById(R.id.speaker_status);
			viewHolder.leftBtn = (Button) convertView.findViewById(R.id.left_channel_button);
			viewHolder.rightBtn = (Button) convertView.findViewById(R.id.right_channel_button);
			//viewHolder.selectSpeakerCb=(CheckBox)convertView.findViewById(R.id.have_select_cb);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}	
	 //   viewHolder.selectSpeakerCb.setChecked(position==selectSpeakerId?true:false);
	
		//设备状态1:在线，0：离线
		int status=speakers.get(position).getStatus();
		viewHolder.rightBtn.setEnabled(status==1?true:false);
		viewHolder.leftBtn.setEnabled(status==1?true:false);
		viewHolder.rightBtn.setText("R"+(position+1));
		viewHolder.leftBtn.setText("L"+(position+1));
		convertView.setClickable(status==1?false:true);
		
		viewHolder.ipTv.setText(speakers.get(position).getIpStr());
		viewHolder.status.setText(status==1?"在线":"离线");
		
		final View view = convertView;
		final int p = position;
		final int right = viewHolder.rightBtn.getId();
		final int left = viewHolder.leftBtn.getId();
		viewHolder.rightBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				callback.onClick(view, parent, p, right);
			}
		});
		viewHolder.leftBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				callback.onClick(view, parent, p, left);
			}
		});
		return convertView;
	}

	class ViewHolder
	{
		TextView ipTv;
		TextView status;
		Button leftBtn;
		Button rightBtn;
		//CheckBox selectSpeakerCb;
	}
}
