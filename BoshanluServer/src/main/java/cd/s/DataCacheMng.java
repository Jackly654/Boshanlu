package cd.s;

import java.io.File;
import cd.s.data.MsgNotification;
import cd.util.MUtil;
import hf.http.util.Empty;
import hf.http.util.IO;
import hz.dodo.FileUtil;
import android.content.Context;

public class DataCacheMng
{
	private
	static
	DataCacheMng
		mThis;
	
	Context
		ctx;
	
	private
	PathMng 
		pathMng;
	
	MUtil
	mUtil;
	
	public static DataCacheMng getInstance(Context ctx)
	{
		if(mThis == null)
		{
			synchronized(DataCacheMng.class)
			{
				if(mThis == null)
				{
					mThis = new DataCacheMng(ctx);
				}
			}
		}
		return mThis;
	}
	
	private DataCacheMng(Context ctx)
	{
		this.ctx = ctx;
		pathMng = PathMng.getInstance(ctx);
		mUtil = MUtil.getInstance();
	}
	
	
	//保存文件修改时间
	public void saveFileModifyTime(final String uid)
	{
		if(!Empty.isEmpty(uid))
		{
			//文件最后修改时间存放路径
			String lastModifyPath = pathMng.getNotifiyLastTime(uid);
			if(!Empty.isEmpty(lastModifyPath))
			{
				//获取最后修改时间并写入存放路径
				File dataFile = new File(pathMng.getNotifyRootDir(uid));
				IO.write(lastModifyPath, dataFile.lastModified() + "");
			}
		}
	}
	//保存responseTime
	public void saveResponseTime(final String uid, MsgNotification msgNotification)
	{
		if(!Empty.isEmpty(uid) && msgNotification != null)
		{
			IO.write(pathMng.getNotifiyResponseTimeDir(uid), msgNotification.sResponseTime + "");	
		}
	}
	
	//保存消息通知的json串
	public void saveNotificationJson(final String value, final String uid)
	{
		if(!Empty.isEmpty(value))
		{
			IO.write(pathMng.getMsgNotifiyJsonDir(uid), value + "");	
		}
	}

	//删除并创建所有文件
	public void createAllService(String uid, MsgNotification msgNotification)
	{
		//TODO 删除根目录文件夹，2个时间删掉
		if(!Empty.isEmpty(uid) && msgNotification != null)
		{

			String responseTime = pathMng.getNotifiyResponseTimeDir(uid);
			String lastModifyPath = pathMng.getNotifiyLastTime(uid);
			

			if(!Empty.isEmpty(responseTime))
			{
				FileUtil.delete(FileUtil.isExists(responseTime));
			}
			if(!Empty.isEmpty(lastModifyPath))
			{
				FileUtil.delete(FileUtil.isExists(lastModifyPath));
			}
		}
	}

}
