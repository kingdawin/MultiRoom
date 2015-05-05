package com.homni.multiroom.adapter;

import java.util.List;

import com.homni.multiroom.R;
import com.homni.multiroom.model.Music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {
	public Context mContext;
	public List<Music> musics;

	public MusicAdapter(Context context, List<Music> musics) {
		this.musics = musics;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return musics.size();
	}

	@Override
	public Object getItem(int position) {
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_song, null);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.title);
			viewHolder.tvSinger = (TextView) convertView
					.findViewById(R.id.singer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// ¸èÃû
		viewHolder.tvTitle.setText(musics.get(position).getMusicName());
		// ×¨¼­-¸èÊÖ
		viewHolder.tvSinger.setText(new StringBuffer()
				.append(musics.get(position).getAlbumName()).append("-")
				.append(musics.get(position).getArtist()));
		return convertView;
	}

	private final static class ViewHolder {
		TextView tvTitle;
		TextView tvSinger;
	}

}
