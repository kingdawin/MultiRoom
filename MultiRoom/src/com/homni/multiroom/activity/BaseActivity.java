package com.homni.multiroom.activity;

import com.homni.multiroom.util.ActivityUtil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity的基类
 * 
 * @author Dawin
 * 
 *         所有Activity都继承此类
 */
public abstract class BaseActivity extends Activity
{
	//顶部左按钮
	public Button topLeftBtn;
	//顶部右按钮
	public Button topRightBtn;
	//顶部标题
	public TextView titleBarNameTv;
	/** 上下文对象:指整个程序的环境 */
	public Context context;
	/** 是否允许全屏 */
	private boolean mAllowFullScreen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		// 将Activity加入List容器
		ActivityUtil.getInstance().addActivity(this);
		// 去标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏锁定
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// 是否全屏
		if (mAllowFullScreen)
		{
			// 取消标题
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		// 隐藏软键盘
		//hideSoftInputView();
	}

	public abstract void initView();

	/**
	 * 隐藏软键盘
	 */
	public void hideSoftInputView()
	{
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
		{
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/** 返回 */
	public void return0(View v)
	{
		finish();
	}

}
