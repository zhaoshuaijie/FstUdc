package com.muzhi.camerasdk.model;

/**
 * 图片实体
 */
public class ImageInfo {
    public String path;
    public String name;
    public long time;
    public int type;//0：普通图片；1：gif图

    public boolean selected=false;
    
    public ImageInfo(){}
    
    public ImageInfo(String path, String name, long time,int type){
        this.path = path;
        this.name = name;
        this.time = time;
        this.type=type;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageInfo other = (ImageInfo) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
