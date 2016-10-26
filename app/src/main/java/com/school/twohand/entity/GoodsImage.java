package com.school.twohand.entity;

public class GoodsImage {
	private Integer imageId;
	private Integer imageGoodsId;
	private String imageAddress;
	public Integer getImageId() {
		return imageId;
	}
	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}
	public Integer getImageGoodsId() {
		return imageGoodsId;
	}
	public void setImageGoodsId(Integer imageGoodsId) {
		this.imageGoodsId = imageGoodsId;
	}
	public String getImageAddress() {
		return imageAddress;
	}
	public void setImageAddress(String imageAddress) {
		this.imageAddress = imageAddress;
	}
	public GoodsImage(Integer imageId, Integer imageGoodsId, String imageAddress) {
		super();
		this.imageId = imageId;
		this.imageGoodsId = imageGoodsId;
		this.imageAddress = imageAddress;
	}
	public GoodsImage() {
		super();
	}
	
	
}
