package com.school.twohand.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class AmoyCircle implements Parcelable{
	private Integer circleId;        	 //淘圈Id（主键）
	private Integer circleUserId;    	 //淘圈创建者Id（圈主Id）
	private String circleName;       	 //淘圈名
	private String circleLabel;      	 //淘圈标签
	private String circleAddress;    	 //淘圈地点
	private Integer circleNumber;   	 //淘圈成员数量
	private Timestamp circleCreateTime;  //淘圈创建时间
	private String circleImageUrl;		 //淘圈头像地址
	private double circleLatitude;    //淘圈地理位置的纬度
	private double circleLongitude;   //淘圈地理位置的经度
	
	public Integer getCircleId() {
		return circleId;
	}
	public void setCircleId(Integer circleId) {
		this.circleId = circleId;
	}
	public Integer getCircleUserId() {
		return circleUserId;
	}
	public void setCircleUserId(Integer circleUserId) {
		this.circleUserId = circleUserId;
	}
	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	public String getCircleLabel() {
		return circleLabel;
	}
	public void setCircleLabel(String circleLabel) {
		this.circleLabel = circleLabel;
	}
	public String getCircleAddress() {
		return circleAddress;
	}
	public void setCircleAddress(String circleAddress) {
		this.circleAddress = circleAddress;
	}
	public Integer getCircleNumber() {
		return circleNumber;
	}
	public void setCircleNumber(Integer circleNumber) {
		this.circleNumber = circleNumber;
	}
	public Timestamp getCircleCreateTime() {
		return circleCreateTime;
	}
	public void setCircleCreateTime(Timestamp circleCreateTime) {
		this.circleCreateTime = circleCreateTime;
	}
	public String getCircleImageUrl() {
		return circleImageUrl;
	}
	public void setCircleImageUrl(String circleImageUrl) {
		this.circleImageUrl = circleImageUrl;
	}

	public double getCircleLatitude() {
		return circleLatitude;
	}

	public void setCircleLatitude(double circleLatitude) {
		this.circleLatitude = circleLatitude;
	}

	public double getCircleLongitude() {
		return circleLongitude;
	}

	public void setCircleLongitude(double circleLongitude) {
		this.circleLongitude = circleLongitude;
	}

	public AmoyCircle() {
		super();
	}

	//构造方法，没有经纬度
	public AmoyCircle(Integer circleId, Integer circleUserId,
			String circleName, String circleLabel, String circleAddress,
			Integer circleNumber, Timestamp circleCreateTime,
			String circleImageUrl) {
		super();
		this.circleId = circleId;
		this.circleUserId = circleUserId;
		this.circleName = circleName;
		this.circleLabel = circleLabel;
		this.circleAddress = circleAddress;
		this.circleNumber = circleNumber;
		this.circleCreateTime = circleCreateTime;
		this.circleImageUrl = circleImageUrl;
	}

	//构造方法，包含所有字段
	public AmoyCircle(Integer circleId, Integer circleUserId, String circleName, String circleLabel, String circleAddress, Integer circleNumber, Timestamp circleCreateTime, String circleImageUrl, double circleLatitude, double circleLongitude) {
		this.circleId = circleId;
		this.circleUserId = circleUserId;
		this.circleName = circleName;
		this.circleLabel = circleLabel;
		this.circleAddress = circleAddress;
		this.circleNumber = circleNumber;
		this.circleCreateTime = circleCreateTime;
		this.circleImageUrl = circleImageUrl;
		this.circleLatitude = circleLatitude;
		this.circleLongitude = circleLongitude;
	}

	//本构造器仅供类的方法createFromParcel调用
	protected AmoyCircle(Parcel in) {
		circleId = in.readInt();
		circleUserId = in.readInt();
		circleName = in.readString();
		circleLabel = in.readString();
		circleAddress = in.readString();
		circleNumber = in.readInt();
		circleImageUrl = in.readString();
	}

	//将对象中的属性保存至目标对象dest中
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(circleId);
		dest.writeInt(circleUserId);
		dest.writeString(circleName);
		dest.writeString(circleLabel);
		dest.writeString(circleAddress);
		dest.writeInt(circleNumber);
		dest.writeString(circleImageUrl);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// 必须要创建一个名叫CREATOR的常量（名字大小写都不能使其他的）
	public static final Creator<AmoyCircle> CREATOR = new Creator<AmoyCircle>() {
		@Override
		public AmoyCircle createFromParcel(Parcel in) {
			return new AmoyCircle(in);
		}

		@Override
		public AmoyCircle[] newArray(int size) {
			return new AmoyCircle[size];
		}
	};

	
}
