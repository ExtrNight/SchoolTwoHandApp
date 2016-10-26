package com.school.twohand.entity;

import java.sql.Timestamp;

public class MessageBoard {
	Integer messageBoardId;
	Goods messageBoardGoods;
	User messageBoardUserMe;
	User messageBoardUserOther;
	String messageBoardContent;
	Timestamp messageBoardTime;
	public Integer getMessageBoardId() {
		return messageBoardId;
	}
	public void setMessageBoardId(Integer messageBoardId) {
		this.messageBoardId = messageBoardId;
	}
	public Goods getMessageBoardGoods() {
		return messageBoardGoods;
	}
	public void setMessageBoardGoods(Goods messageBoardGoods) {
		this.messageBoardGoods = messageBoardGoods;
	}
	public User getMessageBoardUserMe() {
		return messageBoardUserMe;
	}
	public void setMessageBoardUserMe(User messageBoardUserMe) {
		this.messageBoardUserMe = messageBoardUserMe;
	}
	public User getMessageBoardUserOther() {
		return messageBoardUserOther;
	}
	public void setMessageBoardUserOther(User messageBoardUserOther) {
		this.messageBoardUserOther = messageBoardUserOther;
	}
	public String getMessageBoardContent() {
		return messageBoardContent;
	}
	public void setMessageBoardContent(String messageBoardContent) {
		this.messageBoardContent = messageBoardContent;
	}
	public Timestamp getMessageBoardTime() {
		return messageBoardTime;
	}
	public void setMessageBoardTime(Timestamp messageBoardTime) {
		this.messageBoardTime = messageBoardTime;
	}
	public MessageBoard(Integer messageBoardId, Goods messageBoardGoods,
			User messageBoardUserMe, User messageBoardUserOther,
			String messageBoardContent, Timestamp messageBoardTime) {
		super();
		this.messageBoardId = messageBoardId;
		this.messageBoardGoods = messageBoardGoods;
		this.messageBoardUserMe = messageBoardUserMe;
		this.messageBoardUserOther = messageBoardUserOther;
		this.messageBoardContent = messageBoardContent;
		this.messageBoardTime = messageBoardTime;
	}
	public MessageBoard() {
		super();
	}
	
}
