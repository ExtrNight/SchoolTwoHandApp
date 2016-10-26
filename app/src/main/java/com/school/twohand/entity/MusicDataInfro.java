package com.school.twohand.entity;

/**
 * Created by Administrator on 2016/10/14 0014.
 */
public class MusicDataInfro {
    private Integer _id;
    private String musicName;//音乐名
    private String singer;//演唱者
    private int duration;//演唱持续时间

    public MusicDataInfro() {
    }

    public MusicDataInfro(Integer _id, String musicName, String singer, int duration) {
        this._id = _id;
        this.musicName = musicName;
        this.singer = singer;
        this.duration = duration;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

