package hf.control;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import hf.bean.TipConfigDialog;
import hf.ifs.IOnClickOver;
import hf.lib.data.Empty;
import hf.lib.data.Logger;

public class V7Dialog extends AlertDialog.Builder
{
	Activity
		at;
	AlertDialog
		mDialog;
	IOnClickOver
		iOnClickOver;
	TipConfigDialog
		config;

	public V7Dialog(Activity at)
	{
		super(at/*, android.R.style.Theme_Material_Light_Dialog_Alert*/);
		this.at = at;

		setCancelable(false);
	}
	public void onDestroy()
	{
		if(mDialog != null)
		{
			mDialog.dismiss();
			mDialog = null;
		}
		iOnClickOver = null;
		config = null;
	}
	public void show(TipConfigDialog tipConfig, IOnClickOver iOnClickOver)
	{
		dismiss();

		this.iOnClickOver = iOnClickOver;
		config = tipConfig;

		at.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(config != null)
				{
					setIcon(config.iBmResId);
					setTitle(Empty.isEmpty(config.sTitle) ? "" : config.sTitle);
					setMessage(Empty.isEmpty(config.sMsg) ? "" : config.sMsg);
					setNegativeButton(Empty.isEmpty(config.sBtnNegative) ? "" : config.sBtnNegative, null);
					setPositiveButton(Empty.isEmpty(config.sBtnPositive) ? "" : config.sBtnPositive, null);
				}

				mDialog = create();
				if(mDialog != null)
				{
					mDialog.show();

					if(config != null)
					{
						setPositiveButton(mDialog, config.iClrPos);
						setNegativeButton(mDialog, config.iClrNeg);
					}
				}
			}
		});
	}
	public void dismiss()
	{
		at.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(mDialog != null && mDialog.isShowing())
				{
					mDialog.dismiss();
					mDialog = null;

					if(iOnClickOver != null && config != null)
					{
						iOnClickOver.onClick(config.sTag, 0, "Auto");
					}
				}
			}
		});
	}
	public void setPositiveButton(final AlertDialog dialog, final int positiveBtnColor)
	{
		try
		{
			if(dialog != null)
			{
				Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				if(positiveButton != null)
				{
					positiveButton.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
							toCallback("" + ((Button)v).getText());
						}
					});
					positiveButton.setAllCaps(false);
					positiveButton.setTextColor(positiveBtnColor);
				}
			}
		}
		catch(Exception ext)
		{
			Logger.e("V7Dialog setPositiveButton " + ext.toString());
		}
	}
	public void setNegativeButton(final AlertDialog dialog, final int negativeBtnColor)
	{
		try
		{
			if(dialog != null)
			{
				Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
				if(negativeButton != null)
				{
					negativeButton.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
							toCallback("" + ((Button)v).getText());
						}
					});
					negativeButton.setAllCaps(false);
					negativeButton.setTextColor(negativeBtnColor);
				}
			}
		}
		catch(Exception ext)
		{
			Logger.e("V7Dialog setNegativeButton " + ext.toString());
		}
	}
	protected void toCallback(final String sItem)
	{
		try
		{
			if(iOnClickOver != null)
			{
				iOnClickOver.onClick(config != null ? config.sTag : "", 0, sItem);
			}
		}
		catch(Exception ext)
		{
			Logger.e("V7Dialog toCallback " + ext.toString());
		}
	}
}
