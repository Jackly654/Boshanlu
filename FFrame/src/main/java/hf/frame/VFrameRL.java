package hf.frame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import hf.bean.TipConfigDialog;
import hf.bean.TipConfigProgress;
import hf.control.VOver;
import hf.data.FSize;
import hf.data.ResMng;
import hf.data.SFD;
import hf.ifs.IBase;
import hf.ifs.IGeneral;
import hf.ifs.IGeneralString;
import hf.ifs.IImgPick;
import hf.ifs.INetNotification;
import hf.ifs.IOnClickOver;
import hf.lib.controls.FRela;
import hf.lib.data.Empty;
import hf.lib.data.Logger;
import hf.lib.sim.SIMMng;
import hf.util.ClipImg;
import hz.dodo.controls.SoftKeyboardStateHelper;

// 1.界面管理中枢
// 2.数据传递使者
public class VFrameRL extends FRela implements SoftKeyboardStateHelper.SoftKeyboardStateListener, SIMMng.Callback, ViewMng.IViewChanged
{
	protected
	Activity
		at;
	protected
	ResMng
		resMng;
	VFrameRoot
		vRoot;
	VOver
		vOver;
	ViewMng
		vm;
	SoftKeyboardStateHelper
		sk;
	IImgPick
		iImgPick;
	IGeneral
		iSoftKeyboardInteger;

	List<INetNotification>
		ltNet;

	public
	int
		iNetType, // 网络类型
		iInputH; // 键盘高度

	public
	String
		sChannel; // 渠道号

