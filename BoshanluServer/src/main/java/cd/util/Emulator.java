package cd.util;

import hf.http.util.Empty;
import hz.dodo.Logger;

public class Emulator
{
	private static String getSystemProperty(String name)
	{
		try
		{
			if(!Empty.isEmpty(name))
			{
				Class<?> clazz = Class.forName("android.os.SystemProperties");
				if(clazz != null)
				{
					Object obj = clazz.getMethod("get", new Class[]{String.class}).invoke(clazz, new Object[]{name});
					if(obj != null)
					{
						return obj.toString();
					}
				}
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			Logger.e("getSystemProperty() " + exc.toString());
		}
		return "";
	}
	public static boolean isEmulator()
	{
		try
		{
			// 判断主板平台 比如:麒麟960=hi3660 / 高通骁龙835=msm8998 模拟器一般为空
			String platform = getSystemProperty("ro.board.platform");
			// 判断基带 模拟器一般为空
			String baseband = getSystemProperty("gsm.version.baseband");

			Logger.i("platform:" + platform + " baseband:" + baseband);
			return Empty.isEmpty(platform) || Empty.isEmpty(baseband);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			Logger.i("isEmulator() " + exc.toString());
		}
		return false;
	}
}
