package hf.data;

/**
 *
 * Created by fanjl on 2017-12-26.
 */

public class FSize
{
	private
	static
	FSize
		mThis;

	private
	int
		iWidthWindow, // 程序可用宽度
		iHeightWindow, // 程序可用高度

		iDeadLR, // 左右不可触碰区域
		iTtH, // 标题高度
		iStrokeWidth; // 加粗线条

	public static FSize getInstance()
	{
		if(mThis == null)
		{
			synchronized(FSize.class)
			{
				if(mThis == null)
				{
					mThis = new FSize();
				}
			}
		}
		return mThis;
	}
	private FSize()
	{
	}
	public void setScreenSize(int width, int height)
	{
		iWidthWindow = width;
		iHeightWindow = height;

		iDeadLR = getRelativeWidth(20);
		iTtH = getRelativeHeight(90);
		iStrokeWidth = getRelativeHeight(3);
	}
	public int getDeadLR()
	{
		return iDeadLR;
	}
	public int getTtH()
	{
		return iTtH;
	}
	public int getStrokeWidth()
	{
		return iStrokeWidth;
	}
	public int getWindowWidth()
	{
		return iWidthWindow;
	}
	public int getWindowHeight()
	{
		return iHeightWindow;
	}
	public int getRelativeWidth(int width)
	{
		return iWidthWindow*width/750;
	}
	public int getRelativeHeight(int height)
	{
		return iHeightWindow*height/1334;
	}
}
