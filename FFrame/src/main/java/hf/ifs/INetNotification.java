package hf.ifs;

public interface INetNotification
{
	// isConnect: 是否联网
	// netType: 网络类型, SIMMng.NET_WIFI / SIMMng.NET_2G / SIMMng.NET_3G / SIMMng.NET_4G
	void onNetChanged(final boolean isConnect, final int netType);
}
