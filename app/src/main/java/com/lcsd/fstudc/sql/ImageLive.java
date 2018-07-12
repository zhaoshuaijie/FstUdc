package com.lcsd.fstudc.sql;

import java.io.Serializable;

/**
 * Created by jie on 2018/5/30.
 * 创建图文直播草稿
 */
public class ImageLive implements Serializable {
    private  Integer id;
    private String title;       //标题
    private String time;        //修改时间
    private String startime;    //开始时间
    private String endtime;     //结束时间
    private String sponsor;     //主办方
    private String address;     //地址
    private String imgs;     //封面图集
    private String synopsis;    //简介

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
