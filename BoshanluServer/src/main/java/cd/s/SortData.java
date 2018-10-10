package cd.s;
import java.util.Comparator;

import cd.s.data.Block;
import cd.s.data.Column;
import cd.s.data.SearchHistory;
/**
 * Created by Jackly on 2017/8/24.
 */

public class SortData
{
	public class SortArticlePosition implements Comparator<Block>
	{
		@Override
		public int compare(Block lhs, Block rhs)
		{
			if(lhs == null || rhs == null) return 0;
			return lhs.iPosition == rhs.iPosition ? 0 : lhs.iPosition > rhs.iPosition ? 1 : -1;
		}
	}
	
	public class SortColumnsPosition implements Comparator<Column>
	{
		@Override
		public int compare(Column lhs, Column rhs)
		{
			if(lhs == null || rhs == null) return 0;
			int cr = 0;
			int a = rhs.iPosition - lhs.iPosition;
			if(a != 0)
			{
				if(a == 8 || a == -8)
				{
					a = lhs.iDisplayOrder - rhs.iDisplayOrder;
					if(a != 0)
					{
						cr = (a > 0) ? 1 : -2;
					}
				}
				else
				{
					cr = (a > 0) ? 2 : -1;
				}
			}
			else
			{
				a = lhs.iDisplayOrder - rhs.iDisplayOrder;
				if(a != 0)
				{
					cr = (a > 0) ? 1 : -2;
				}
			}
			return cr;
		}
	}
	
	public class SortSearchHistory implements Comparator<SearchHistory>
	{
		public int compare(SearchHistory lhs, SearchHistory rhs)
		{
			if(lhs == null || rhs == null) return 0;
			return lhs.time == rhs.time ? 0 : lhs.time > rhs.time ? -1 : 1;
		}
	}
}
