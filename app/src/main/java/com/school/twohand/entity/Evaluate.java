package com.school.twohand.entity;

public class Evaluate {
	Integer evaluateId;
	Goods evaluateGoods;
	User evaluateUserMe;
	User evaluateUserOther;
	String evaluateContext;
	public Integer getEvaluateId() {
		return evaluateId;
	}
	public void setEvaluateId(Integer evaluateId) {
		this.evaluateId = evaluateId;
	}
	public Goods getEvaluateGoods() {
		return evaluateGoods;
	}
	public void setEvaluateGoods(Goods evaluateGoods) {
		this.evaluateGoods = evaluateGoods;
	}
	public User getEvaluateUserMe() {
		return evaluateUserMe;
	}
	public void setEvaluateUserMe(User evaluateUserMe) {
		this.evaluateUserMe = evaluateUserMe;
	}
	public User getEvaluateUserOther() {
		return evaluateUserOther;
	}
	public void setEvaluateUserOther(User evaluateUserOther) {
		this.evaluateUserOther = evaluateUserOther;
	}
	public String getEvaluateContext() {
		return evaluateContext;
	}
	public void setEvaluateContext(String evaluateContext) {
		this.evaluateContext = evaluateContext;
	}
	public Evaluate() {
		super();
	}
	public Evaluate(Integer evaluateId, Goods evaluateGoods,
			User evaluateUserMe, User evaluateUserOther, String evaluateContext) {
		super();
		this.evaluateId = evaluateId;
		this.evaluateGoods = evaluateGoods;
		this.evaluateUserMe = evaluateUserMe;
		this.evaluateUserOther = evaluateUserOther;
		this.evaluateContext = evaluateContext;
	}
	
	
}
