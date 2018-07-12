package com.lcsd.fstudc.ui.moudle;

/**
 * Created by jie on 2018/7/10.
 */
public class SckInfo {
    private String id;    /*1828*/
    private String addtime;    /*1530694625*/
    private String title;    /*about_pic*/
    private String mime_type;    /*image/jpeg*/
    private String url;    /*sw.5kah.com/res/vlive/img/201807/04/58c5ee87ad1d7eb9.jpg*/
    private String ico;
    private float scale;

    public void setId(String value) {
        this.id = value;
    }

    public String getId() {
        return this.id;
    }

    public void setAddtime(String value) {
        this.addtime = value;
    }

    public String getAddtime() {
        return this.addtime;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getTitle() {
        return this.title;
    }

    public void setMime_type(String value) {
        this.mime_type = value;
    }

    public String getMime_type() {
        return this.mime_type;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public String getUrl() {
        return this.url;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }
}
