package com.school.twohand.query.entity;

/**
 * Created by C5-0 on 2016/11/08.
 */
public class QueryGetAddressBean {
    private Integer userId;
    private String userName;
    private String receiptNumber;
    private String receiptDetail;
    public QueryGetAddressBean(){

    }
    public QueryGetAddressBean(Integer userId) {
        this.userId = userId;
    }





    public QueryGetAddressBean(String userName, String receiptNumber, String receiptDetail) {
        super();
        this.userName = userName;
        this.receiptNumber = receiptNumber;
        this.receiptDetail = receiptDetail;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }





    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getReceiptNumber() {
        return receiptNumber;
    }
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    public String getReceiptDetail() {
        return receiptDetail;
    }

    public void setReceiptDetail(String receiptDetail) {
        this.receiptDetail = receiptDetail;
    }
}
