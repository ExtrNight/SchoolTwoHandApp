package com.school.twohand.query.entity;

import com.school.twohand.entity.GoodsImage;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by C5-0 on 2016/11/07.
 */
public class QueryPriaseBean {

    private Integer pageNo;//第几页
    private Integer pageSize;//一页显示几个商品
    private Integer userId;
    private Integer goodsId;
    private String userHead;
    private String userName;
    private String goodsDescribe;
    private Float goodsPrice;
    private List<GoodsImage> goodsImage;
    private Timestamp goodsReleaseTime;
    private String userAddress;
    private String amoyCircleName;
    private Integer likeSum;
    private Integer messageSum;

    public QueryPriaseBean(){}

    public QueryPriaseBean(Integer userId) {
        super();
        this.userId = userId;
    }

    public QueryPriaseBean(Integer pageNo, Integer pageSize, Integer userId, Integer goodsId, String userHead, String userName, String goodsDescribe, Float goodsPrice, List<GoodsImage> goodsImage, Timestamp goodsReleaseTime, String userAddress, String amoyCircleName, Integer likeSum, Integer messageSum) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.userId = userId;
        this.goodsId = goodsId;
        this.userHead = userHead;
        this.userName = userName;
        this.goodsDescribe = goodsDescribe;
        this.goodsPrice = goodsPrice;
        this.goodsImage = goodsImage;
        this.goodsReleaseTime = goodsReleaseTime;
        this.userAddress = userAddress;
        this.amoyCircleName = amoyCircleName;
        this.likeSum = likeSum;
        this.messageSum = messageSum;
    }



    public QueryPriaseBean(String userHead, String userName, String goodsDescribe, Float goodsPrice,
                           List<GoodsImage> goodsImage, Timestamp goodsReleaseTime, String userAddress, String amoyCircleName,
                           Integer likeSum, Integer messageSum) {
        super();
        this.userHead = userHead;
        this.userName = userName;
        this.goodsDescribe = goodsDescribe;
        this.goodsPrice = goodsPrice;
        this.goodsImage = goodsImage;
        this.goodsReleaseTime = goodsReleaseTime;
        this.userAddress = userAddress;
        this.amoyCircleName = amoyCircleName;
        this.likeSum = likeSum;
        this.messageSum = messageSum;
    }



    public QueryPriaseBean(Integer pageNo, Integer pageSize, Integer userId) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.userId = userId;
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





    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsDescribe() {
        return goodsDescribe;
    }

    public void setGoodsDescribe(String goodsDescribe) {
        this.goodsDescribe = goodsDescribe;
    }

    public Float getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Float goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public List<GoodsImage> getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(List<GoodsImage> goodsImage) {
        this.goodsImage = goodsImage;
    }

    public Timestamp getGoodsReleaseTime() {
        return goodsReleaseTime;
    }

    public void setGoodsReleaseTime(Timestamp goodsReleaseTime) {
        this.goodsReleaseTime = goodsReleaseTime;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getAmoyCircleName() {
        return amoyCircleName;
    }

    public void setAmoyCircleName(String amoyCircleName) {
        this.amoyCircleName = amoyCircleName;
    }

    public Integer getLikeSum() {
        return likeSum;
    }

    public void setLikeSum(Integer likeSum) {
        this.likeSum = likeSum;
    }

    public Integer getMessageSum() {
        return messageSum;
    }

    public void setMessageSum(Integer messageSum) {
        this.messageSum = messageSum;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
