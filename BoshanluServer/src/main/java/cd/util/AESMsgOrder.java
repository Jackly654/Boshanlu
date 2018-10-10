package cd.util;

import hz.dodo.StrUtil;
import hz.dodo.data.AESJava;
import hz.dodo.data.Empty;


/**
 *
 * Created by fanjl on 2018-5-8.
 */

public class AESMsgOrder
{
	private
	String
		sAppSecret,
		sKey;

	public AESMsgOrder(String appSecret)
	{
		this.sAppSecret = appSecret;
	}
	public String encrypt(String value)
	{
		return !Empty.isEmpty(getKey()) ? new AESJava(getKey()).encrypt(value) : null;
	}
	public String decrypt(String value)
	{
		return !Empty.isEmpty(getKey()) ? new AESJava(getKey()).decrypt(value) : null;
	}
	// 获取密匙
	private String getKey()
	{
		if(Empty.isEmpty(sKey) && !Empty.isEmpty(sAppSecret))
		{
			sKey = !Empty.isEmpty(sAppSecret) ? getEven16(StrUtil.getMD5Lower(sAppSecret)) : null;
		}
		return sKey;
	}
	// 获取 index 为偶数的串 16位
	public static String getEven16(String value)
	{
		if(!Empty.isEmpty(value) && value.length() >= 32)
		{
			StringBuilder sb = new StringBuilder();

			int i1 = 0;
			while(i1 < value.length() && i1 <= 32)
			{
				if(i1 % 2 == 0)
				{
					sb.append(value.charAt(i1));
				}
				++i1;
			}

			String sRst = sb.toString();
			if(!Empty.isEmpty(sRst) && sRst.length() == 16)
			{
				return sRst;
			}
		}
		return null;
	}
}
