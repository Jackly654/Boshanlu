package cd.s.data;

import java.util.List;

// 阅读趋势/阅读量
public class Behavior
{
	List<Radar>
		ltRadar;
	List<Coordinate>
		ltCoordinate;
	
	public void setRadar(List<Radar> lt)
	{
		ltRadar = lt;
	}
	public List<Radar> getRadar()
	{
		return ltRadar;
	}
	public void setCoordinate(List<Coordinate> lt)
	{
		ltCoordinate = lt;
	}
	public List<Coordinate> getCoordinate()
	{
		return ltCoordinate;
	}
}
