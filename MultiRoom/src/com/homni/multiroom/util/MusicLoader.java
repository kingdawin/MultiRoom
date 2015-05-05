package com.homni.multiroom.util;

import java.util.ArrayList;

import com.homni.multiroom.model.Music;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;

/**
 * 音乐信息加载
 * 
 * @author Dawin
 *
 */
public class MusicLoader {
	private static  final String TAG="MusicLoader";
	private static MusicLoader mMusicLoader;
	private static final Uri URI = Media.EXTERNAL_CONTENT_URI;
	private ArrayList<Music> musicInfos=new ArrayList<Music>();
	private static ContentResolver mContentResolver;
	
	public static MusicLoader getInstance(ContentResolver contentResolver) {
		if (mMusicLoader == null) {
			//使用Context提供的getContentResolver()方法获取ContentResolver对象
			mContentResolver=contentResolver;
			mMusicLoader = new MusicLoader();
		}
		return mMusicLoader;
	}
	public MusicLoader(){
		//返回所有在外部存储卡上的音乐文件的信息
		/*
		说明
		Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
		//Uri：指明要查询的数据库名称加上表的名称，从MediaStore中我们可以找到相应信息的参数。
	    //Projection: 指定查询数据库表中的哪几列，返回的游标中将包括相应的信息。Null则返回所有信息。
	    //selection: 指定查询条件
	    //selectionArgs：参数selection里有 ?这个符号是，这里可以以实际值代替这个问号。如果selection这个没有？的话，那么这个String数组可以为null。
	    //SortOrder：指定查询结果的排列顺序	    
	    */
		Cursor cursor = mContentResolver.query(URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER/*null*/);
		if (cursor == null)
			{
				//查找歌曲失败
				LogUtil.e(TAG,"Music Loader cursor == null.  query failed, handle error.");
			} else if (!cursor.moveToFirst())
			{
				//设备没有歌曲
				LogUtil.e(TAG,"Music Loader cursor.moveToFirst() returns false.   no media on the device");
			} else
			{				
				/*-----------歌曲信息下标---------*/				
				int displayNameCol = cursor.getColumnIndex(Media.TITLE);
				int albumCol = cursor.getColumnIndex(Media.ALBUM);
				int albumIDCol=cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
				int idCol = cursor.getColumnIndex(Media._ID);
				int durationCol = cursor.getColumnIndex(Media.DURATION);
				int sizeCol = cursor.getColumnIndex(Media.SIZE);
				int artistCol = cursor.getColumnIndex(Media.ARTIST);
				// 文件路径
				int urlCol = cursor.getColumnIndex(Media.DATA);					
				int isMusicCol=cursor.getColumnIndex(/*MediaStore.Audio.*/Media.IS_MUSIC);
				
				/*------------------将设备所有歌曲的各种信息保存到ArrayList容器中-------------*/
				do
					{
					    // 是否为音乐：1是  0不是，是铃声或...
					    int isMusic =cursor.getInt(isMusicCol);	
					    if(isMusic==1){
					    	//歌曲ID（主键）
							long id = cursor.getLong(idCol);
							// 总时间
							int duration = cursor.getInt(durationCol);
							//歌名
							String title = cursor.getString(displayNameCol);
							//专辑名
							String album = cursor.getString(albumCol);						
							// 专辑ID
							long albumid = cursor.getLong(albumIDCol);
							//歌手
							String artist = cursor.getString(artistCol);
							//歌曲路径
							String url = cursor.getString(urlCol);
							String fileFormat=url.substring(url.lastIndexOf(".")+1);
							// 文件大小
							long size = cursor.getLong(sizeCol);							
							/*--------------------*/
							Music musicInfo = new Music();	
							musicInfo.setMusicId(id);
							musicInfo.setMusicName(title+"."+fileFormat);
							musicInfo.setAlbumName(album);
							musicInfo.setDuration(duration);
							musicInfo.setSize(size);
							musicInfo.setArtist(artist);
							musicInfo.setUrl(url);
							musicInfo.setAlbumId(albumid);
							//是否是音乐
							musicInfo.setIsMusic(isMusic);
							musicInfo.setFileFormat(fileFormat);
							musicInfos.add(musicInfo);
					    }						
					} while (cursor.moveToNext());
			}		
	}
	public ArrayList<Music> getMusicList(){
		return musicInfos;
	}
}
