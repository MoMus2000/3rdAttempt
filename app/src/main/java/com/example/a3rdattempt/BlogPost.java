package com.example.a3rdattempt;

import java.sql.Timestamp;

public class BlogPost {
    public String userId, imageUrl, desc, imageThumb;

    public BlogPost(){}

    public BlogPost(String userId, String imageUrl, String desc, String imageThumb, Timestamp timestamp) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.desc = desc;
        this.imageThumb = imageThumb;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }
    
}
