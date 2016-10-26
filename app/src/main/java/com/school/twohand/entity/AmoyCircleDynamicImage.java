package com.school.twohand.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class AmoyCircleDynamicImage implements Parcelable {

	private int amoyCircleDynamicImageId;    //主键
	private int amoyCircleDynamicId;		 //淘圈动态的Id
	private String circleDynamicImageUrl; //图片地址

	public AmoyCircleDynamicImage(int amoyCircleDynamicImageId, int amoyCircleDynamicId,
								  String circleDynamicImageUrl) {
		super();
		this.amoyCircleDynamicImageId = amoyCircleDynamicImageId;
		this.amoyCircleDynamicId = amoyCircleDynamicId;
		this.circleDynamicImageUrl = circleDynamicImageUrl;
	}
	public int getAmoyCircleDynamicImageId() {
		return amoyCircleDynamicImageId;
	}
	public void setAmoyCircleDynamicImageId(int amoyCircleDynamicImageId) {
		this.amoyCircleDynamicImageId = amoyCircleDynamicImageId;
	}
	public int getAmoyCircleDynamicId() {
		return amoyCircleDynamicId;
	}
	public void setAmoyCircleDynamicId(int amoyCircleDynamicId) {
		this.amoyCircleDynamicId = amoyCircleDynamicId;
	}
	public String getCircleDynamicImageUrl() {
		return circleDynamicImageUrl;
	}
	public void setCircleDynamicImageUrl(String circleDynamicImageUrl) {
		this.circleDynamicImageUrl = circleDynamicImageUrl;
	}
	@Override
	public String toString() {
		return "AmoyCircleDynamicImage [amoyCircleDynamicImageId=" + amoyCircleDynamicImageId + ", amoyCircleDynamicId="
				+ amoyCircleDynamicId + ", circleDynamicImageUrl=" + circleDynamicImageUrl + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.amoyCircleDynamicImageId);
		dest.writeInt(this.amoyCircleDynamicId);
		dest.writeString(this.circleDynamicImageUrl);
	}

	protected AmoyCircleDynamicImage(Parcel in) {
		this.amoyCircleDynamicImageId = in.readInt();
		this.amoyCircleDynamicId = in.readInt();
		this.circleDynamicImageUrl = in.readString();
	}

	public static final Creator<AmoyCircleDynamicImage> CREATOR = new Creator<AmoyCircleDynamicImage>() {
		@Override
		public AmoyCircleDynamicImage createFromParcel(Parcel source) {
			return new AmoyCircleDynamicImage(source);
		}

		@Override
		public AmoyCircleDynamicImage[] newArray(int size) {
			return new AmoyCircleDynamicImage[size];
		}
	};
}
