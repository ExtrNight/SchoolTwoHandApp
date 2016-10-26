package com.school.twohand.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class User implements Parcelable {
	Integer userId;
	String userAccount;
	String userName;
	String userPassword;
	String userHead;
	String userSex;
	Timestamp userBirthday;
	String userPersonalProfile;
	Float userBalance;
	Float userAmountEarned;
	String userAddress;
	School userSchool;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserHead() {
		return userHead;
	}
	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}
	public String getUserSex() {
		return userSex;
	}
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	public Timestamp getUserBirthday() {
		return userBirthday;
	}
	public void setUserBirthday(Timestamp userBirthday) {
		this.userBirthday = userBirthday;
	}
	public String getUserPersonalProfile() {
		return userPersonalProfile;
	}
	public void setUserPersonalProfile(String userPersonalProfile) {
		this.userPersonalProfile = userPersonalProfile;
	}
	public Float getUserBalance() {
		return userBalance;
	}
	public void setUserBalance(Float userBalance) {
		this.userBalance = userBalance;
	}
	public Float getUserAmountEarned() {
		return userAmountEarned;
	}
	public void setUserAmountEarned(Float userAmountEarned) {
		this.userAmountEarned = userAmountEarned;
	}
	
	
	public String getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	public School getUserSchool() {
		return userSchool;
	}
	public void setUserSchool(School userSchool) {
		this.userSchool = userSchool;
	}
	public User(Integer userId, String userAccount, String userName,
			String userPassword, String userHead, String userSex,
			Timestamp userBirthday, String userPersonalProfile,
			Float userBalance, Float userAmountEarned, String userAddress,
			School userSchool) {
		super();
		this.userId = userId;
		this.userAccount = userAccount;
		this.userName = userName;
		this.userPassword = userPassword;
		this.userHead = userHead;
		this.userSex = userSex;
		this.userBirthday = userBirthday;
		this.userPersonalProfile = userPersonalProfile;
		this.userBalance = userBalance;
		this.userAmountEarned = userAmountEarned;
		this.userAddress = userAddress;
		this.userSchool = userSchool;
	}
	public User() {
		super();
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.userId);
		dest.writeString(this.userAccount);
		dest.writeString(this.userName);
		dest.writeString(this.userPassword);
		dest.writeString(this.userHead);
		dest.writeString(this.userSex);
		dest.writeSerializable(this.userBirthday);
		dest.writeString(this.userPersonalProfile);
		dest.writeValue(this.userBalance);
		dest.writeValue(this.userAmountEarned);
		dest.writeString(this.userAddress);
		dest.writeParcelable(this.userSchool, flags);
	}

	protected User(Parcel in) {
		this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
		this.userAccount = in.readString();
		this.userName = in.readString();
		this.userPassword = in.readString();
		this.userHead = in.readString();
		this.userSex = in.readString();
		this.userBirthday = (Timestamp) in.readSerializable();
		this.userPersonalProfile = in.readString();
		this.userBalance = (Float) in.readValue(Float.class.getClassLoader());
		this.userAmountEarned = (Float) in.readValue(Float.class.getClassLoader());
		this.userAddress = in.readString();
		this.userSchool = in.readParcelable(School.class.getClassLoader());
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};
}
