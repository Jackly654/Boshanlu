package cd.s;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetInfo
{
	public static boolean isWIFI(Context ctx)
	{
		if(ctx != null)
		{
			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			
			if (info != null && info.isAvailable() && info.isConnected())
			{
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					if (ConnectivityManager.TYPE_WIFI == info.getType())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
