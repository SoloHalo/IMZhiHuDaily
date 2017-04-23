package com.example.cql.imzhihudaily.data.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by CQL on 2016/10/5.
 */

public class HomeInfo {
    private String date;
    private List<HomeStoriesInfo> stories;
    @SerializedName("top_stories")
    private List<HomeTopInfo> topStories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<HomeStoriesInfo> getStories() {
        return stories;
    }

    public void setStories(List<HomeStoriesInfo> stories) {
        this.stories = stories;
    }

    public List<HomeTopInfo> getTopStories() {
        return topStories;
    }

    public void setTopStories(List<HomeTopInfo> topStories) {
        this.topStories = topStories;
    }
}
