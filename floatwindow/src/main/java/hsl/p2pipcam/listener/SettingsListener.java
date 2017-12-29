package hsl.p2pipcam.listener;

public interface SettingsListener 
{
	void callBack_getParam(long UserID, long nType, String param);
	void callBack_setParam(long UserID, long nType, int nResult);
	void recordFileList(long UserID, int filecount, String fname, String strDate, int size);
}
