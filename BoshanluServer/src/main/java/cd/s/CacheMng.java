package cd.s;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cd.s.SortData.SortSearchHistory;
import cd.s.data.SearchHistory;


import hf.http.util.Empty;
import hf.http.util.IO;
import hz.dodo.FileUtil;
import hz.dodo.Logger;
import hz.dodo.data.AESJava;
import android.content.Context;

public class CacheMng
{
	public final int SEARCHHISTORY_SIZE = 10;
	Context
		ctx;
	AESJava
		aesJava;
	HashMap<String, String>
		hmAccountDir;

	public CacheMng(Context ctx, AESJava aesJava)
	{
		this.ctx = ctx;
		this.aesJava = aesJava;
		hmAccountDir = new HashMap<>(2);
	}
	public boolean writeAccount(String uId, String value)
	{
		return write(getUserInfoFilePath(uId), value);
	}
	public String readAccount(String uId)
	{
		return read(getUserInfoFilePath(uId));
	}
	public boolean writeAddress(String uId, String value)
	{
		return write(getAddressFilePath(uId), value);
	}
	public String readAddress(String uId)
	{
		return read(getAddressFilePath(uId));
	}
	private boolean write(String path, String value)
	{
		try
		{
			if(!Empty.isEmpty(path) && !Empty.isEmpty(value))
			{
				return IO.write(path, aesJava != null ? aesJava.encrypt(value) : value);
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			Logger.e("CacheMng write " + exc.toString());
		}
		return false;
	}
	private String read(String path)
	{
		try
		{
			if(!Empty.isEmpty(path))
			{
				String value = IO.read(path);
				if(!Empty.isEmpty(value))
				{
					value = value.trim();
					return aesJava != null ? aesJava.decrypt(value) : value;
				}
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			Logger.e("CacheMng read " + exc.toString());
		}
		return null;
	}
	private String getAccountDir(String uId)
	{
		if(!Empty.isEmpty(uId))
		{
			try
			{
				String dir = hmAccountDir.get(uId);
				if(Empty.isEmpty(dir))
				{
					dir = PathMng.getInstance(ctx).getAccountDir() + uId + PathMng.SEPARATOR;
					hmAccountDir.put(uId, dir);
				}
				return dir;
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
				Logger.e("" + exc.toString());
			}
		}
		return null;
	}
	private String getUserInfoFilePath(String uId)
	{
		String dir = getAccountDir(uId);
		return !Empty.isEmpty(dir) ? (dir + uId) : null;
	}
	public String getAddressFilePath(String uId)
	{
		String dir = getAccountDir(uId);
		return !Empty.isEmpty(dir) ? (dir + "ads") : null;
	}
	
	public List<SearchHistory> getSearchHistory(final String path)
	{
		try
		{
			if(!Empty.isEmpty(path))
			{
				if(FileUtil.isExists(path) != null)
				{
					FileUtil fileUtil = new FileUtil();
					
					return JsonParser.getSearchHistory(fileUtil.read(path));
				}
			}
		}
		catch (Exception e)
		{
			Logger.e("CacheManager getSearchHistory error == " + e.toString());
		}
		
		return null;
	}
	
	public boolean saveSearchHistory(final String key, final String path)
	{
		if(Empty.isEmpty(path) || Empty.isEmpty(key)) return false;
		
		try
		{
			List<SearchHistory> list = getSearchHistory(path);
			
			SearchHistory searchHistory = getSearchHistoryByKey(key, list);
			
			if(searchHistory != null)
			{
				searchHistory.count ++;
				searchHistory.time = System.currentTimeMillis();
			}
			else
			{
				if(list == null)
				{
					list = new ArrayList<>(1);
				}
				
				searchHistory = new SearchHistory();
				searchHistory.id = System.currentTimeMillis();
				searchHistory.msg = key;
				searchHistory.time = System.currentTimeMillis();
				searchHistory.count = 1;
				
				list.add(searchHistory);
			}
			
			SortData sortData = new SortData();
			
			SortSearchHistory sortSearchHistory = sortData.new SortSearchHistory();
			Collections.sort(list, sortSearchHistory);
			
			while(list.size() > SEARCHHISTORY_SIZE)
			{
				list.remove(list.size() - 1);
			}
			
			String msg = JsonParser.putSearchHistory(list);
			
			if(!Empty.isEmpty(msg))
			{
				FileUtil fileUtil = new FileUtil();
				
				return fileUtil.write(msg, path) == FileUtil.rst_success;
			}
		}
		catch (Exception e)
		{
			Logger.e("CacheManager saveSearchHistory error == " + e.toString());
		}
		
		return false;
	}
	
	private SearchHistory getSearchHistoryByKey(final String key, final List<SearchHistory> list)
	{
		if(Empty.isEmpty(key) || Empty.isEmpty(list)) return null;
		
		SearchHistory searchHistory;
		
		int i1 = 0;
		
		while(i1 < list.size())
		{
			if((searchHistory = list.get(i1)) != null)
			{
				if(key.equals(searchHistory.msg))
				{
					return searchHistory;
				}
			}
			i1 ++;
		}
		
		return null;
	}
	
	public boolean removeSearchHistory(final SearchHistory searchHistory, final String path)
	{
		if(Empty.isEmpty(path) || searchHistory == null || Empty.isEmpty(searchHistory.msg)) return false;
		
		List<SearchHistory> list = getSearchHistory(path);
		
		if(!Empty.isEmpty(list))
		{
			boolean bDeleteSuccess = false;
			SearchHistory history;
			int i1 = 0;
			while(i1 < list.size())
			{
				if((history = list.get(i1)) != null)
				{
					if(history.id == searchHistory.id)
					{
						bDeleteSuccess = list.remove(history);
						break;
					}
				}
				i1 ++;
			}
			
			if(bDeleteSuccess)
			{
				if(list.size() == 0)
				{
					return removeAllSearchHistory(path);
				}
				else
				{
					String msg = JsonParser.putSearchHistory(list);
					
					if(!Empty.isEmpty(msg))
					{
						FileUtil fileUtil = new FileUtil();
						
						return fileUtil.write(msg, path) == FileUtil.rst_success;
					}
				}
			}
			
		}
		
		return false;
	}
	
	public boolean removeAllSearchHistory(final String path)
	{
		if(Empty.isEmpty(path)) return true;
		
		return FileUtil.delete(new File(path)) == FileUtil.rst_success;
	}
}
