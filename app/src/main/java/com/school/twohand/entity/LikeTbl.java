package com.school.twohand.entity;

public class LikeTbl {
	Integer likeId;
	User likeUserMe;
	User likeUserOther;
	Goods likeGoods;
	public Integer getLikeId() {
		return likeId;
	}
	public void setLikeId(Integer likeId) {
		this.likeId = likeId;
	}
	public User getLikeUserMe() {
		return likeUserMe;
	}
	public void setLikeUserMe(User likeUserMe) {
		this.likeUserMe = likeUserMe;
	}
	public User getLikeUserOther() {
		return likeUserOther;
	}
	public void setLikeUserOther(User likeUserOther) {
		this.likeUserOther = likeUserOther;
	}
	public Goods getLikeGoodsId() {
		return likeGoods;
	}
	public void setLikeGoodsId(Goods likeGoods) {
		this.likeGoods = likeGoods;
	}
	public LikeTbl(Integer likeId, User likeUserMe, User likeUserOther,
			Goods likeGoods) {
		super();
		this.likeId = likeId;
		this.likeUserMe = likeUserMe;
		this.likeUserOther = likeUserOther;
		this.likeGoods = likeGoods;
	}
	public LikeTbl() {
		super();
	}
	
}
