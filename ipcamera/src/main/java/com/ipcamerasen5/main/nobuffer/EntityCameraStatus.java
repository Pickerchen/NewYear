package com.ipcamerasen5.main.nobuffer;

import android.text.TextUtils;

public class EntityCameraStatus {
	private String did;
	private int status = -1;
	private long userID = -1;

	public  String getDbFieldMode() {
		return DB_FIELD_MODE;
	}

	public  void setDbFieldMode(String dbFieldMode) {
		DB_FIELD_MODE = dbFieldMode;
	}

	private String DB_FIELD_MODE;
	
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getUserID() {
		return userID;
	}
	public void setUserID(long userID) {
		this.userID = userID;
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o != null && o instanceof EntityCameraStatus){
			EntityCameraStatus entityCameraStatus = (EntityCameraStatus)o;
			if(!TextUtils.isEmpty(entityCameraStatus.getDid())
					&& entityCameraStatus.getDid().equals(this.getDid())){
				return true;
			}
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return super.toString();
		return "---toString  did = " + did + "  status = " + status + "  userID = " + userID;
	}
}
