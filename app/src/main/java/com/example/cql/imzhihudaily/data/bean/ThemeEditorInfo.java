package com.example.cql.imzhihudaily.data.bean;

/**
 * Created by CQL on 2016/10/17.
 *  "url":"http://www.zhihu.com/people/wezeit",
 "bio":"微在 Wezeit 主编",
 "id":70,
 "avatar":"http://pic4.zhimg.com/068311926_m.jpg",
 "name":"益康糯米"
 */

public class ThemeEditorInfo {
    private String url;
    private String bio;
    private int id;
    private String avatar;
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
