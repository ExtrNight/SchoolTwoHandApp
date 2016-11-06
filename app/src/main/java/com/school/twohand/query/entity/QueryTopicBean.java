package com.school.twohand.query.entity;


import com.school.twohand.entity.AmoyCircleDynamicImage;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by chenglong on 2016/11/1.
 */
public class QueryTopicBean {
    private Integer userId;
    private Integer pageNo;//第几页
    private Integer pageSize;//一页显示几个商品
    private String userHead;
    private String userName;
    private Timestamp amoyCircleDynamicTime;
    private String amoyCircleDynamicTitle;
    private String amoyCircleDynamiContent;
    List<AmoyCircleDynamicImage> imageList;
    private String userAddress;
    private String amoyCircleName;
    private Integer likeSum;
    private Integer messageSum;

    public QueryTopicBean(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public QueryTopicBean(Integer userId, String userHead, String userName, Timestamp amoyCircleDynamicTime,
                          String amoyCircleDynamicTitle, String amoyCircleDynamiContent, List<AmoyCircleDynamicImage> imageList,
                          String userAddress, String amoyCircleName, Integer likeSum, Integer messageSum) {
        super();
        this.userId = userId;
        this.userHead = userHead;
        this.userName = userName;
        this.amoyCircleDynamicTime = amoyCircleDynamicTime;
        this.amoyCircleDynamicTitle = amoyCircleDynamicTitle;
        this.amoyCircleDynamiContent = amoyCircleDynamiContent;
        this.imageList = imageList;
        this.userAddress = userAddress;
        this.amoyCircleName = amoyCircleName;
        this.likeSum = likeSum;
        this.messageSum = messageSum;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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

    public Timestamp getAmoyCircleDynamicTime() {
        return amoyCircleDynamicTime;
    }

    public void setAmoyCircleDynamicTime(Timestamp amoyCircleDynamicTime) {
        this.amoyCircleDynamicTime = amoyCircleDynamicTime;
    }

    public String getAmoyCircleDynamicTitle() {
        return amoyCircleDynamicTitle;
    }

    public void setAmoyCircleDynamicTitle(String amoyCircleDynamicTitle) {
        this.amoyCircleDynamicTitle = amoyCircleDynamicTitle;
    }

    public String getAmoyCircleDynamiContent() {
        return amoyCircleDynamiContent;
    }

    public void setAmoyCircleDynamiContent(String amoyCircleDynamiContent) {
        this.amoyCircleDynamiContent = amoyCircleDynamiContent;
    }

    public List<AmoyCircleDynamicImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<AmoyCircleDynamicImage> imageList) {
        this.imageList = imageList;
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

}
