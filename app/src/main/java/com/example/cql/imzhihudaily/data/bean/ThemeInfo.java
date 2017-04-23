package com.example.cql.imzhihudaily.data.bean;

import java.util.List;

/**
 * Created by CQL on 2016/10/17.
 */
/***每个栏目具体信息***/

public class ThemeInfo {
    private List<ThemeStoriesInfo> stories;
    private String description;
    private String background;
    private String color;
    private String name;
    private String image;
    private List<ThemeEditorInfo> editors;

    public List<ThemeStoriesInfo> getStories() {
        return stories;
    }

    public void setStories(List<ThemeStoriesInfo> stories) {
        this.stories = stories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ThemeEditorInfo> getEditors() {
        return editors;
    }

    public void setEditors(List<ThemeEditorInfo> editors) {
        this.editors = editors;
    }
}
