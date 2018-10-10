package cd.s.Integral;

import hf.http.util.StrUtil;
import hz.dodo.FileUtil;
import hz.dodo.Logger;
import hz.dodo.SPUtil;
import hz.dodo.data.Empty;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import android.content.Context;
import cd.s.Async;
import cd.s.Async.ISyncIntegral;
import cd.s.DataMng;
import cd.s.JsonParser;
import cd.s.Async.IPostIntegral;
import cd.s.PathMng;
import cd.s.data.DR;
import cd.s.data.ECode;
import cd.s.data.IntegralInfo;
import cd.s.data.IntegralResult;
import cd.util.FThreadPoolUtil;
import cd.util.FileValidityUtil;

public class IntegralMng
{
	Context
		ctx;
	DataMng
		dm;
	PathMng
		pathMng;
	FileUtil
		fu;
	public 
	static
	IntegralMng
		mThis;
	int
		iCount;
	boolean
		isUploadSuccess;
	
	public IntegralMng(DataMng dm, Context ctx) 
	{
		this.ctx = ctx;
		this.dm = dm;
		pathMng = PathMng.getInstance(ctx);
		fu = new FileUtil();
	}
	
	public static IntegralMng getInstance(DataMng dm, Context ctx)
	{
		if(mThis == null)
		{
			synchronized (IntegralMng.class)
			{
				if(mThis == null)
				{
					mThis = new IntegralMng(dm, ctx);
				}
			}
		}
		return mThis;
	}
	
