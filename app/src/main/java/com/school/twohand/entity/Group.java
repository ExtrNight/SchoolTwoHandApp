package com.school.twohand.entity;

public class Group {
	String groupMainUserId;
	String groupNumber;
	public String getGroupMainUserId() {
		return groupMainUserId;
	}
	public void setGroupMainUserId(String groupMainUserId) {
		this.groupMainUserId = groupMainUserId;
	}
	public String getGroupNumber() {
		return groupNumber;
	}
	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}
	public Group(String groupMainUserId, String groupNumber) {
		super();
		this.groupMainUserId = groupMainUserId;
		this.groupNumber = groupNumber;
	}
	public Group() {
		super();
	}
	
	
}
