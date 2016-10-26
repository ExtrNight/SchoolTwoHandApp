package com.school.twohand.entity;

public class ClassTbl {
	private Integer class_id;
	private String goodsClass;
	
	public Integer getClass_id() {
		return class_id;
	}
	public void setClass_id(Integer class_id) {
		this.class_id = class_id;
	}
	public String getGoodsClass() {
		return goodsClass;
	}
	public void setGoodsClass(String goodsClass) {
		this.goodsClass = goodsClass;
	}
	public ClassTbl(Integer class_id, String goodsClass) {
		super();
		this.class_id = class_id;
		this.goodsClass = goodsClass;
	}
	public ClassTbl() {
		super();
	}
	
	
	
}
