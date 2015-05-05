package com.homni.multiroom.util;

import java.util.ArrayList;
import java.util.List;

import com.homni.multiroom.model.Device;
import com.homni.multiroom.model.Music;
import com.homni.multiroom.model.Speaker;

/**
 * 全局变量
 * 
 * @author Dawin
 * 
 */
public class GlobalValue
{
	public static String localIp;
	public static boolean isAppOpen=false;
	//记录当前设备在用的喇叭
	public static List<Device> nowDevicePlayer=new ArrayList<Device>();
	
	/**记录当前选择的喇叭*/
	public static List<Integer> currentPickPlay=new ArrayList<Integer>();
	//8个喇叭对应播放的ip
	public static String ip1;
	public static String ip2;
	public static String ip3;
	public static String ip4;
	public static String ip5;
	public static String ip6;
	public static String ip7;
	public static String ip8;
	//8个喇叭对应播放的port
	public static String port1;
	public static String port2;
	public static String port3;
	public static String port4;
	public static String port5;
	public static String port6;
	public static String port7;
	public static String port8;
	
	/** 当前选择的喇叭Id  默认第一个 */
	public static int currentSpeakerId;
	/** 当前选择的歌曲Id */
	public static int currentMusicId;
	public static List<Speaker> speakers;
	//存储存储卡所有歌曲信息
	public static ArrayList<Music> musics;
	/**记录已在播放的喇叭,最多两个同时播放*/
	//public static List<Speaker>  speakerPlay=new ArrayList<Speaker>();	
}
