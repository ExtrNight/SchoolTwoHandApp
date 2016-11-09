package com.school.twohand.query.entity;

/**
 * Created by C5-0 on 2016/11/07.
 */
public class QueryCircleDetailBean {
    private Integer userId;
    private String imageUrl;
    private String circleName;
    private Integer circleNumber;
    private Integer releaseSum;
    public QueryCircleDetailBean(){}



    public QueryCircleDetailBean(Integer userId) {
        super();
        this.userId = userId;
    }



    public QueryCircleDetailBean(String imageUrl, String circleName, Integer circleNumber,
                                 Integer releaseSum) {
        super();
        this.imageUrl = imageUrl;
        this.circleName = circleName;
        this.circleNumber = circleNumber;

        this.releaseSum = releaseSum;
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public Integer getCircleNumber() {
        return circleNumber;
    }

    public void setCircleNumber(Integer circleNumber) {
        this.circleNumber = circleNumber;
    }



    public Integer getUserId() {
        return userId;
    }



    public void setUserId(Integer userId) {
        this.userId = userId;
    }



    public Integer getReleaseSum() {
        return releaseSum;
    }



    public void setReleaseSum(Integer releaseSum) {
        this.releaseSum = releaseSum;
    }

}
