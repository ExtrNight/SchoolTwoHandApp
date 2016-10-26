package com.school.twohand.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class School implements Parcelable {
	private Integer schoolId;
	private String schoolName;
	public Integer getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public School(Integer schoolId, String schoolName) {
		super();
		this.schoolId = schoolId;
		this.schoolName = schoolName;
	}
	public School() {
		super();
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.schoolId);
		dest.writeString(this.schoolName);
	}

	protected School(Parcel in) {
		this.schoolId = (Integer) in.readValue(Integer.class.getClassLoader());
		this.schoolName = in.readString();
	}

	public static final Parcelable.Creator<School> CREATOR = new Parcelable.Creator<School>() {
		@Override
		public School createFromParcel(Parcel source) {
			return new School(source);
		}

		@Override
		public School[] newArray(int size) {
			return new School[size];
		}
	};
}
