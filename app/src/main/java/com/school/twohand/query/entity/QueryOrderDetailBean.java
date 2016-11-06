package com.school.twohand.query.entity;


import com.school.twohand.entity.GoodsImage;
import com.school.twohand.entity.GoodsOrderState;

import java.sql.Timestamp;

/**
 * Created by C5-0 on 2016/11/03.
 */
public class QueryOrderDetailBean {
    private Integer orderId;
    private GoodsOrderState goodsOrderState;
    private Timestamp orderTime;
    private String sellUserName;
    private GoodsImage goodsImage;
    private String goodsName;
    private Float goodsPrice;
    private String rechieveName;
    private String phoneNumber;
    private String receiptDetail;
    private String orderNumber;

    public QueryOrderDetailBean(){}

    public QueryOrderDetailBean(Integer orderId) {
        this.orderId = orderId;
    }

    public QueryOrderDetailBean(Integer orderId, GoodsOrderState goodsOrderState, Timestamp orderTime, String sellUserName, GoodsImage goodsImage, String goodsName, Float goodsPrice, String rechieveName, String phoneNumber, String receiptDetail, String orderNumber) {
        this.orderId = orderId;
        this.goodsOrderState = goodsOrderState;
        this.orderTime = orderTime;
        this.sellUserName = sellUserName;
        this.goodsImage = goodsImage;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.rechieveName = rechieveName;
        this.phoneNumber = phoneNumber;
        this.receiptDetail = receiptDetail;
        this.orderNumber = orderNumber;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public String getSellUserName() {
        return sellUserName;
    }

    public void setSellUserName(String sellUserName) {
        this.sellUserName = sellUserName;
    }

    public GoodsImage getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(GoodsImage goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Float getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Float goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getRechieveName() {
        return rechieveName;
    }

    public void setRechieveName(String rechieveName) {
        this.rechieveName = rechieveName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getReceiptDetail() {
        return receiptDetail;
    }

    public void setReceiptDetail(String receiptDetail) {
        this.receiptDetail = receiptDetail;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


}


