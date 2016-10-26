package com.school.twohand.entity;

import java.sql.Timestamp;

public class Chat {
	Integer chatId;
	Goods chatGoods;
	User chatUserMe;
	User chatUserOther;
	String chatContent;
	Timestamp chatTime;
	public Integer getChatId() {
		return chatId;
	}
	public void setChatId(Integer chatId) {
		this.chatId = chatId;
	}
	public Goods getChatGoods() {
		return chatGoods;
	}
	public void setChatGoods(Goods chatGoods) {
		this.chatGoods = chatGoods;
	}
	public User getChatUserMe() {
		return chatUserMe;
	}
	public void setChatUserMe(User chatUserMe) {
		this.chatUserMe = chatUserMe;
	}
	public User getChatUserOther() {
		return chatUserOther;
	}
	public void setChatUserOther(User chatUserOther) {
		this.chatUserOther = chatUserOther;
	}
	public String getChatContent() {
		return chatContent;
	}
	public void setChatContent(String chatContent) {
		this.chatContent = chatContent;
	}
	public Timestamp getChatTime() {
		return chatTime;
	}
	public void setChatTime(Timestamp chatTime) {
		this.chatTime = chatTime;
	}
	public Chat(Integer chatId, Goods chatGoods, User chatUserMe,
			User chatUserOther, String chatContent, Timestamp chatTime) {
		super();
		this.chatId = chatId;
		this.chatGoods = chatGoods;
		this.chatUserMe = chatUserMe;
		this.chatUserOther = chatUserOther;
		this.chatContent = chatContent;
		this.chatTime = chatTime;
	}
	public Chat() {
		super();
	} 
	
}
