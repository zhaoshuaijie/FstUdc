package com.lcsd.fstudc.ui.moudle;

/**
 * Created by Administrator on 2018/4/24.
 */
public class logoInfo {
    private String path;
    private int picx;
    private int picy;
    private float picWidth;
    private float picHeight;
    //是否是gif
    private boolean isAnimation;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPicx() {
        return picx;
    }

    public void setPicx(int picx) {
        this.picx = picx;
    }

    public int getPicy() {
        return picy;
    }

    public void setPicy(int picy) {
        this.picy = picy;
    }

    public float getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(float picWidth) {
        this.picWidth = picWidth;
    }

    public float getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(float picHeight) {
        this.picHeight = picHeight;
    }

    public boolean isAnimation() {
        return isAnimation;
    }

    public void setAnimation(boolean animation) {
        isAnimation = animation;
    }
}
