package com.school.twohand.entity;

public class Auction {
	Integer auctionId;
	User auctionUser;
	Goods auctionGoods;
	Float auctionPrice;
	public Integer getAuctionId() {
		return auctionId;
	}
	public void setAuctionId(Integer auctionId) {
		this.auctionId = auctionId;
	}
	public User getAuctionUser() {
		return auctionUser;
	}
	public void setAuctionUser(User auctionUser) {
		this.auctionUser = auctionUser;
	}
	public Goods getAuctionGoods() {
		return auctionGoods;
	}
	public void setAuctionGoods(Goods auctionGoods) {
		this.auctionGoods = auctionGoods;
	}
	public Float getAuctionPrice() {
		return auctionPrice;
	}
	public void setAuctionPrice(Float auctionPrice) {
		this.auctionPrice = auctionPrice;
	}
	public Auction() {
		super();
	}
	public Auction(Integer auctionId, User auctionUser, Goods auctionGoods,
			Float auctionPrice) {
		super();
		this.auctionId = auctionId;
		this.auctionUser = auctionUser;
		this.auctionGoods = auctionGoods;
		this.auctionPrice = auctionPrice;
	}
	
}
