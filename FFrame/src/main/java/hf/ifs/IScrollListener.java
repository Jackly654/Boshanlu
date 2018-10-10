package hf.ifs;

public interface IScrollListener
{
	void onScrollChanged(float seatBottom, int offsetY);
	void onScrollStateChanged(int newState);
}