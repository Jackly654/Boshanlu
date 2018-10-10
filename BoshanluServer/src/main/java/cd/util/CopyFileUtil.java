package cd.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import hz.dodo.FileUtil;
import hz.dodo.Logger;
import hz.dodo.data.Empty;

public class CopyFileUtil
{
	public static boolean copyFolderFile(final String oldPath, final String newPath)
	{
		try
		{
			if(!Empty.isEmpty(oldPath) && !Empty.isEmpty(newPath) && !oldPath.equals(newPath))
			{
				if(oldPath.endsWith(File.separator) && newPath.endsWith(File.separator))
				{
					File fileOld;
					if((fileOld = FileUtil.isExists(oldPath)) != null)
					{
						File file = new File(newPath);
						if (!file.exists())
						{
							if(!file.mkdirs()) // 创建多级目录结构
							{
								return false;
							}
						}

						File[] files = fileOld.listFiles();
						if(files != null && files.length > 0)
						{
							File fileTemp;
							int i1 = 0;
							while(i1 < files.length)
							{
								if((fileTemp = files[i1]) != null)
								{
									if(fileTemp.isDirectory())
									{
										if(!copyFolderFile(handlePathEndSeparator(fileTemp.getAbsolutePath()), handlePathEndSeparator(newPath + fileTemp.getName())))
										{
											return false;
										}
									}
									else
									{
										if(!copyFile(fileTemp.getAbsolutePath(), newPath + fileTemp.getName()))
										{
											return false;
										}
									}
								}
								i1 ++;
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.e("Util copyFolderFile error == " + e.toString());
		}
		finally 
		{
		}
		return false;
	}
	
	public static boolean copyFile(final String oldPath, final String newPath)
	{
		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;

		try
		{
			if(!Empty.isEmpty(oldPath) && !Empty.isEmpty(newPath) && !oldPath.equals(newPath))
			{
				File fileOld;
				if((fileOld = FileUtil.isExists(oldPath)) != null)
				{
					int index = newPath.lastIndexOf(File.separator);
					if(index > 0 && index < newPath.length())
					{
						String folderPath = newPath.substring(0, index);
						if(!Empty.isEmpty(folderPath))
						{
							File file = new File(folderPath);
							if (!file.exists())
							{
								if(!file.mkdirs()) // 创建多级目录结构
								{
									return false;
								}
							}

							File filetmp = new File(newPath + ".hdd");
							if(filetmp.exists())
							{
								filetmp.delete();
							}

							if(filetmp.createNewFile())
							{
								fileOutputStream = new FileOutputStream(filetmp, true);
								int reading, writed = 0;
								inputStream = new FileInputStream(oldPath);
								ByteArrayOutputStream babuf = new ByteArrayOutputStream();
								byte[] buf = new byte[5120];  
								while ((reading = inputStream.read(buf)) != -1) 
								{   
									babuf.write(buf, 0, reading);
									writed += reading;
									if(writed >= 65536/*64k*/) // 256 * Dodo.KB = 262144
									{
										// 写文件
										fileOutputStream.write(babuf.toByteArray());
										babuf.reset();
										writed = 0;
									}
								}

								try
								{
									fileOutputStream.write(babuf.toByteArray());
								}
								catch(Exception e1)
								{
									Logger.e("最后一部分不用写=" + e1.toString());
								}

								if(fileOld.length() == filetmp.length())
								{
									file = new File(newPath);
									if(file.exists())
									{
										file.delete();
									}
									return filetmp.renameTo(file);
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.e("Util copyFile error == " + e.toString());
		}
		finally 
		{
			try
			{
				if(inputStream != null)
				{
					inputStream.close();
					inputStream = null;
				}
				
				if(fileOutputStream != null)
				{
					fileOutputStream.close();
					fileOutputStream = null;
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	private static String handlePathEndSeparator(String path)
	{
		if(!Empty.isEmpty(path))
		{
			if(!path.endsWith(File.separator))
			{
				path += File.separator;
			}

			return path;
		}

		return path;
	}
}
