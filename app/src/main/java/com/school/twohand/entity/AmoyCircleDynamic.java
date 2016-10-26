package com.school.twohand.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** 淘圈动态的实体类
 * Created by yang on 2016/10/19 0019.
 */
public class AmoyCircleDynamic implements Parcelable {
    private int amoyCirlceDynamicId;
    private User user;                             //动态发布人的User对象
    private int amoyCircleId;                       //动态所在淘圈的Id
    private String amoyCircleDynamicTitle;         //动态的标题
    private String amoyCircleDynamicContent;      //动态的内容
    private int amoyCircleDynamicPageviews;       //浏览量
    private Timestamp amoyCircleDynamicTime;      //动态创建的时间
    List<AmoyCircleDynamicImage> imageList;         //图片对象的集合
    List<Integer> likesList;		  			  //包含点赞者用户Id的集合

    //包含所有字段的构造方法
    public AmoyCircleDynamic(int amoyCirlceDynamicId, User user, int amoyCircleId, String amoyCircleDynamicTitle,
                             String amoyCircleDynamicContent,
                             int amoyCircleDynamicPageviews,Timestamp amoyCircleDynamicTime,
                             List<AmoyCircleDynamicImage> imageList,List<Integer> likesList) {
        this.amoyCirlceDynamicId = amoyCirlceDynamicId;
        this.user = user;
        this.amoyCircleId = amoyCircleId;
        this.amoyCircleDynamicTitle = amoyCircleDynamicTitle;
        this.amoyCircleDynamicContent = amoyCircleDynamicContent;
        this.amoyCircleDynamicPageviews = amoyCircleDynamicPageviews;
        this.amoyCircleDynamicTime = amoyCircleDynamicTime;
        this.imageList = imageList;
        this.likesList = likesList;
    }

    //没有动态Id的构造方法，主要用于向数据库添加一条动态,也没有点赞者用户Id的集合
    public AmoyCircleDynamic(User user, int amoyCircleId, String amoyCircleDynamicTitle,
                             String amoyCircleDynamicContent,
                             int amoyCircleDynamicPageviews,Timestamp amoyCircleDynamicTime,List<AmoyCircleDynamicImage> imageList) {
        super();
        this.user = user;
        this.amoyCircleId = amoyCircleId;
        this.amoyCircleDynamicTitle = amoyCircleDynamicTitle;
        this.amoyCircleDynamicContent = amoyCircleDynamicContent;
        this.amoyCircleDynamicPageviews = amoyCircleDynamicPageviews;
        this.amoyCircleDynamicTime = amoyCircleDynamicTime;
        this.imageList = imageList;
    }

    public int getAmoyCirlceDynamicId() {
        return amoyCirlceDynamicId;
    }

    public void setAmoyCirlceDynamicId(int amoyCirlceDynamicId) {
        this.amoyCirlceDynamicId = amoyCirlceDynamicId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAmoyCircleId() {
        return amoyCircleId;
    }

    public void setAmoyCircleId(int amoyCircleId) {
        this.amoyCircleId = amoyCircleId;
    }

    public String getAmoyCircleDynamicTitle() {
        return amoyCircleDynamicTitle;
    }

    public void setAmoyCircleDynamicTitle(String amoyCircleDynamicTitle) {
        this.amoyCircleDynamicTitle = amoyCircleDynamicTitle;
    }

    public String getAmoyCircleDynamicContent() {
        return amoyCircleDynamicContent;
    }

    public void setAmoyCircleDynamicContent(String amoyCircleDynamicContent) {
        this.amoyCircleDynamicContent = amoyCircleDynamicContent;
    }

    public int getAmoyCircleDynamicPageviews() {
        return amoyCircleDynamicPageviews;
    }

    public void setAmoyCircleDynamicPageviews(int amoyCircleDynamicPageviews) {
        this.amoyCircleDynamicPageviews = amoyCircleDynamicPageviews;
    }

    public Timestamp getAmoyCircleDynamicTime() {
        return amoyCircleDynamicTime;
    }

    public void setAmoyCircleDynamicTime(Timestamp amoyCircleDynamicTime) {
        this.amoyCircleDynamicTime = amoyCircleDynamicTime;
    }

    public List<AmoyCircleDynamicImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<AmoyCircleDynamicImage> imageList) {
        this.imageList = imageList;
    }
    public List<Integer> getLikesList() {
        return likesList;
    }

    public void setLikesList(List<Integer> likesList) {
        this.likesList = likesList;
    }

    //--------------序列化
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.amoyCirlceDynamicId);
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.amoyCircleId);
        dest.writeString(this.amoyCircleDynamicTitle);
        dest.writeString(this.amoyCircleDynamicContent);
        dest.writeInt(this.amoyCircleDynamicPageviews);
        dest.writeSerializable(this.amoyCircleDynamicTime);
        dest.writeTypedList(this.imageList);
        dest.writeList(this.likesList);
    }

    protected AmoyCircleDynamic(Parcel in) {
        this.amoyCirlceDynamicId = in.readInt();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.amoyCircleId = in.readInt();
        this.amoyCircleDynamicTitle = in.readString();
        this.amoyCircleDynamicContent = in.readString();
        this.amoyCircleDynamicPageviews = in.readInt();
        this.amoyCircleDynamicTime = (Timestamp) in.readSerializable();
        this.imageList = in.createTypedArrayList(AmoyCircleDynamicImage.CREATOR);
        this.likesList = new ArrayList<Integer>();
        in.readList(this.likesList, Integer.class.getClassLoader());
    }

    public static final Creator<AmoyCircleDynamic> CREATOR = new Creator<AmoyCircleDynamic>() {
        @Override
        public AmoyCircleDynamic createFromParcel(Parcel source) {
            return new AmoyCircleDynamic(source);
        }

        @Override
        public AmoyCircleDynamic[] newArray(int size) {
            return new AmoyCircleDynamic[size];
        }
    };



}
