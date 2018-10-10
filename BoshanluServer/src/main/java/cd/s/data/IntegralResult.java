package cd.s.data;

import hz.dodo.data.Empty;
import java.util.ArrayList;
import java.util.List;

public class IntegralResult 
{
	private
	ResultMsg
		resultMsg;
	private
	List<String>
		ltSuccessIds,
		ltFailIds;
	
	public void setResultMsg(final ResultMsg resultMsg)
	{
		this.resultMsg = resultMsg;
	}
	
	public ResultMsg getResultMsg()
	{
		return resultMsg;
	}
	
	public void setSuccessIds(final List<String> ltSuccessIds)
	{
		this.ltSuccessIds = ltSuccessIds;
	}
	
	public void addSuccessId(final String successId)
	{
		if(!Empty.isEmpty(successId))
		{
			if(ltSuccessIds == null)
			{
				ltSuccessIds = new ArrayList<>();
			}
			this.ltSuccessIds.add(successId);
		}
	}
	
	public List<String> getSuccessIds()
	{
		return ltSuccessIds;
	}
	
	public void setFailIds(final List<String> ltFailIds)
	{
		this.ltFailIds = ltFailIds;
	}
	
	public List<String> getFailIds()
	{
		return ltFailIds;
	}
}
