package cd.s.data;

public class StockCoinsResult extends DataBase
{
	/*##错误码
	##处理下单时. 判断商品是否充足
	3521  商品售完.
	##处理下单时. 判断用户金币和兑换商品的金币比较.
	3522  用户金币不足
	3525  重复提交订单(uuid)*/
	public static final int OUTOFSTOCK = 3521;
	public static final int NOTENOUGHCOINS = 3522;
	public static final int REPOSTORDER = 3525;
	
	public
	ResultMsg
		msgResult;
	
	public 
	int 
		iProductPrice,
		iProductId;
	
	public 
	String
		sProductName,
		sUserId;
	
	public void setResultMsgCode(ResultMsg resultMsg)
	{
		this.msgResult = resultMsg;
	}
}
