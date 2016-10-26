package com.school.twohand.entity.bean;

import com.school.twohand.entity.GoodsImage;

import java.util.List;

/**
 * 每个淘圈里面显示的商品的Item的实体类
 * @author yang
 *
 */
public class EachCircleItem {
	
	private int goodsId;
	private int userId;
	private String userName;
	private String userHead;			  //用户头像url地址
	private float goodsPrice;             
	private String goodsDescribe;         //商品描述
	private List<GoodsImage> goodsImages;  //存放商品图片的集合
	
	public EachCircleItem(int goodsId, int userId, String userName, String userHead, float goodsPrice,
						  String goodsDescribe, List<GoodsImage> goodsImages) {
		super();
		this.goodsId = goodsId;
		this.userId = userId;
		this.userName = userName;
		this.userHead = userHead;
		this.goodsPrice = goodsPrice;
		this.goodsDescribe = goodsDescribe;
		this.goodsImages = goodsImages;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserHead() {
		return userHead;
	}

	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}

	public float getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(float goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public String getGoodsDescribe() {
		return goodsDescribe;
	}

	public void setGoodsDescribe(String goodsDescribe) {
		this.goodsDescribe = goodsDescribe;
	}

	public List<GoodsImage> getGoodsImages() {
		return goodsImages;
	}

	public void setGoodsImages(List<GoodsImage> goodsImages) {
		this.goodsImages = goodsImages;
	}

	@Override
	public String toString() {
		return "EachCircleItem [goodsId=" + goodsId + ", userId=" + userId + ", userName=" + userName + ", userHead="
				+ userHead + ", goodsPrice=" + goodsPrice + ", goodsDescribe=" + goodsDescribe + ", goodsImages="
				+ goodsImages + "]";
	}
	
	

}
