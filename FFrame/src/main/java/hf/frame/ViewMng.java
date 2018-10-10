package hf.frame;

import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import hf.data.SFD;
import hf.ifs.IBase;
import hf.lib.controls.AnimTrans;
import hf.lib.data.Empty;
import hf.lib.data.Logger;
import hf.util.FHandler;

// 界面管理
class ViewMng implements Handler.Callback
{
	interface IViewChanged
	{
		void onPreAnim(int ot);
		void onCompAnim(int ot, View below, View above);
		int onIndex(View view);
		void onAddChild(View view);
		void onRemoveChild(View view);
		void onExit();
	}

	InputMethodManager
		imm;

	Activity
		at;
	IViewChanged
		iViewChanged;

	FHandler
		handler;
	AnimTrans
		anim;

	List<View>
		ltStack; // 界面进退栈

	int
		fw, fh, transX;
	boolean
		bAnim;

	public ViewMng(final Activity at, IViewChanged iViewChanged, final View view, final int fw, int fh)
	{
		this.at = at;
		this.iViewChanged = iViewChanged;
		this.fw = fw;
		this.fh = fh;
		transX = fw/4;

		handler = new FHandler(this);
		imm = ((InputMethodManager)at.getSystemService(Activity.INPUT_METHOD_SERVICE));
		ltStack = new ArrayList<>(5);
		anim = new AnimTrans();
		push(view);
		if(view instanceof IBase)
		{
			((IBase) view).onChangedView(IBase.S_ENTER_COMPLETE, null);
		}
	}
	public void onDestroy()
	{
		if(!Empty.isEmpty(ltStack))
		{
			ltStack.clear();
		}
		ltStack = null;

		if(handler != null)
		{
			handler.clear();
		}
		handler = null;
		anim = null;
	}
	// 键盘
	public void showInput(EditText et)
	{
		try
		{
			if(et != null)
			{
				et.setFocusable(true);
				et.setFocusableInTouchMode(true);
				et.requestFocus();
			}

			if(imm != null)
			{
				imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}
		catch(Exception exc)
		{
			Logger.e("showInput() " + exc.toString());
		}
	}
	public void dismissInput()
	{
		try
		{
//			if(vrl != null)
			{
				if(imm != null)
				{
					if(at != null)
					{
						View view = at.getCurrentFocus();
						if(view != null)
						{
							IBinder iBinder = view.getWindowToken();
							if(iBinder != null)
							{
								imm.hideSoftInputFromWindow(iBinder/*vrl.getWindowToken()*/, 0);
							}
						}
					}
				}
			}
		}
		catch(Exception exc)
		{
			Logger.e("dismissInput() " + exc.toString());
		}
	}
	// 压栈
	private boolean push(View view)
	{
		pull(view);
		return ltStack != null && ltStack.add(view);
	}
	// 出栈
	private boolean pull(View view)
	{
		return ltStack != null && ltStack.remove(view);
	}
	// 栈顶view
	public View getTopView()
	{
		return stackSize() > 0 ? ltStack.get(ltStack.size() - 1) : null;
	}
	// 栈顶的下面的一个view
	public View getBelowTopView()
	{
		if(stackSize() > 1)
		{
			return ltStack.get(ltStack.size() - 2);
		}
		return null;
	}
	private int stackSize()
	{
		return ltStack != null ? ltStack.size() : 0;
	}
	public boolean changeToView(View toView, int ot, Object obj)
	{
		if(bAnim || toView == null || getTopView() == toView)
		{
			return false;
		}
		bAnim = true;

//		if(vrl != null)
//		{
//			vrl.dismissInput();
//		}
		if(iViewChanged != null)
		{
			iViewChanged.onPreAnim(ot);
		}

		if(ot == SFD.OT_L)
		{
			push(toView);
		}

		Message msg = handler.obtainMessage(SFD.MSG_CHANGE_VIEW);
		msg.arg1 = ot;
		msg.obj = obj;
		handler.sendMessage(msg);

		return true;
	}
	// enter
	private void onEnterPrev(final View leftView, final View rightView)
	{
		if(leftView == null || rightView == null || iViewChanged == null)
		{
			return;
		}

		int left = iViewChanged.onIndex(leftView); // vrl.indexOfChild(leftView);
		int right = iViewChanged.onIndex(rightView); // vrl.indexOfChild(rightView);

		if(right < 0) // 未在父类中
		{
			// vrl.addView(rightView);
			iViewChanged.onAddChild(rightView);
		}
		else if(left > right)
		{
			// vrl.removeView(rightView);
			// vrl.addView(rightView);

			iViewChanged.onRemoveChild(rightView);
			iViewChanged.onAddChild(rightView);
		}
		if(rightView.getTranslationX() != fw)
		{
			rightView.setTranslationX(fw);
		}
	}
	// back
	private void onBackPrev(final View leftView, final View rightView)
	{
		if(leftView != null && leftView.getTranslationX() != -transX)
		{
			leftView.setTranslationX(-transX);
		}
		if(rightView != null && rightView.getTranslationX() != 0)
		{
			rightView.setTranslationX(0);
		}
	}
	public boolean handleMessage(final Message msg)
	{
		switch(msg.what)
		{
			case SFD.MSG_CHANGE_VIEW:
				
				final View
					v1 = getTopView(),
					v2 = getBelowTopView();

				switch(msg.arg1)
				{
					case SFD.OT_R:

						onBackPrev(v2, v1);

						// 数据传递给目标view
//						if(v2 instanceof IBase)
//						{
//							((IBase)v2).onData(IBase.S_RESUME_PREV, msg.obj);
//						}

						if(v1 instanceof IBase)
						{
							((IBase)v1).onChangedView(IBase.S_LEAVE_PREV, msg.obj);
						}
						if(v2 instanceof IBase)
						{
							((IBase)v2).onChangedView(IBase.S_RESUME_PREV, msg.obj);
						}

						if(anim != null)
						{
							anim.TO_LR(new AnimTrans.IAnimTrans()
							{
								@Override
								public void onStart()
								{
									bAnim = true;
								}
								@Override
								public void onEnd(View view, View view1)
								{
									bAnim = false;
									pull(getTopView());

									if(v1 instanceof IBase)
									{
										((IBase)v1).onChangedView(IBase.S_LEAVE_COMPLETE, msg.obj);
									}
									if(v2 instanceof IBase)
									{
										((IBase)v2).onChangedView(IBase.S_RESUME_COMPLETE, msg.obj);
									}

									onAnimComplete(SFD.OT_R, v2, v1);
								}
							}, v1, fw, v2, transX);
						}

						break;
					case SFD.OT_L:

						// 检查界面层次
						onEnterPrev(v2, v1);
						// 数据传递给目标view
						if(v1 instanceof IBase)
						{
//							((IBase)v1).onData(IBase.S_ENTER_PREV, msg.obj);
						}

						if(v2 instanceof IBase)
						{
							((IBase)v2).onChangedView(IBase.S_PAUSE_PREV, msg.obj);
						}
						if(v1 instanceof IBase)
						{
							((IBase)v1).onChangedView(IBase.S_ENTER_PREV, msg.obj);
						}

						if(anim != null)
						{
							anim.TO_LR(new AnimTrans.IAnimTrans()
							{
								@Override
								public void onStart()
								{
									bAnim = true;
								}
								@Override
								public void onEnd(View view, View view1)
								{
									bAnim = false;

									if(v1 instanceof IBase)
									{
										((IBase)v1).onChangedView(IBase.S_ENTER_COMPLETE, msg.obj);
									}
									if(v2 instanceof IBase)
									{
										((IBase)v2).onChangedView(IBase.S_PAUSE_COMPLETE, msg.obj);
									}
									onAnimComplete(SFD.OT_L, v2, v1);
								}
							}, v2, -transX, v1, -fw);
						}
						break;
				}

				break;
		}
		return true;
	}
	// 切面切换完成时会调用,如果需要,子类可以继承
	protected void onAnimComplete(int to, View below, View above)
	{
		if(iViewChanged != null)
		{
			iViewChanged.onCompAnim(to, below, above);
		}
	}
	// 返回
	public int onPressBack(int type, Object... obj)
	{
		int iRst = -1;
//		if(vrl != null && vrl.getInputH() > 0)
//		{
//			dismissInput();
//			if(type != SFD.BACK_AUTO)
//			{
//				return iRst;
//			}
//		}
		if(ltStack.size() <= 1)
		{
			switch(type)
			{
				case SFD.BACK_KEY:
				case SFD.BACK_TOUCH:
					if(iViewChanged != null)
					{
						iViewChanged.onExit();
						iRst = -1;
					}
					break;
			}
		}
		else
		{
			if(!changeToView(getBelowTopView(), SFD.OT_R, obj))
			{
				iRst = -1;
			}
		}
		return iRst;
	}
	public boolean isRunning()
	{
		return bAnim;
	}
}
