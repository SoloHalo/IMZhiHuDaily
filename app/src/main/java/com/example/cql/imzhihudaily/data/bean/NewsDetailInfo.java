package com.example.cql.imzhihudaily.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CQL on 2016/10/11.
 */

public class NewsDetailInfo {
    private  String body;
    @SerializedName("image_source")
    private String imageSource;
    private String title;
    private String image;
    @SerializedName("share_url")
    private String shareUrl;
    @SerializedName("ga_prefix")
    private String gaPrefix;
    private int type;
    private String id;

    public  String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getGaPrefix() {
        return gaPrefix;
    }

    public void setGaPrefix(String gaPrefix) {
        this.gaPrefix = gaPrefix;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
