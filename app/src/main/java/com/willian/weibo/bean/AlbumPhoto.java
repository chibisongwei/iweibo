package com.willian.weibo.bean;

import java.io.Serializable;

/**
 * 相册图片实体类
 */
public class AlbumPhoto implements Serializable{

    // 图片路径
    public String photoPath;

    // 图片名称
    public String photoName;

    // 图片时间
    public long photoTime;

    public AlbumPhoto(String photoPath, String photoName, long photoTime) {
        this.photoPath = photoPath;
        this.photoName = photoName;
        this.photoTime = photoTime;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public long getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(long photoTime) {
        this.photoTime = photoTime;
    }
}
