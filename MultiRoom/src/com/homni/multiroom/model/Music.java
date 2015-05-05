package com.homni.multiroom.model;

/**
 * 音乐
 * @author Dawin
 *
 */
public class Music {	
	// 歌曲ID
	private long musicId;
	// 专辑ID
	private long albumId;
	// 标题
	private String musicName;
	// 专辑名
	private String albumName;
	// 歌曲持续时间
	private int duration;
	private long size;
	// 歌手
	private String artist;
	// 歌曲路径
	private String url;
	//是否是音乐
	private int isMusic;
	//格式
	private String fileFormat;
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public long getMusicId() {
		return musicId;
	}
	public void setMusicId(long musicId) {
		this.musicId = musicId;
	}
	public long getAlbumId() {
		return albumId;
	}
	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}
	public String getMusicName() {
		return musicName;
	}
	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getIsMusic() {
		return isMusic;
	}
	public void setIsMusic(int isMusic) {
		this.isMusic = isMusic;
	}
	
}
