package com.school.twohand.entity;

import java.sql.Timestamp;

/**
 * 淘圈动态的评论
 * @author yang
 */
public class AmoyCircleDynamicComment {
	private int circleDynamicCommentId;
	private int circleDynamicId;          				//所在动态的Id
	private String circleDynamicCommentContent; 			//评论的内容
	private Integer circleDynamicCommentFatherId;       //评论的父评论Id
	private int isEnd;										//是否是最后一个评论
	private User user;										//评论者的对象
	private Timestamp circleDynamicCommentTime;			//评论的时间
	private String fatherCommentUserName;               //父评论评论者的用户名

	//包含所有字段，用于查询
	public AmoyCircleDynamicComment(int circleDynamicCommentId, int circleDynamicId, String circleDynamicCommentContent,
			Integer circleDynamicCommentFatherId, int isEnd, User user, Timestamp circleDynamicCommentTime,String fatherCommentUserName) {
		super();
		this.circleDynamicCommentId = circleDynamicCommentId;
		this.circleDynamicId = circleDynamicId;
		this.circleDynamicCommentContent = circleDynamicCommentContent;
		this.circleDynamicCommentFatherId = circleDynamicCommentFatherId;
		this.isEnd = isEnd;
		this.user = user;
		this.circleDynamicCommentTime = circleDynamicCommentTime;
		this.fatherCommentUserName = fatherCommentUserName;
	}

	//没有评论的Id，用于向数据库添加一条评论,也没有父评论的评论者用户名和isEnd属性和时间（不需要）
	public AmoyCircleDynamicComment(int circleDynamicId, String circleDynamicCommentContent,
									Integer circleDynamicCommentFatherId,User user) {
		super();
		this.circleDynamicId = circleDynamicId;
		this.circleDynamicCommentContent = circleDynamicCommentContent;
		this.circleDynamicCommentFatherId = circleDynamicCommentFatherId;
		this.user = user;
	}

	public int getCircleDynamicCommentId() {
		return circleDynamicCommentId;
	}

	public void setCircleDynamicCommentId(int circleDynamicCommentId) {
		this.circleDynamicCommentId = circleDynamicCommentId;
	}

	public int getCircleDynamicId() {
		return circleDynamicId;
	}

	public void setCircleDynamicId(int circleDynamicId) {
		this.circleDynamicId = circleDynamicId;
	}

	public String getCircleDynamicCommentContent() {
		return circleDynamicCommentContent;
	}

	public void setCircleDynamicCommentContent(String circleDynamicCommentContent) {
		this.circleDynamicCommentContent = circleDynamicCommentContent;
	}

	public Integer getCircleDynamicCommentFatherId() {
		return circleDynamicCommentFatherId;
	}

	public void setCircleDynamicCommentFatherId(Integer circleDynamicCommentFatherId) {
		this.circleDynamicCommentFatherId = circleDynamicCommentFatherId;
	}

	public int getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(int isEnd) {
		this.isEnd = isEnd;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getCircleDynamicCommentTime() {
		return circleDynamicCommentTime;
	}

	public void setCircleDynamicCommentTime(Timestamp circleDynamicCommentTime) {
		this.circleDynamicCommentTime = circleDynamicCommentTime;
	}
	public String getFatherCommentUserName() {
		return fatherCommentUserName;
	}

	public void setFatherCommentUserName(String fatherCommentUserName) {
		this.fatherCommentUserName = fatherCommentUserName;
	}

	@Override
	public String toString() {
		return "AmoyCircleDynamicComment [circleDynamicCommentId=" + circleDynamicCommentId + ", circleDynamicId="
				+ circleDynamicId + ", circleDynamicCommentContent=" + circleDynamicCommentContent
				+ ", circleDynamicCommentFatherId=" + circleDynamicCommentFatherId + ", isEnd=" + isEnd + ", user="
				+ user + ", circleDynamicCommentTime=" + circleDynamicCommentTime + ", fatherCommentUserName="
				+ fatherCommentUserName + "]";
	}
	
	
}
