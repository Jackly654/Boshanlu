package hf;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import hf.data.SFD;
import hf.frame.VFrameRoot;
import hf.lib.data.Empty;
import hf.lib.data.Logger;

public class FrameAt extends Activity
{
	VFrameRoot
		vRoot;
	protected
	String
		sChannel;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY); // 顶部出现复制/粘贴等功能时,不影响布局,悬浮方式

		// reqTrans(this);

		sChannel = getApplicationMetaData(getPackageName(), SFD.CHANNEL_NAME);
		if(Empty.isEmpty(sChannel))
		{
			sChannel = SFD.CHANNEL_DEBUG_VALUE;
		}

		Logger.init(this, SFD.CHANNEL_DEBUG_VALUE.equals(sChannel));
	}
	protected void setRootView(VFrameRoot vRoot)
	{
		setContentView(this.vRoot = vRoot);
	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		if(intent != null && vRoot != null)
		{
			vRoot.onNewIntent(intent);
		}
	}
	public String getApplicationMetaData(final String pkgName, final String keyName)
	{
		try
		{
			if(Empty.isEmpty(pkgName))
			{
				return null;
			}

			ApplicationInfo appi = getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
			Bundle bundle = appi.metaData;
			if (bundle != null)
			{
				try
				{
					Object obj = bundle.get(keyName);
					if(obj != null)
					{
						return obj.toString();
					}
				}
				catch(Exception exc)
				{
					exc.printStackTrace();
					Logger.e("getApplicationMetaData() " + exc.toString());
				}
			}
		}
		catch (Exception exc)
		{
			Logger.e("getApplicationMetaData() " + exc.toString());
		}
		return null;
	}
	public void reqTrans(Activity at)
	{
		Window window = at.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
		else
		{
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}
	public void clearTrans(View view)
	{
		Window window = getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.setStatusBarColor(0x7F000000);
		}
		else
		{
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		if(view != null)
		{
			ViewCompat.requestApplyInsets(view);
		}
	}
	protected void onResume()
	{
		super.onResume();
		try
		{
			if(vRoot != null)
			{
				vRoot.onResume();
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	protected void onPause()
	{
		super.onPause();
		try
		{
			if(vRoot != null)
			{
				vRoot.onPause();
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	protected void onDestroy()
	{
		try
		{
			if(vRoot != null)
			{
				vRoot.onDestroy();
			}
			super.onDestroy();

			android.os.Process.killProcess(android.os.Process.myUid());
			System.exit(0);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	@Override
	public void onBackPressed()
	{
		if(vRoot != null)
		{
			vRoot.onPressBack();
		}
		else
		{
			exit();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(vRoot != null)
		{
			vRoot.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(vRoot != null)
		{
			vRoot.onActivityResult(requestCode, resultCode, data);
		}
	}
	public void exit()
	{
		finish();
	}
}