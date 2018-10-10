package cd.s.data;

import java.util.Hashtable;

import hz.dodo.data.AESJava;
import hz.dodo.data.Empty;

/**
 * Created by fanjl on 2017-7-14.
 */

public class JniData
{
	public static final String WX_APPID = "WX_APPID";
	public static final String WX_SECRET = "WX_SECRET";
	public static final String WB_APPKEY = "WB_APPKEY";
	public static final String WB_SECRET = "WB_SECRET";
	public static final String TWIT_APPKEY = "TWIT_APPKEY";
	public static final String TWIT_SECRET = "TWIT_SECRET";
	public static final String XM_APPKEY = "XM_APPKEY";
	public static final String XM_APPID = "XM_APPID";
	public static final String GD_APPKEY = "GD_APPKEY";
	public static final String CDAD_APPID = "CDAD_APPID";
	public static final String CDAD_APPKEY = "CDAD_APPKEY";

	private
	static
	JniData
		mThis;
	AESJava
		aesJava;
	String
		sKey,
		sAppKeyUCenter,
		sMd5,
		sProtocol,
		sProtocolSSL,
		sDomainISMP,
		sDomainZQ,
		sDomainSearch,
		sDomainUCenter;

	Hashtable<String, String>
		htKeys;

	public static JniData getInstance()
	{
		if(mThis == null)
		{
			synchronized(JniData.class)
			{
				if(mThis == null)
				{
					mThis = new JniData();
				}
			}
		}
		return mThis;
	}
	private JniData()
	{
		getAESJava();
		getSignMd5();
	}
	public AESJava getAESJava()
	{
		if(aesJava == null)
		{
			aesJava = new AESJava(getKey());
		}
		return aesJava;
	}
	public String getKey()
	{
		try
		{
			if(sKey == null)
			{
				sKey = hf.key.cd.FKey.getAesKey();
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sKey;
	}
	public String getSignMd5()
	{
		try
		{
			if(sMd5 == null)
			{
				sMd5 = getAESJava().decrypt(hf.key.cd.FKey.getSignMd5()); // 签名
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sMd5;
	}
	public String getProtocol()
	{
		try
		{
			if(sProtocol == null)
			{
				sProtocol = getAESJava().decrypt(hf.key.cd.FKey.getProtocol());
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sProtocol;
	}
	public String getProtocolSSL()
	{
		try
		{
			if(sProtocolSSL == null)
			{
				sProtocolSSL = getAESJava().decrypt(hf.key.cd.FKey.getProtocolSSL());
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sProtocolSSL;
	}
	public String getDomainISMP()
	{
		try
		{
			if(Empty.isEmpty(sDomainISMP))
			{
				sDomainISMP = getAESJava().decrypt(hf.key.cd.FKey.getDomainISMP());
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sDomainISMP;
	}
	public String getDomainZQ()
	{
		try
		{
			if(Empty.isEmpty(sDomainZQ))
			{
				sDomainZQ = getAESJava().decrypt(hf.key.cd.FKey.getDomainZQ());
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sDomainZQ;
	}
	public String getDomainUCenter()
	{
		try
		{
			if(Empty.isEmpty(sDomainUCenter))
			{
				sDomainUCenter = getAESJava().decrypt(hf.key.cd.FKey.getDomainUCenter());
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sDomainUCenter;
	}
	public String getDomainSearch()
	{
		try
		{
			if(Empty.isEmpty(sDomainSearch))
			{
				sDomainSearch = getAESJava().decrypt(hf.key.cd.FKey.getDomainSearch());
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sDomainSearch;
	}
	public String getAppKeyUCenter()
	{
		try
		{
			if(Empty.isEmpty(sAppKeyUCenter))
			{
				sAppKeyUCenter = getAESJava().decrypt(hf.key.cd.FKey.getAppKeyUCenter());
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		return sAppKeyUCenter;
	}
	public String getAppKey(String name)
	{
		if(htKeys == null)
		{
			htKeys = initAppKeys();
		}
		if(htKeys != null && !Empty.isEmpty(name))
		{
			return htKeys.get(name);
		}
		return null;
	}
	private Hashtable<String, String> initAppKeys()
	{
		try
		{
			String value = getAESJava().decrypt(hf.key.cd.FKey.getAppKey());
			if(!Empty.isEmpty(value))
			{
				String[] sArr = value.split("&");
				if(!Empty.isEmpty(sArr))
				{
					Hashtable<String, String> ht = new Hashtable<String, String>();
					String[] sArr1;
					int i1 = 0;
					while(i1 < sArr.length)
					{
						if(!Empty.isEmpty(sArr1 = sArr[i1].split("=")) && sArr1.length == 2)
						{
							ht.put(sArr1[0], sArr1[1]);
						}
						++i1;
					}
					
					return ht;
				}
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		
		return null;
	}
}
