package cd.s.data;

public class Media extends DataBase
{
	public static final String TYPE_VIDEO = "VIDEO";
	public static final String TYPE_AUDIO = "AUDIO";
	
	public static final String ORIENTATION_HORIZONTAL = "h";
	public static final String ORIENTATION_VERTICAL = "v";
	
	public
	String
			sType,
			sUrl,
			sOrientation;
	
	public
	int
		iDuration;//秒
}
