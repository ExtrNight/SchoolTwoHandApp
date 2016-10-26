package com.school.twohand.entity;

public class Follow {
	Integer followId;
	User followMe;
	User followOther;
	public Integer getFollowId() {
		return followId;
	}
	public void setFollowId(Integer followId) {
		this.followId = followId;
	}
	public User getFollowMe() {
		return followMe;
	}
	public void setFollowMe(User followMe) {
		this.followMe = followMe;
	}
	public User getFollowOther() {
		return followOther;
	}
	public void setFollowOther(User followOther) {
		this.followOther = followOther;
	}
	public Follow(Integer followId, User followMe, User followOther) {
		super();
		this.followId = followId;
		this.followMe = followMe;
		this.followOther = followOther;
	}
	public Follow() {
		super();
	}
	
	

}
