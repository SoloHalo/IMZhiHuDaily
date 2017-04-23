package com.example.cql.imzhihudaily.data.bean;

import java.util.List;

/**
 * Created by CQL on 2016/10/17.
 */

public class ThemeStoriesInfo {
    private List<String> images;
    private int type;
    private int id;
    private String title;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
