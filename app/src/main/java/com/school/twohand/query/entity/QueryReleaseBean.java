package com.school.twohand.query.entity;

import com.school.twohand.entity.GoodsImage;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by chenglong on 2016/11/1.
 */
public class QueryReleaseBean {
    private Integer pageNo;//第几页
    private Integer pageSize;//一页显示几个商品
    private Integer goodsId;
    private String goodsDescribe;
    private Float goodsPrice;
    private List<GoodsImage> goodsImage;
    private Timestamp goodsReleaseTime;
    private Integer likeSum;
    private Integer messageSum;

    public QueryReleaseBean() {

    }

    public QueryReleaseBean(Integer pageNo, Integer pageSize) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public QueryReleaseBean(Integer goodsId, String goodsDescribe, Float goodsPrice, List<GoodsImage> goodsImage,
                            Timestamp goodsReleaseTime, Integer likeSum, Integer messageSum) {
        super();
        this.goodsId = goodsId;
        this.goodsDescribe = goodsDescribe;
        this.goodsPrice = goodsPrice;
        this.goodsImage = goodsImage;
        this.goodsReleaseTime = goodsReleaseTime;
        this.likeSum = likeSum;
        this.messageSum = messageSum;
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




}
