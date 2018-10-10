package cd.s.data;

import java.util.List;

public class Column extends DataBase
{
	/**
	 * uuid : 59476c6b82ce8b8340b7e1b2
	 * updated : 2017-07-21T09:39:36.000+0000
	 * name : 测试栏目
	 * comment : 
	 * parentId : 
	 * displayOrder : 1
	 * children : []
	 * dirname : jinji
	 * seoTitle : 
	 * thumbnail3Url : 
	 * thumbnail4Url : 
	 * alias : 
	 * position : 0
	 * seoKeywords : 
	 * seoDescription : 
	 * thumbnail1Url : 
	 * thumbnail2Url : 
	 * treeOrder : 001
	 * type : 0
	 */

	//栏目位置 [0]普通，[1]固定，[2]可选，[9]隐藏
	public static final int COLUMN_NORMAL = 0;// 普通
	public static final int COLUMN_FIXED = 1;//固定
	public static final int COLUMN_CHOOSE = 2;// 可选
	public static final int COLUMN_HIDE = 9;// 隐藏
	
	public
	String
		sUuid,
		sUpdated,
		sName,
		sComment,
		sParentId,
		sDirname,
		sSeoTitle,
		sThumbnail3Url,
		sThumbnail4Url,
		sAlias,
		sSeoKeywords,
		sSeoDescription,
		sThumbnail1Url,
		sThumbnail2Url,
		sTreeOrder;
	
	public 
	int 
		iDisplayOrder,
		iPosition,
		iType;
	
	private List<Column> children;

	public List<Column> getChildren()
	{
		return children;
	}

	public void setChildren(List<Column> children)
	{
		this.children = children;
	}
}
