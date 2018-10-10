package cd.s.data;

import java.util.ArrayList;
import java.util.List;

public class Ad extends DataBase
{
	public class AdItem extends DataBase
	{
		public
		String
			sTitle,
			sFileUrl,
			sActionUrl,
			sFilePath;
		
		public
		int
			iId,
			iMediaType,
			iWidth,
			iHeight;
		
		public
		long
			lDuration;
	}
	
	List<AdItem>
		ltAdItem;
	
	public void addAdItem(final AdItem adItem)
	{
		if(adItem != null)
		{
			if(ltAdItem == null)
			{
				ltAdItem = new ArrayList<>();
			}
			
			ltAdItem.add(adItem);
		}
	}
	
	public List<AdItem> getAdItemList()
	{
		return ltAdItem;
	}
}
