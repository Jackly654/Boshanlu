package hf.data;

import android.content.Context;
import android.content.res.Resources;

import hf.lib.data.Logger;

/**
 *
 * Created by duxin on 2016/8/18.
 */
public class ResMng
{
	private
	static
	ResMng
		resMng;

	Resources
		resources;

	public static ResMng getInstance(Context context)
	{
		if(resMng == null)
		{
			synchronized(ResMng.class)
			{
				if(resMng == null)
				{
					resMng = new ResMng(context);
				}
			}
		}
		return resMng;
	}

	private ResMng(Context context)
	{
		resources = context.getResources();
	}

	public String getString(int id)
	{
		try
		{
			if(id != 0)
			{
				return resources.getString(id);
			}
		}
		catch(Exception e)
		{
			Logger.e("ResMng getString error == " + e.toString());
		}
		return "";
	}

	public String[] getStringArray(int id)
	{
		try
		{
			if(id != 0)
			{
				return resources.getStringArray(id);
			}
		}
		catch(Exception e)
		{
			Logger.e("ResMng getStringArray error == " + e.toString());
		}
		return null;
	}
}