	public VFrameRL(final Activity at, final VFrameRoot vRoot, int vw, int vh, final String channel)
	{
		super(at, vw, vh);

		this.at = at;
		this.vRoot = vRoot;

		this.vw = vw;
		this.vh = vh;

		initSize();

		ltNet = new ArrayList<>(2);

		this.sChannel = channel;
		getVOver();

		View view = getV(SFD.V_HOME);
		if(view != null)
		{
			vm = new ViewMng(at, this, view, vw, vh);
			addView(view);
		}

		addSoftKeyboardStateListener();

		// 延迟5秒后启动优先级较低的任务
		postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				delayTask();
			}
		}, 5000);
	}
	protected void delayTask()
	{
		if(SFD.CHANNEL_DEBUG_VALUE.equals(sChannel))
		{
			showTip("延迟了5秒,开始可能需要的后台操作");
		}
	}
	public void initSize()
	{
		iInputH = 0;
	}
	public int getInputH()
	{
		return iInputH;
	}
	public void onPause()
	{
		dismissInput();
	}
	public void onResume()
	{
		dismissInput();
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		// 过滤多点触摸
		if(MotionEvent.ACTION_POINTER_DOWN == ev.getActionMasked())
		{
			return true;
		}

		return super.dispatchTouchEvent(ev);
	}
	public void showInput(EditText et)
	{
		if(vm != null)
		{
			vm.showInput(et);
		}
	}
	public void dismissInput()
	{
		if(vm != null)
		{
			vm.dismissInput();
		}
	}
	public boolean getIsForeground(final int vid)
	{
		return getV(vid) == vm.getTopView();
	}
	public VOver getVOver()
	{
		if(vOver == null)
		{
			vOver = VOver.getInstance(at, vw, vh);
		}
		return vOver;
	}
	public void showTip(final int id)
	{
		if(vRoot != null && vRoot.getVRLVisible() != SFD.S_VRL_RESUME)
		{
			return;
		}

		String value = getResMng().getString(id);
		if(Empty.isEmpty(value))
		{
			value = "Tip Code [" + id + "]";
		}
		showTip(value);
	}
	public void showTip(String value)
	{
		if(!Empty.isEmpty(value))
		{
			getVOver().showTip(value);
		}
	}
	public void showProgress(TipConfigProgress tipConfig, IGeneralString iGeneralString)
	{
		getVOver().showProgress(tipConfig, iGeneralString);
	}
	public void dismissProgress(final boolean isSuccess, final @NonNull String tag)
	{
		getVOver().dismissProgress(isSuccess, tag);
	}
	public void showDialog(TipConfigDialog tipConfig, IOnClickOver iOnClickOver)
	{
		getVOver().showDialog(tipConfig, iOnClickOver);
	}
	public String getChannel()
	{
		return sChannel;
	}
	public ResMng getResMng()
	{
		if(resMng == null)
		{
			resMng = ResMng.getInstance(getContext());
		}
		return resMng;
	}
	public void onDestroy()
	{
//		onReqDestroy(vHome);

		if(vm != null)
		{
			vm.onDestroy();
		}

		if(!Empty.isEmpty(ltNet))
		{
			ltNet.clear();
			ltNet = null;
		}

		if(sk != null)
		{
			sk.removeSoftKeyboardStateListener(this);
			sk = null;
		}
	}

	public void onReqDestroy(View view)
	{
		removeView(view);
		if(view instanceof IBase)
		{
			((IBase) view).onDestroy();
		}
	}
	protected void onNull(View view)
	{
//		if(view instanceof VLogin)
//		{
//			vLogin = null;
//		}
	}

	public void changeToView(int toView, Object... obj)
	{
		if(vm != null && !vm.isRunning())
		{
			View view = getV(toView);
			if(view != null)
			{
				vm.changeToView(view, SFD.OT_L, obj);
			}
		}
	}

	// 如需要得到网络变化通知,则调用该方法注册
	public void addNetListener(final INetNotification nnf)
	{
		if(nnf != null && ltNet != null)
		{
			ltNet.add(nnf);
		}
	}

	public void removeNetListener(final INetNotification nnf)
	{
		if(!Empty.isEmpty(ltNet))
		{
			ltNet.remove(nnf);
		}
	}

	protected void addSoftKeyboardStateListener()
	{
		if(sk == null)
		{
			sk = new SoftKeyboardStateHelper(this, this);
		}
	}
	public void setSoftKeyboardIntegerListener(IGeneral iSoftKeyboardInteger)
	{
		this.iSoftKeyboardInteger = iSoftKeyboardInteger;
	}

	public void removeSoftKeyboardIntegerListener()
	{
		this.iSoftKeyboardInteger = null;
	}
	@Override
	public void onSoftKeyboardOpened(int keyboardHeightInPx)
	{
		if(keyboardHeightInPx > 0 && keyboardHeightInPx != iInputH)
		{
			iInputH = keyboardHeightInPx;
			if(iSoftKeyboardInteger != null)
			{
				iSoftKeyboardInteger.onResponse(iInputH);
			}
		}
	}
	@Override
	public void onSoftKeyboardClosed()
	{
		if(iInputH != 0)
		{
			iInputH = 0;
		}

		if(iSoftKeyboardInteger != null)
		{
			iSoftKeyboardInteger.onResponse(iInputH);
		}
	}
	// 处理覆盖层
	private boolean onPressBackOverView()
	{
		return false;
	}
	// 返回
	public void onPressBack(int type, Object... obj)
	{
		if(onPressBackOverView())
		{
			onPressBackDebug("VRL onPressBack #1"); // 覆盖层需要处理,不必返回
			return;
		}

		if(vm != null)
		{
			View view = vm.getTopView();
			if(view == null)
			{
				onPressBackDebug("VRL onPressBack #2");
			}
			else
			{
				if(view instanceof IBase)
				{
					if(((IBase) view).onPressBack())
					{
						onPressBackDebug("VRL onPressBack #3"); // 当前界面已经消耗掉返回事件
						return;
					}
				}
				else
				{
					onPressBackDebug("VRL onPressBack #4");
				}
			}

			int vid = vm.onPressBack(type, obj);
			if(vid >= 0)
			{
			}
		}
		else
		{
			exit();
		}
	}
	private void onPressBackDebug(String value)
	{
		if(SFD.CHANNEL_DEBUG_VALUE.equals(sChannel)) // DEBUG 模式提示
		{
			showTip(value);
		}
	}
	public void exit()
	{
		if(vRoot != null)
		{
			vRoot.exit();
		}
	}

	@Override
	public void addOnLayoutChangeListener(OnLayoutChangeListener listener)
	{
		super.addOnLayoutChangeListener(listener);
	}

	public View getV(int vid)
	{
		return null;
	}
	public void imgPickCamera(IImgPick iImgPick, final int requestCode)
	{
		this.iImgPick = iImgPick;
		try
		{
			Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			at.startActivityForResult(it, requestCode);
		}
		catch(Exception exc)
		{
			Logger.e("startCamera()" + exc.toString());
		}
	}
	public void onNewIntent(Intent it)
	{
	}
	public void imgPick(IImgPick iImgPick, final int requestCode)
	{
		this.iImgPick = iImgPick;

		try
		{
			Intent it = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			at.startActivityForResult(it, requestCode);
		}
		catch(Exception ext)
		{
			Logger.e("imgPick()" + ext.toString());
		}
	}
	public void onHeadClip(Bitmap bm)
	{
		if(iImgPick != null)
		{
			iImgPick.onImage(bm);
		}
	}
	public void runUiThread(Runnable action)
	{
		if(at != null && action != null)
		{
			at.runOnUiThread(action);
		}
	}
	// SIMMng Callback Begin
	public void onNetWorkChanged(final int netType)
	{
		boolean isConnect = false;
		iNetType = netType;
		switch(netType)
		{
			case SIMMng.NET_NA: // 断开
				break;
			case SIMMng.NET_WIFI: // WIFI
				isConnect = true;
				break;
			case SIMMng.NET_2G:
			case SIMMng.NET_3G:
			case SIMMng.NET_4G:
				isConnect = true;
				break;
		}
		// 网络接通时
		if(isConnect)
		{
			onNetConnect();
		}
		else
		{
			onNetDisconnect();
		}

		if(!Empty.isEmpty(ltNet))
		{
			for(INetNotification nnf : ltNet)
			{
				if(nnf != null)
				{
					nnf.onNetChanged(isConnect, netType);
				}
			}
		}
	}
	public void onSimStatusChanged(int var1)
	{
	}
	public void onSignalChanged(int var1, int var2)
	{
	}
	public void onCdmaSignalChanged(int var1, int var2, int var3)
	{
	}
	// SIMMng Callback End
	protected void onNetConnect()
	{
	}
	protected void onNetDisconnect()
	{
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try
		{
			if (resultCode == Activity.RESULT_OK)
			{
				switch (requestCode)
				{
					case SFD.RESULT_LOAD_IMAGE:
						break;
					case SFD.RESULT_CLIP_IMAGE:
						if (data != null)
						{
							int outputX = FSize.getInstance().getRelativeWidth(180);
							ClipImg.clipPhoto(at, data.getData(), outputX, outputX);
						}
						break;
					case SFD.RESULT_CLIP_HEAD:
						if (data != null)
						{
							Bitmap bm = data.getParcelableExtra("data");
							onHeadClip(bm);
						}
						break;
					case SFD.RESULT_CAMERA:

						if (data != null)
						{
							Bitmap bm = data.getParcelableExtra("data");
							onHeadClip(bm);
						}

						break;
				}
			}
			else
			{
				showTip("获取本地图片失败");
			}
		}
		catch (Exception ext)
		{
			Logger.e("onActivityResult() requestCode:" + requestCode + ", resultCode:" + resultCode);
		}
	}
	// ↓↓↓ IViewChanged ↓↓↓
	@Override
	public void onPreAnim(int ot)
	{
		// 界面切换时,包括进/退
		if(getInputH() > 0)
		{
			dismissInput();
		}
	}
	@Override
	public void onCompAnim(int ot, View below, View above)
	{
		switch(ot)
		{
			case SFD.OT_R:

				onReqDestroy(above);
				onNull(above);
				break;
		}
	}
	@Override
	public int onIndex(View view)
	{
		return view != null ? indexOfChild(view) : -1;
	}
	@Override
	public void onAddChild(View view)
	{
		addView(view);
	}
	@Override
	public void onRemoveChild(View view)
	{
		removeView(view);
	}
	@Override
	public void onExit()
	{
		exit();
	}
	// ↑↑↑ IViewChanged ↑↑↑
}