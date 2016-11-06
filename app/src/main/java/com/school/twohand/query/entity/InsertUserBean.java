package com.school.twohand.query.entity;


import java.sql.Timestamp;

/**
 * Created by chenglong on 2016/10/27.
 */
public class InsertUserBean {
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public InsertUserBean(Integer userId) {

        this.userId = userId;
    }

    private String userSex;
    private Timestamp userBirthday;
    private String userAddress;
    private String userPersonalProfile;

    public InsertUserBean() {

    }

    public InsertUserBean( String userSex, Timestamp userBirthday, String userAddress, String userPersonalProfile) {
        this.userSex = userSex;
        this.userBirthday = userBirthday;
        this.userAddress = userAddress;
        this.userPersonalProfile = userPersonalProfile;
    }




    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public Timestamp getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Timestamp userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPersonalProfile() {
        return userPersonalProfile;
    }

    public void setUserPersonalProfile(String userPersonalProfile) {
        this.userPersonalProfile = userPersonalProfile;
    }
}
