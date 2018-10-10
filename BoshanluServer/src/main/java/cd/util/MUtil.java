package cd.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import hz.dodo.FileUtil;
import hz.dodo.Logger;
import hz.dodo.data.Empty;

public class MUtil
{
	public static final String CODE_UTF8 = "UTF-8";
	public static final String CODE_UTF16 = "UTF-16BE";
	public static final String CODE_UNICODE = "Unicode";
	public static final String CODE_GBK = "GBK";
	public static final String CODE_GB18030 = "GB18030";
	
	private
	static
	MUtil
			mThis;
	
	private
	ThreadLocal<SimpleDateFormat>
			threadLocal,
			threadLocalHan;

	public static MUtil getInstance()
	{
		if(mThis == null)
		{
			synchronized(MUtil.class)
			{
				if(mThis == null)
				{
					mThis = new MUtil();
				}
			}
		}

		return mThis;
	}
	
	private MUtil()
	{
		threadLocal = new ThreadLocal<SimpleDateFormat>()
		{
			@Override
			protected SimpleDateFormat initialValue()
			{
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				return simpleDateFormat;
			}
		};
		
		threadLocalHan = new ThreadLocal<SimpleDateFormat>()
		{
			@Override
			protected SimpleDateFormat initialValue()
			{
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.ENGLISH);
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				return simpleDateFormat;
			}
		};
	}
	
	public boolean moveFile(String srcFileName, String destDir)
	{
		try
		{
			if(!Empty.isEmpty(srcFileName) && !Empty.isEmpty(destDir))
			{
				File srcFile;
				if((srcFile = FileUtil.isExists(srcFileName)) != null)
				{
					String directoryPath = srcFile.getParent();
					
					if(!Empty.isEmpty(directoryPath))
					{
						if(!directoryPath.endsWith(File.separator))
						{
							directoryPath += File.separator;
						}
						
						if(directoryPath.equals(destDir))
						{
							return true;
						}
					}
					
					File destFile = new File(destDir);
					if (!destFile.exists())
					{
						if(!destFile.mkdirs()) // 创建多级目录结构
						{
							return false;
						}
					}

					return srcFile.renameTo(new File(destDir + File.separator + srcFile.getName()));
				}
			}
		}
		catch (Exception e)
		{
			Logger.e("Util moveFile error == " + e.toString());
			e.printStackTrace();
		}

		return false;
	}
	
	public long getHanLongTime(final String time)
	{
		try
		{
			if(!Empty.isEmpty(time))
			{
				SimpleDateFormat format = threadLocalHan.get();
				
				Date date = format.parse(time);
				return date.getTime();
			}
		}
		catch (Exception e)
		{
			Logger.e("Util getHanLongTime error == " + e.toString());
			e.printStackTrace();
		}
		
		return System.currentTimeMillis();
	}
	
	public String getTime()
	{
		return getTime(System.currentTimeMillis());
	}
	
	public String getTime(final long time)
	{
		SimpleDateFormat format = threadLocal.get();
		return format.format(new Date(time));
	}
	
	//服务类的读取拼接
	public String read(final String abspath, final String code, final int pageSize)
	{
		FileInputStream fis = null;
		try
		{
			File file = FileUtil.isExists(abspath);
			if (file == null)
				return null;

			fis = new FileInputStream(file);
			InputStreamReader inputStrReader = new InputStreamReader(fis, code);
			BufferedReader buffereReader = new BufferedReader(inputStrReader);
			StringBuilder sb = new StringBuilder();
			String line = "";
			if(pageSize > 0)
			{
				int i1 = 0;
				while (i1 < pageSize)
				{
					if((line = buffereReader.readLine()) != null)
					{
						if(i1 == 0)
						{
							//构建json格式
							sb.append("[");
						}
						sb.append(line).append("\n");
					}
					i1 ++;
				}
			}
			else
			{
				//构建json格式
				sb.append("[");
				while ((line = buffereReader.readLine()) != null)
				{
					sb.append(line).append("\n");
				}
			}
			sb.append("]");
			buffereReader.close();
			inputStrReader.close();
			
			return sb.toString();
		}
		catch (Exception e1)
		{
			System.out.println("FileUtil read error: " + e1.toString());
		}
		finally
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (Exception e1)
			{
				Logger.e("read(abspath)" + e1.toString());
			}
		}
		return null;
	}
	
	//应用二级目录的数据读取拼接
	public String readIndex(final String abspath, final String code)
	{
		FileInputStream fis = null;
		try
		{
			File file = FileUtil.isExists(abspath);
			if (file == null)
				return null;

			fis = new FileInputStream(file);
			InputStreamReader inputStrReader = new InputStreamReader(fis, code);
			BufferedReader buffereReader = new BufferedReader(inputStrReader);
			StringBuilder sb = new StringBuilder();
			String line = "";
			
			//构建json格式
			sb.append("[");
			while ((line = buffereReader.readLine()) != null)
			{
				sb.append(line).append("\n");
			}
			sb.append("]");
			buffereReader.close();
			inputStrReader.close();
			
			return sb.toString();
		}
		catch (Exception e1)
		{
			System.out.println("FileUtil read error: " + e1.toString());
		}
		finally
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (Exception e1)
			{
				Logger.e("read(abspath)" + e1.toString());
			}
		}
		return null;
	}
	
	//应用的数据读取拼接
	public String read(final String abspath, final String code)
	{
		FileInputStream fis = null;
		try
		{
			File file = FileUtil.isExists(abspath);
			if (file == null)
				return null;

			fis = new FileInputStream(file);
			InputStreamReader inputStrReader = new InputStreamReader(fis, code);
			BufferedReader buffereReader = new BufferedReader(inputStrReader);
			StringBuilder sb = new StringBuilder();
			String line = "";
			
			//构建json格式
			sb.append("[");
			while ((line = buffereReader.readLine()) != null)
			{
				sb.append(line).append("\n");
			}
			sb.append("]");
			buffereReader.close();
			inputStrReader.close();
			
			return sb.toString();
		}
		catch (Exception e1)
		{
			System.out.println("FileUtil read error: " + e1.toString());
		}
		finally
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (Exception e1)
			{
				Logger.e("read(abspath)" + e1.toString());
			}
		}
		return null;
	}
}
