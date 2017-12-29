package com.sen5.smartlifebox.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class SceneData implements Parcelable {

	private String icon_path;
	private int scene_id;
	private String scene_name;
	private long update_time;
	private List<SceneActionData> action_list;

	public SceneData() {
		// TODO Auto-generated constructor stub
	}

	protected SceneData(Parcel in) {
		icon_path = in.readString();
		scene_id = in.readInt();
		scene_name = in.readString();
		update_time = in.readLong();
		action_list = in.createTypedArrayList(SceneActionData.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(icon_path);
		dest.writeInt(scene_id);
		dest.writeString(scene_name);
		dest.writeLong(update_time);
		dest.writeTypedList(action_list);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<SceneData> CREATOR = new Creator<SceneData>() {
		@Override
		public SceneData createFromParcel(Parcel in) {
			return new SceneData(in);
		}

		@Override
		public SceneData[] newArray(int size) {
			return new SceneData[size];
		}
	};

	public String getIcon_path() {
		return icon_path;
	}

	public void setIcon_path(String icon_path) {
		this.icon_path = icon_path;
	}

	public int getScene_id() {
		return scene_id;
	}

	public void setScene_id(int scene_id) {
		this.scene_id = scene_id;
	}

	public String getScene_name() {
		return scene_name;
	}

	public void setScene_name(String scene_name) {
		try {
			scene_name = URLDecoder.decode(scene_name,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.scene_name = scene_name;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		this.update_time = update_time;
	}

	public List<SceneActionData> getAction_list() {
		return action_list;
	}

	public void setAction_list(List<SceneActionData> action_list) {
		this.action_list = action_list;
	}

}
