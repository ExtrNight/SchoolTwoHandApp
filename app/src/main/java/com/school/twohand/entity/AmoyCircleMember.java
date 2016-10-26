package com.school.twohand.entity;

import java.sql.Timestamp;


public class AmoyCircleMember {
	Integer circleMemberId;
	User circleUser;
	AmoyCircle circle;
	Timestamp circleAddTime;
	public Integer getCircleMemberId() {
		return circleMemberId;
	}
	public void setCircleMemberId(Integer circleMemberId) {
		this.circleMemberId = circleMemberId;
	}
	public User getCircleUser() {
		return circleUser;
	}
	public void setCircleUser(User circleUser) {
		this.circleUser = circleUser;
	}
	
	public AmoyCircle getCircle() {
		return circle;
	}
	public void setCircle(AmoyCircle circle) {
		this.circle = circle;
	}
	public Timestamp getCircleAddTime() {
		return circleAddTime;
	}
	public void setCircleAddTime(Timestamp circleAddTime) {
		this.circleAddTime = circleAddTime;
	}
	public AmoyCircleMember(Integer circleMemberId, User circleUser,
			AmoyCircle circle, Timestamp circleAddTime) {
		super();
		this.circleMemberId = circleMemberId;
		this.circleUser = circleUser;
		this.circle = circle;
		this.circleAddTime = circleAddTime;
	}
	public AmoyCircleMember() {
		super();
	}

}
