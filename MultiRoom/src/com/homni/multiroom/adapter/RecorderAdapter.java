package com.homni.multiroom.adapter;

import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.model.Recorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 录音设备列表
 * 
 * @author Dawin
 *
 */
public class RecorderAdapter extends BaseAdapter
{
	List<Recorder> recorders;
	Context context;

	public RecorderAdapter(List<Recorder> recorders, Context context)
	{
		this.recorders = recorders;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return recorders.size();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_recorder, null);
			viewHolder = new ViewHolder();
			viewHolder.ipTv = (TextView) convertView.findViewById(R.id.speaker_name);
			viewHolder.status= (TextView) convertView.findViewById(R.id.speaker_status);
			viewHolder.leftBtn = (Button) convertView.findViewById(R.id.left_channel_button);
			viewHolder.rightBtn = (Button) convertView.findViewById(R.id.right_channel_button);
			viewHolder.tableLayout=(TableLayout)convertView.findViewById(R.id.player_table_layout);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder=(ViewHolder)convertView.getTag();
		}	
		
//
//		/* Create a new row to be added. */
//		TableRow tr = new TableRow(context);
//		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//		/* Create a Button to be the row-content. */
//		Button b = new Button(context);
//		b.setText("Dynamic Button");
//		b.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//		/* Add Button to row. */
//		tr.addView(b);
//		/* Add row to TableLayout. */
//		tr.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
   
		//设备状态1:在线，0：离线
		int status=recorders.get(position).getStatus();
		viewHolder.rightBtn.setEnabled(status==1?true:false);
		viewHolder.leftBtn.setEnabled(status==1?true:false);
		viewHolder.rightBtn.setText("R"+(position+1));
		viewHolder.leftBtn.setText("L"+(position+1));
	//	convertView.setClickable(status==1?false:true);
		
		viewHolder.ipTv.setText(recorders.get(position).getIp());
		viewHolder.status.setText(status==1?"在线":"离线");
		
		final View view = convertView;
		final int p = position;
		final int right = viewHolder.rightBtn.getId();
		final int left = viewHolder.leftBtn.getId();
	/*	viewHolder.rightBtn.setOnClickListener(new OnClickListener()
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
		});*/
		return convertView;
	}

	class ViewHolder
	{
		TextView ipTv;
		TextView status;
		Button leftBtn;
		Button rightBtn;
		TableLayout tableLayout;
		//CheckBox selectSpeakerCb;
	}
}
