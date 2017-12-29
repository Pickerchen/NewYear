package hsl.p2pipcam.listener;

public interface PlayListener
{
	void cameraGetParamsResult(long userid, String cameraParams);
	void callBackAudioData(long userID, byte[] pcm, int size);
	void callBackVideoData(long userID, byte[] data, int type, int size);
	void smartAlarmCodeGetParamsResult(long userid, String params);
	void smartAlarmNotify(long userid,String message);
	//void setCameraParamsResult(long userID, long nType, int nResult);
}
