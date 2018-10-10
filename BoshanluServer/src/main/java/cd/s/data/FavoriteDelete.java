package cd.s.data;

import java.util.List;

public class FavoriteDelete
{
	public
	String
		sUid;
	private
	ResultMsg
		resultMsg;
	private
	List<String>
		ltFavoriteDeleteIds,
		ltFavoriteUndeleteIds;
	
	public void setResultMsg(final ResultMsg resultMsg)
	{
		this.resultMsg = resultMsg;
	}
	
	public ResultMsg getResultMsg()
	{
		return resultMsg;
	}
	
	public void setFavoriteDeleteIds(final List<String> ltFavDeleteIds)
	{
		this.ltFavoriteDeleteIds = ltFavDeleteIds;
	}
	
	public List<String> getFavoriteDeleteIds()
	{
		return ltFavoriteDeleteIds;
	}
	
	public void setFavoriteUndeleteIds(final List<String> ltFavUndeleteIds)
	{
		this.ltFavoriteUndeleteIds = ltFavUndeleteIds;
	}
	
	public List<String> getFavoriteUndeleteIds()
	{
		return ltFavoriteUndeleteIds;
	}
}
