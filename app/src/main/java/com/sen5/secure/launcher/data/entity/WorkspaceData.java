package com.sen5.secure.launcher.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkspaceData implements Parcelable {

	/** 自动编号 */
	private String AutoID;
	/** 包名 */
	private String PackageName;
	/** 类名 */
	private String ClassName;
	
//	/** 是否已在OTT中加载 */
//	private String IsAdded;
	/** 添加在自由分组中的标识， 1表示OTT， 2表示Game ... */
	private String IsAdded;
	
	
	/** app在OTT的排列位置 */
	private String AppOrder;
	/** 备注 */
	private String Remark;
	
	public WorkspaceData(){}
	
	public WorkspaceData(String packageName, String className,
                         String isAdded, String appOrder, String remark) {
		super();
		PackageName = packageName;
		ClassName = className;
		IsAdded = isAdded;
		AppOrder = appOrder;
		Remark = remark;
	}

	public WorkspaceData(String autoID, String packageName, String className,
                         String appOrder, String isAdded, String remark) {
		super();
		AutoID = autoID;
		PackageName = packageName;
		ClassName = className;
		IsAdded = isAdded;
		AppOrder = appOrder;
		Remark = remark;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错  
		// 2.序列化对象 
		dest.writeString(AutoID);
		dest.writeString(PackageName);
		dest.writeString(ClassName);
		dest.writeString(IsAdded);
		dest.writeString(AppOrder);
		dest.writeString(Remark);
	}
	
	public static final Creator<WorkspaceData> CREATOR = new Creator<WorkspaceData>() {

		@Override
		public WorkspaceData createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			WorkspaceData workspaceData = new WorkspaceData();
			workspaceData.setAutoID(source.readString());
			workspaceData.setPackageName(source.readString());
			workspaceData.setClassName(source.readString());
			workspaceData.setIsAdded(source.readString());
			workspaceData.setAppOrder(source.readString());
			workspaceData.setRemark(source.readString());
			return workspaceData;
		}

		@Override
		public WorkspaceData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new WorkspaceData[size];
		}
	};

	public String getAutoID() {
		return AutoID;
	}

	public void setAutoID(String autoID) {
		AutoID = autoID;
	}

	public String getPackageName() {
		return PackageName;
	}

	public void setPackageName(String packageName) {
		PackageName = packageName;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public String getIsAdded() {
		return IsAdded;
	}

	public void setIsAdded(String isAdded) {
		IsAdded = isAdded;
	}
	
	public String getAppOrder() {
		return AppOrder;
	}
	
	public void setAppOrder(String appOrder) {
		AppOrder = appOrder;
	}
	
	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}
	
}
