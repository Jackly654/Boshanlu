package cd.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;
import hf.http.util.Empty;

// http://blog.csdn.net/hbcui1984/article/details/5201247
// AES加密解密工具类 
public class AES128
{
	private static final String UTF_8 = "UTF-8";

	/** 
     * 加密 
     * @param content 需要加密的内容 
     * @param password  加密密码 
     * @return 
     */  
    @SuppressLint ("TrulyRandom")
	public static String encrypt(String key, String src)
	{
		try
		{
			if(key == null || key.length() <= 0 || src == null || src.length() <= 0) return null;
			
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] byteContent = src.getBytes(UTF_8);
			if(!Empty.isEmpty(byteContent))
			{
				cipher.init(Cipher.ENCRYPT_MODE, skey);// 初始化
				byte[] result = cipher.doFinal(byteContent);
				if(!Empty.isEmpty(result))
				{
					return byte2Base64(result); // 加密
				}
			}
		}
		catch (Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
    
    public static String decrypt(String key, String src)
	{
		try
		{
			if(key == null || key.length() <= 0 || src == null || src.length() <= 0) return null;
			
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] byteContent = base642byte(src);
			if(!Empty.isEmpty(byteContent))
			{
				cipher.init(Cipher.DECRYPT_MODE, skey);// 初始化
				byte[] result = cipher.doFinal(byteContent);
				if(!Empty.isEmpty(result))
				{
					return new String(result); // 加密
				}
			}
		}
		catch (Exception ext)
		{
			ext.printStackTrace();
		}
		return null;
	}
    
    private static String byte2Base64(byte[] encode)
	{
		try
		{
			return Base64.encodeToString(encode, Base64.NO_WRAP);

		}
		catch (Exception ext)
		{
			ext.printStackTrace();
		}

		return null;
	}
    
    private static byte[] base642byte(String str)
    {
    	return Base64.decode(str, Base64.NO_WRAP);
    }
}
