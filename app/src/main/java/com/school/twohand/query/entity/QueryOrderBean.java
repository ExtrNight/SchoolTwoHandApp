package com.school.twohand.query.entity;





import com.school.twohand.entity.GoodsImage;
import com.school.twohand.entity.GoodsOrderState;

import java.sql.Timestamp;

/**
 * Created by chenglong on 2016/10/29.
 */
public class QueryOrderBean {
	private Integer pageNo;
	private Integer pageSize;
	private Integer orderId;
	private GoodsImage goodsImage;
	private String goodsDescribe;
	private Float orderPrice;
	private GoodsOrderState goodsOrderState;
	private Timestamp orderTime;



	public QueryOrderBean(){}




	public QueryOrderBean(Integer pageNo, Integer pageSize) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}









	public QueryOrderBean(Integer orderId, GoodsImage goodsImage, String goodsDescribe, Float orderPrice,
						  GoodsOrderState goodsOrderState, Timestamp orderTime) {
		super();
		this.orderId = orderId;
		this.goodsImage = goodsImage;
		this.goodsDescribe = goodsDescribe;
		this.orderPrice = orderPrice;
		this.goodsOrderState = goodsOrderState;
		this.orderTime = orderTime;
	}




	public QueryOrderBean(Integer pageNo, Integer pageSize, Integer orderId, GoodsImage goodsImage,
						  String goodsDescribe, Float orderPrice, GoodsOrderState goodsOrderState, Timestamp orderTime) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.orderId = orderId;
		this.goodsImage = goodsImage;
		this.goodsDescribe = goodsDescribe;
		this.orderPrice = orderPrice;
		this.goodsOrderState = goodsOrderState;
		this.orderTime = orderTime;
	}




	public Integer getPageNo() {
		return pageNo;
	}




	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}




	public Integer getPageSize() {
		return pageSize;
	}




	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}




	public Integer getOrderId() {
		return orderId;
	}




	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}


	public String getGoodsDescribe() {
		return goodsDescribe;
	}




	public void setGoodsDescribe(String goodsDescribe) {
		this.goodsDescribe = goodsDescribe;
	}




	public Float getOrderPrice() {
		return orderPrice;
	}




	public void setOrderPrice(Float orderPrice) {
		this.orderPrice = orderPrice;
	}




	public GoodsOrderState getGoodsOrderState() {
		return goodsOrderState;
	}




	public void setGoodsOrderState(GoodsOrderState goodsOrderState) {
		this.goodsOrderState = goodsOrderState;
	}




	public Timestamp getOrderTime() {
		return orderTime;
	}




	public void setOrderTime(Timestamp orderTime) {
		this.orderTime = orderTime;
	}




	public GoodsImage getGoodsImage() {
		return goodsImage;
	}




	public void setGoodsImage(GoodsImage goodsImage) {
		this.goodsImage = goodsImage;
	}


}
