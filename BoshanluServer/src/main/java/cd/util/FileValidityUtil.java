package cd.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

import hz.dodo.FileUtil;
import hz.dodo.Logger;
import hz.dodo.StrUtil;
import hz.dodo.data.Empty;

public class FileValidityUtil
{
	public static final String HASH_MD5 = "MD5";
	public static final String HASH_SHA1 = "SHA1";
	public static final String HASH_SHA256 = "SHA256";
	public static final String HASH_SHA384 = "SHA384";
	public static final String HASH_SHA512 = "SHA512";

	public static String getHash(String path)
	{
		return getHash(path, HASH_SHA512);
	}
	public static String getHash(String path, String hashType)
	{
		RandomAccessFile randomAccessFile = null;
		try
		{
			File file;
			if(!Empty.isEmpty(path) && !Empty.isEmpty(hashType) && (file = FileUtil.isExists(path)) != null)
			{
				randomAccessFile = new RandomAccessFile(file,"r");

				byte buffer[] = new byte[1024 * 2];
				MessageDigest md = MessageDigest.getInstance(hashType);
				int numRead;
				while((numRead = randomAccessFile.read(buffer)) > 0)
				{
					md.update(buffer, 0, numRead);
				}
				return StrUtil.byte2hex(md.digest());
			}
		}
		catch(Exception e)
		{
			Logger.e("FileValidityUtil getHash error == " + e.toString());
		}
		finally
		{
			if(randomAccessFile != null)
			{
				try
				{
					randomAccessFile.close();
				}
				catch(IOException e)
				{
					Logger.e("FileValidityUtil getHash close error == " + e.toString());
				}
			}
		}
		return "";
	}
}
