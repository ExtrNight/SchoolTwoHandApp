package com.school.twohand.entity;

import java.sql.Timestamp;

public class OrderTbl {
	Integer orderId;
	User orderUser;
	Goods orderGoods;
	Integer orderStatusId;
	Receipt orderReceipt;
	Timestamp orderTime;
	Float orderPrice;
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public User getOrderUser() {
		return orderUser;
	}
	public void setOrderUser(User orderUser) {
		this.orderUser = orderUser;
	}
	public Goods getOrderGoods() {
		return orderGoods;
	}
	public void setOrderGoods(Goods orderGoods) {
		this.orderGoods = orderGoods;
	}
	public Integer getOrderStatusId() {
		return orderStatusId;
	}
	public void setOrderStatusId(Integer orderStatusId) {
		this.orderStatusId = orderStatusId;
	}
	public Receipt getOrderReceipt() {
		return orderReceipt;
	}
	public void setOrderReceipt(Receipt orderReceipt) {
		this.orderReceipt = orderReceipt;
	}
	public Timestamp getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Timestamp orderTime) {
		this.orderTime = orderTime;
	}
	public Float getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(Float orderPrice) {
		this.orderPrice = orderPrice;
	}
	public OrderTbl(Integer orderId, User orderUser, Goods orderGoods,
			Integer orderStatusId, Receipt orderReceipt, Timestamp orderTime,
			Float orderPrice) {
		super();
		this.orderId = orderId;
		this.orderUser = orderUser;
		this.orderGoods = orderGoods;
		this.orderStatusId = orderStatusId;
		this.orderReceipt = orderReceipt;
		this.orderTime = orderTime;
		this.orderPrice = orderPrice;
	}
	public OrderTbl() {
		super();
	}
	
	
}