	public String getUId()
	{
		return dm != null ? dm.getUId() : null;
	}
	
//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓以下是用户积分代码↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	public void syncLocalIntegral(final String aeskey, final String sha1, final ISyncIntegral iReq)
	{
		try
		{
			final String userId = getUId();
			if (!Empty.isEmpty(userId))
			{
				FThreadPoolUtil.getInstance().executeSingleSyncTask(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(pathMng != null)
						{
							String integralPath = pathMng.getAccountIntegralDir(userId);
							if(!Empty.isEmpty(integralPath))
							{
								File f = new File(integralPath);
								if(f != null && f.exists())
								{
									if(checkFilesCorrect(userId, f))
									{
										iCount = 0;
										isUploadSuccess = false;
										handleIntegralByOrder(aeskey, sha1, userId, new IPostIntegral()
										{
											@Override
											public void onStart(Object tag) 
											{
											}
											@Override
											public void onError(int code, String msg, Object tag) 
											{
												handleSyncResult(iReq);
											}
											@Override
											public void onPostIntegral(IntegralResult interalResult, Object tag) 
											{
												isUploadSuccess = true;
												handleSyncResult(iReq);
											}
										});
									}
									else
									{
										handleFileIllegalModification(userId, f);
										handleIntegralLocalFinish(iReq);
									}
								}
								else
								{
									Logger.i("Integral 用户积分 本地无积分信息");
									handleIntegralLocalFinish(iReq);
								}
							}
							else
							{
								handleIntegralLocalFinish(iReq);
							}
						}
					}
				});
			}
			else
			{
				Logger.i("Integral 用户未登录，不统计积分");
				handleIntegralLocalFinish(iReq);
			}
		}
		catch (Exception ext)
		{
			Logger.e("IntegralMng syncLocalIntegral(): " + ext.toString());
		}
	}
	
	private void handleIntegralLocalFinish(final ISyncIntegral iReq) 
	{
		if(iReq != null)
		{
			iReq.onFinished(false);
		}
	}
	
	private void handleSyncResult(ISyncIntegral iReq)
	{
		++ iCount;
		Logger.i("Integral 本地积分项返回处理个数" + iCount);
		if(iReq != null && iCount == 4)
		{
			iReq.onFinished(isUploadSuccess);
		}
	}
	
	private void handleIntegralByOrder(String aeskey, final String sha1, final String userId, final IPostIntegral iReq)
	{
		postIntegralToServer(aeskey, sha1, userId, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_READ), iReq);
		postIntegralToServer(aeskey, sha1, userId, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_LIKE), iReq);
		postIntegralToServer(aeskey, sha1, userId, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_SHARE), iReq);
		postIntegralToServer(aeskey, sha1, userId, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_AUDIO), iReq);
	}

	@SuppressWarnings("unused")
	private void handleIntegral(String aeskey, String sha1, final String userId)
	{
		try
		{
			if(!Empty.isEmpty(userId))
			{
				HashMap<String, IntegralInfo> hmIntegral = new HashMap<>();
				addToTaskMap(hmIntegral, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_READ));
				addToTaskMap(hmIntegral, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_LIKE));
				addToTaskMap(hmIntegral, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_SHARE));
				addToTaskMap(hmIntegral, handleIntegral(userId, IntegralInfo.INTEGRALTYPE_AUDIO));
				postBatchIntegralToServer(aeskey, sha1, userId, hmIntegral);
			}
		} 
		catch (Exception ext)
		{
			Logger.e("IntegralMng handleIntegral(): " + ext.toString());
		}
	}

	private IntegralInfo handleIntegral(String userId, int integralType)
	{
		return handleIntegral(userId, integralType, true, null);
	}

	private void addToTaskMap(HashMap<String, IntegralInfo> hmIntegral, IntegralInfo integral)
	{
		if(hmIntegral != null && integral != null)
		{
			hmIntegral.put(integral.sUUid, integral);
		}
	}

	private IntegralInfo handleIntegral(String userId, int integralType, boolean isReadOnly, String sArtId) 
	{
		try 
		{
			if(!Empty.isEmpty(userId) && pathMng != null)
			{
				if((FileUtil.isExists(pathMng.getAccountIntegralResultFilePath(userId, integralType))) != null)
				{
					Logger.i("Integral 今日类型为" + integralType + "的积分已完成并同步到服务器");
				}
				else
				{
					String path = pathMng.getAccountIntegralFilePath(userId, integralType);
					boolean completeTask = false;
					if(isReadOnly)
					{
						completeTask = isIntegralCompletedReadOnly(integralType, path, userId);
					}
					else
					{
						completeTask = isInteralCompleted(integralType, path, sArtId, userId);
					}
					if(completeTask)
					{
						Logger.i("Integral 本地完成了 " + integralType + " 的积分缓存");
						IntegralInfo integralinfo = new IntegralInfo();
						integralinfo.sUUid = UUID.randomUUID().toString();
						integralinfo.taskTypeId = integralType;
						return integralinfo;
					}
				}
			}
		}
		catch (Exception ext)
		{
			Logger.e("IntegralMng handleIntegral(): " + ext.toString());
		}
		return null;
	}

	private boolean isInteralCompleted(int integralType, String path, String sArtId, String sUid) 
	{
		try
		{
			if(!Empty.isEmpty(path) && fu != null)
			{
				String content = fu.read(path);
				boolean writeSuccess = false;
				if(Empty.isEmpty(content))
				{
					Logger.i("Integral 本地没有  " + integralType + " 的积分缓存");
					switch(integralType)
					{
						case IntegralInfo.INTEGRALTYPE_READ:
							if(fu.write("" + sArtId, path) == FileUtil.rst_success)
							{
								saveLastModified(sUid);
							}
							return false;
						case IntegralInfo.INTEGRALTYPE_LIKE:	
						case IntegralInfo.INTEGRALTYPE_SHARE:
						case IntegralInfo.INTEGRALTYPE_AUDIO:
							writeSuccess = fu.write("1", path) == FileUtil.rst_success;
							if(writeSuccess)
							{
								saveLastModified(sUid);
							}
							return writeSuccess && integralType != IntegralInfo.INTEGRALTYPE_LIKE;
					}
				}
				else
				{
					Logger.i("Integral 本地存在  " + integralType + " 的积分缓存, 本地保存的字符串 ： " + content);
					switch(integralType)
					{
						case IntegralInfo.INTEGRALTYPE_READ:
							String[] sArr = content.split("#");
							if(!checkReaded(sArr, sArtId))
							{
								writeSuccess = fu.write(content.trim() + "#" + sArtId,  path) == FileUtil.rst_success;
							}
							if(writeSuccess)
							{
								saveLastModified(sUid);
							}
							if(!Empty.isEmpty(sArr))
							{
								return sArr.length >= 5 || (sArr.length == 4 && (writeSuccess));
							}
							return false;
						case IntegralInfo.INTEGRALTYPE_LIKE:
						case IntegralInfo.INTEGRALTYPE_SHARE:
						case IntegralInfo.INTEGRALTYPE_AUDIO:
							int iCount = 0;
							try 
							{
								iCount = Integer.valueOf(content.trim());
							}
							catch(NumberFormatException e) 
							{
								handleNumberFormatException(path, sUid);
							}
							++ iCount;
							writeSuccess = fu.write("" + iCount,  path) == FileUtil.rst_success;
							if(writeSuccess)
							{
								Logger.i("Integral 本地写入  " + integralType + " 的积分缓存 共" + iCount + "篇");
								saveLastModified(sUid);
							}
							int targetNum = integralType == IntegralInfo.INTEGRALTYPE_LIKE ? 5 : 1;
							return iCount > targetNum || (iCount == targetNum && writeSuccess);
					}
				}
			}
		}
		catch (Exception ext)
		{
			Logger.e("IntegralMng isInteralCompleted(): " + ext.toString());
		}
		return false;
	}
	//积分文件夹被非法修改处理
	private void handleFileIllegalModification(String sUid, File f)
	{
		if(f != null && f.exists())
		{
			if(FileUtil.delete(f) == FileUtil.rst_success)
			{
				saveLastModified(sUid);
			}
		}
	}
	
	private boolean isIntegralCompletedReadOnly(int integralType, String path, String sUid) 
	{
		try 
		{
			if(!Empty.isEmpty(path) && (FileUtil.isExists(path)) != null && fu != null)
			{
				String content = fu.read(path);
				if(!Empty.isEmpty(content))
				{
					Logger.i("Integral 打开客户端或者网络接通 检测到本地存在  " + integralType + " 的积分缓存");
					switch(integralType)
					{
						case IntegralInfo.INTEGRALTYPE_READ:
							String[] sArr = content.split("#");
							if(!Empty.isEmpty(sArr) && sArr.length >= 5)
							{
								return true;
							}
							break;
						case IntegralInfo.INTEGRALTYPE_LIKE:	
						case IntegralInfo.INTEGRALTYPE_SHARE:
						case IntegralInfo.INTEGRALTYPE_AUDIO:
							int iCount = 0;
							try 
							{
								iCount = Integer.valueOf(content.trim());
							}
							catch(NumberFormatException e) 
							{
								handleNumberFormatException(path, sUid);
							}
							int targetNum = integralType == IntegralInfo.INTEGRALTYPE_LIKE ? 5 : 1;
							if(iCount >= targetNum)
							{
								return true;
							}
							break;
					}
				}
			}
		} 
		catch (Exception ext) 
		{
			Logger.e("IntegralMng isIntegralCompletedReadOnly(): " + ext.toString());
		}
		return false;
	}

	private void handleNumberFormatException(String path, String sUid) 
	{
		if(!Empty.isEmpty(path))
		{
			if(FileUtil.delete(new File(path)) == FileUtil.rst_success)
			{
				saveLastModified(sUid);
			}
		}
	}
	
	private boolean checkReaded(String[] sArr, String sArtId)
	{
		if(!Empty.isEmpty(sArr) && !Empty.isEmpty(sArtId))
		{
			int i1 = 0;
			String sTemp;
			while(i1 < sArr.length)
			{
				sTemp = sArr[i1];
				if(!Empty.isEmpty(sTemp))
				{
					if(sArtId.equals(sTemp.trim()))
					{
						Logger.i("Integral 该文章已经被阅读过文章id: " + sArtId + "，文章数量: " + sArr.length + "保持不变");
						return true;
					}
				}
				++ i1;
			}
		}
		return false;
	}
	//修改本地积分文件夹操作修改时间
	private void saveLastModified(String sUid) 
	{
		if(!Empty.isEmpty(sUid) && pathMng != null)
		{
			String sFileMD5 = getFileMD5(pathMng.getAccountIntegralDir(sUid));
			if(!Empty.isEmpty(sFileMD5))
			{
				SPUtil.saveString(ctx, DR.SP_KEY_INTEGRAL_FILEMD5, sUid, sFileMD5);
			}
			File f = new File(pathMng.getAccountIntegralDir(sUid));
			if(f != null && f.exists())
			{
				Logger.i("Integral 客户端操作了积分文件夹，最后操作时间  " + f.lastModified());
			}
			SPUtil.saveLong(ctx, DR.SP_KEY_INTEGRAL_TIME, sUid, (f != null && f.exists() ? f.lastModified() : 0));
		}
	}
	
	private String getFileMD5(String path)
	{
		String sFileMD5 = "";
		try 
		{
			if(!Empty.isEmpty(path))
			{
				File f = new File(path);
				if(f != null)
				{
					if(f.isDirectory())
					{
						File[] ltFiles = f.listFiles();
						int i1 = 0;
						File subFile;
						while(i1 < ltFiles.length)
						{
							subFile = ltFiles[i1];
							if(subFile != null)
							{
								sFileMD5 = StrUtil.getMD5(sFileMD5 + getFileMD5(subFile.getAbsolutePath()));
							}
							++ i1;
						}
					}
					else if(f.isFile())
					{
						sFileMD5 = StrUtil.getMD5(sFileMD5 + f.getName() + FileValidityUtil.getHash(f.getAbsolutePath()));
					}
				}
			}
		} 
		catch (Exception ext) 
		{
			Logger.e("IntegerMng getFileMD5(): " + ext.toString());
		}
		return sFileMD5;
	}
	
	public void addIntegral(String aeskey, String sha1, int integralType, String sArtId, Async.IPostIntegral iReq)
	{
		String uid = getUId();
		if(!Empty.isEmpty(uid) && pathMng != null)
		{
			String path = pathMng.getAccountIntegralDir(uid);
			if(!Empty.isEmpty(path))
			{
				File f = new File(path);
				if(f != null && f.exists())
				{
					if(!checkFilesCorrect(uid, f))
					{
						handleFileIllegalModification(uid, f);
					}
				}
			}
			postIntegralToServer(aeskey, sha1, uid, handleIntegral(uid, integralType, false, sArtId), iReq);
		}
		else
		{
			Logger.i("Integral 用户未登录， 不需要统计用户积分 ");
		}
	}
	
	//批量同步
	private void postBatchIntegralToServer(String aeskey, String sha1, final String sUid, final HashMap<String, IntegralInfo> hmIntegral) 
	{
		try 
		{
			if(!Empty.isEmpty(sUid) && !Empty.isEmpty(hmIntegral))
			{
				Logger.i("Integral 真正执行用户积分同步到服务器， 一共 " + hmIntegral.size() + "个积分项");
				postIntegralToServer(aeskey, sha1, sUid, new IPostIntegral()
				{
					@Override
					public void onStart(Object tag) 
					{	
					}
					@Override
					public void onError(int code, String msg, Object tag)
					{		
						Logger.i("Integral 积分上传onError " + msg);
					}
					@Override
					public void onPostIntegral(IntegralResult integralResult, Object tag)
					{
						Logger.i("Integral 积分上传成功接口返回");
						if(integralResult != null && pathMng != null)
						{
							List<String> successIds = integralResult.getSuccessIds();
							if(!Empty.isEmpty(successIds))
							{
								int i1 = 0;
								String sId;
								IntegralInfo info;
								while(i1 < successIds.size())
								{
									sId = successIds.get(i1);
									if(!Empty.isEmpty(sId) && (info = hmIntegral.get(sId)) != null)
									{
										if(fu != null && fu.write("", pathMng.getAccountIntegralResultFilePath(sUid, info.taskTypeId)) == FileUtil.rst_success)
										{
											Logger.i("Integral 今天已经完成积分是" + info.taskTypeId + "的任务并已在本地标记上传成功");
											saveLastModified(sUid);
										}
									}
									++ i1;
								}
							}
						}
					}
				}, JsonParser.putIntegralInfo(hmIntegral, sUid));
			}
			else
			{
				if(Empty.isEmpty(sUid))
				{
					Logger.i("Integral 用户未登录，不统计积分");
				}
				else
				{
					Logger.i("Integral 本地不存在需要同步的积分");
				}
			}
		}
		catch (Exception ext)
		{
			Logger.e("IntegralMng postBatchIntegralToServer(): " + ext.toString());
		}	
	}
	//单篇
	private void postIntegralToServer(String aeskey, String sha1, final String sUid, final IntegralInfo integral, final IPostIntegral iReq) 
	{
		try 
		{
			if(!Empty.isEmpty(sUid) && integral != null)
			{
				Logger.i("Integral 真正执行用户积分同步到服务器, 同步类型： " + integral.taskTypeId);
				postIntegralToServer(aeskey, sha1, sUid, new IPostIntegral()
				{
					@Override
					public void onStart(Object tag) 
					{	
						if(iReq != null)
						{
							iReq.onStart(tag);
						}
					}
					@Override
					public void onError(int code, String msg, Object tag)
					{		
						if(iReq != null)
						{
							iReq.onError(code, msg, tag);
						}
						Logger.i("Integral 积分上传onError " + msg);
					}
					@Override
					public void onPostIntegral(IntegralResult integralResult, Object tag)
					{
						if(iReq != null)
						{
							iReq.onPostIntegral(integralResult, tag);
						}
						Logger.i("Integral 积分上传成功接口返回");
						if(integralResult != null && pathMng != null)
						{
							List<String> successIds = integralResult.getSuccessIds();
							if(!Empty.isEmpty(successIds))
							{
								int i1 = 0;
								String sId;
								while(i1 < successIds.size())
								{
									sId = successIds.get(i1);
									if(!Empty.isEmpty(sId) && (sId.equals(integral.sUUid)))
									{
										if(fu != null && fu.write("", pathMng.getAccountIntegralResultFilePath(sUid, integral.taskTypeId)) == FileUtil.rst_success)
										{
											Logger.i("Integral 今天已经完成积分是" + integral.taskTypeId + "的任务并已在本地标记上传成功");
											saveLastModified(sUid);
										}
									}
									++ i1;
								}
							}
						}
					}
				}, JsonParser.putIntegralInfo(integral, sUid));
			}
			else
			{
				String sErr = "本地不存在需要同步的积分";
				if(Empty.isEmpty(sUid))
				{
					sErr = "userId错误";
					Logger.i("Integral 用户未登录，不统计积分");
				}
				if(dm != null)
				{
					dm.onCheckFail(iReq, ECode.E_PARAME, sErr);
				}
			}
		}
		catch (Exception ext)
		{
			Logger.e("IntegralMng postIntegralToServer(): " + ext.toString());
		}	
	}
	
	
	private void postIntegralToServer(String aeskey, String sha1, String sUid, IPostIntegral iReq, JSONArray jsonIntegral)
	{
		if(dm != null)
		{
			dm.postIntegralToServer(aeskey, sha1, sUid, iReq, jsonIntegral);
		}
	}

	//校验本地文件夹是否有更改
	private boolean checkFilesCorrect(final String userId, File f)
	{
		boolean filesCorrect = true;
		try 
		{
			if(f != null && f.exists())
			{
				long lRecord = SPUtil.getLong(ctx, DR.SP_KEY_INTEGRAL_TIME, userId, 0);
				Logger.i("Integral 用户积分 保存在本地的最后修改时间: " + lRecord);
				long lCurrent = System.currentTimeMillis();
				String timeRecord = hz.dodo.StrUtil.formatTime1(lRecord);
				String timeCurrent = hz.dodo.StrUtil.formatTime1(lCurrent);
				if(!Empty.isEmpty(timeRecord) && !timeRecord.equals(timeCurrent))
				{
					Logger.i("Integral 本地的缓存不是当天记录，无效,需要清空本地积分记录");
					filesCorrect = false;
				}
			}
			if(filesCorrect)
			{
				String sRecord = SPUtil.getString(ctx, DR.SP_KEY_INTEGRAL_FILEMD5, userId, "");
				if(Empty.isEmpty(sRecord))
				{
					Logger.i("Integral sp中记录文件夹MD5值为空 本地不存在积分缓存");
					filesCorrect = false;
				}
				else
				{
					if(!sRecord.equals(getFileMD5(pathMng.getAccountIntegralDir(userId))))
					{
						Logger.i("Integral 该文件夹被其他程序或人为手动修改过");
						filesCorrect = false;
					}					
				}
			}
		} 
		catch (Exception ext) 
		{
			Logger.e("IntegralMng checkFilesCorrect(): " + ext.toString());
		}
		return filesCorrect;
	}
//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑以上是用户积分代码↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
}
