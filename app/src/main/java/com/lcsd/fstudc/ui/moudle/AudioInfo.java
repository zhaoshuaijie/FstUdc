package com.lcsd.fstudc.ui.moudle;

/**
 * Created by Administrator on 2018/4/13.
 */
public class AudioInfo {
    private int id;
    private String displayName;
    private String mimeType;
    private String path;
    private long size;
    private long duration;
    private String date;

    public AudioInfo(int id, String displayName, String mimeType,
                     String path, String date, long size, long duration) {
        this.id = id;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.date = date;
        this.size = size;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
