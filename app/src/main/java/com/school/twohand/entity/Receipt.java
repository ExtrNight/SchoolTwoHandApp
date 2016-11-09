package com.school.twohand.entity;

public class Receipt {
	Integer receiptId;
	User receiptUser;
	String receiptProvince;
	String receiptCity;
	String receiptStreet;
	String receiptSchool;
	String receiptDetailed;
	String receiptContactNumber;
	String receiptZipCode;
	String receiptConsignee;
	public Receipt(Integer receiptId, String receiptDetailed, String receiptContactNumber) {
		super();
		this.receiptId = receiptId;
		this.receiptDetailed = receiptDetailed;
		this.receiptContactNumber = receiptContactNumber;
	}

	public Integer getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(Integer receiptId) {
		this.receiptId = receiptId;
	}
	public User getReceiptUser() {
		return receiptUser;
	}
	public void setReceiptUser(User receiptUser) {
		this.receiptUser = receiptUser;
	}
	public String getReceiptProvince() {
		return receiptProvince;
	}
	public void setReceiptProvince(String receiptProvince) {
		this.receiptProvince = receiptProvince;
	}
	public String getReceiptCity() {
		return receiptCity;
	}
	public void setReceiptCity(String receiptCity) {
		this.receiptCity = receiptCity;
	}
	public String getReceiptStreet() {
		return receiptStreet;
	}
	public void setReceiptStreet(String receiptStreet) {
		this.receiptStreet = receiptStreet;
	}
	public String getReceiptSchool() {
		return receiptSchool;
	}
	public void setReceiptSchool(String receiptSchool) {
		this.receiptSchool = receiptSchool;
	}
	public String getReceiptDetailed() {
		return receiptDetailed;
	}
	public void setReceiptDetailed(String receiptDetailed) {
		this.receiptDetailed = receiptDetailed;
	}
	public String getReceiptContactNumber() {
		return receiptContactNumber;
	}
	public void setReceiptContactNumber(String receiptContactNumber) {
		this.receiptContactNumber = receiptContactNumber;
	}
	public String getReceiptZipCode() {
		return receiptZipCode;
	}
	public void setReceiptZipCode(String receiptZipCode) {
		this.receiptZipCode = receiptZipCode;
	}
	public String getReceiptConsignee() {
		return receiptConsignee;
	}
	public void setReceiptConsignee(String receiptConsignee) {
		this.receiptConsignee = receiptConsignee;
	}
	public Receipt(Integer receiptId, User receiptUser, String receiptProvince,
			String receiptCity, String receiptStreet, String receiptSchool,
			String receiptDetailed, String receiptContactNumber,
			String receiptZipCode, String receiptConsignee) {
		super();
		this.receiptId = receiptId;
		this.receiptUser = receiptUser;
		this.receiptProvince = receiptProvince;
		this.receiptCity = receiptCity;
		this.receiptStreet = receiptStreet;
		this.receiptSchool = receiptSchool;
		this.receiptDetailed = receiptDetailed;
		this.receiptContactNumber = receiptContactNumber;
		this.receiptZipCode = receiptZipCode;
		this.receiptConsignee = receiptConsignee;
	}
	public Receipt() {
		super();
	}

	
}
