package cd.s.data;

public class Order extends DataBase
{
	public 
	Product
		product;
	
	public 
	ResultMsg
		msgResult;
	
	public void setResultMsgCode(ResultMsg resultMsg)
	{
		this.msgResult = resultMsg;
	}
	
	public void setProduct(Product product)
	{
		this.product = product;
	}
}
