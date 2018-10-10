package hf.bean;

import hf.data.FrameCLR;
/**
 * Created by fanjl on 2017-12-7.
 */

public class TipConfigDialog extends TipConfig
{
	public
	String
		sTitle,
		sMsg,
		sBtnNegative, // 取消
		sBtnPositive; // 确定
	public
	int
		iClrNeg, // 取消按钮颜色
		iClrPos; // 确认按钮颜色

	public TipConfigDialog()
	{
		super();
		sTitle = "Title";
		sMsg = "Content";
		sBtnNegative = "Cancel";
		sBtnPositive = "Confirm";

		iClrNeg = FrameCLR.RED;
		iClrPos = FrameCLR.BLUE;
	}
}
