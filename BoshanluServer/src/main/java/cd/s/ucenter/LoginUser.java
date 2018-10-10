package cd.s.ucenter;

import android.content.Context;
import hz.dodo.SPUtil;
import hz.dodo.SystemUtil;
import hz.dodo.data.AESJava;
import hz.dodo.data.Empty;
import hz.dodo.data.MacAddress;
import cd.s.CacheMng;
import cd.s.JsonParserUserCenter;
import cd.s.data.DR;
import cd.s.data.DataBase;
import cd.s.data.User;

public class LoginUser extends DataBase
{
	Context
		ctx;

	static
	LoginUser
		mThis;

	public
	User
		user;
	AESJava
		aesJava;
	
	String
		sUnique;
	
	public static LoginUser getInstance(Context ctx, AESJava aesJava)
	{
		if (mThis == null)
		{
			synchronized (LoginUser.class)
			{
				if (mThis == null)
				{
					mThis = new LoginUser(ctx, aesJava);
				}
			}
		}
		return mThis;
	}
	private LoginUser(Context ctx, AESJava aesJava)
	{
		this.ctx = ctx;
		this.aesJava = aesJava;
		getLoginUser();
	}
	public String getUId()
	{
		getLoginUser();
		return user != null ? user.sId : "";
	}
	public void signIn(String uId)
	{
		SPUtil.saveString(ctx, DR.SP_TABLE_GEN, DR.SP_KEY_UID, uId);
		resetUser();
		getLoginUser();
	}
	public void signOut()
	{
		SPUtil.remove(ctx, DR.SP_TABLE_GEN, DR.SP_KEY_UID);
		resetUser();
	}
	public void resetUser()
	{
		user = null;
	}
	//TODO 修改为先取MAC地址再取IMEI
	public String getUniqueId()
	{
		if(Empty.isEmpty(sUnique))
		{
			String unique = MacAddress.getMac(ctx);
			if(Empty.isEmpty(unique))
			{
				unique = SystemUtil.getIMEI(ctx);
			}
			sUnique = !Empty.isEmpty(unique) ? unique.replaceAll(":", "") : null;
		}
		return sUnique;
	}
	// 优先返回登录用户uId,未空时返回mac
	public String getAccountId()
	{
		String uId = getUId();
		return !Empty.isEmpty(uId) ? uId : getUniqueId();
	}
	public User getLoginUser()
	{
		return getLoginUser(false);
	}
	// 参数:是否强制从文件读取
	public User getLoginUser(boolean force)
	{
		if(user == null || force)
		{
			String uId = SPUtil.getString(ctx, DR.SP_TABLE_GEN, DR.SP_KEY_UID, null);
			if(!Empty.isEmpty(uId))
			{
				String value = new CacheMng(ctx, aesJava).readAccount(uId);
				if(!Empty.isEmpty(value))
				{
					user = JsonParserUserCenter.getUser(value);
				}
			}
		}
		return user;
	}
}
