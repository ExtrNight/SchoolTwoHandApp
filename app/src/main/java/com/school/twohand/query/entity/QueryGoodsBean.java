package com.school.twohand.query.entity;



public class QueryGoodsBean {
	private String productName;//商品名
	private String schoolName;//定位学校
	private String goodsClass;//商品分类
	private int orderFlag;//查询方式
	private Integer pageNo;//第几页
	private Integer pageSize;//一页显示几个商品
	private Integer amoyCircleId;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public int getOrderFlag() {
		return orderFlag;
	}
	public void setOrderFlag(int orderFlag) {
		this.orderFlag = orderFlag;
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

	public String getGoodsClass() {
		return goodsClass;
	}

	public void setGoodsClass(String goodsClass) {
		this.goodsClass = goodsClass;
	}


	public QueryGoodsBean() {
		super();
	}
	public Integer getAmoyCircleId() {
		return amoyCircleId;
	}
	public void setAmoyCircleId(Integer amoyCircleId) {
		this.amoyCircleId = amoyCircleId;
	}


	public QueryGoodsBean(String productName, String schoolName,
						  String goodsClass, int orderFlag, Integer pageNo, Integer pageSize,
						  Integer amoyCircleId) {
		super();
		this.productName = productName;
		this.schoolName = schoolName;
		this.goodsClass = goodsClass;
		this.orderFlag = orderFlag;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.amoyCircleId = amoyCircleId;
	}
	public QueryGoodsBean(String productName, String schoolName, String goodsClass, int orderFlag, Integer pageNo, Integer pageSize) {
		this.productName = productName;
		this.schoolName = schoolName;
		this.goodsClass = goodsClass;
		this.orderFlag = orderFlag;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}
}
