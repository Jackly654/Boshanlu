package hf.bean;

import java.util.UUID;

/**
 * Created by fanjl on 2017-12-7.
 */

public class TipConfig
{
	public
	int
		iBmResId; // 图片id

	public
	boolean
		canBack;

	public
	String
		sMsg,
		sTag; // 全局唯一

	public TipConfig()
	{
		iBmResId = 0;
		canBack = true;
		sMsg = "";
		sTag = UUID.randomUUID().toString();
	}
}
