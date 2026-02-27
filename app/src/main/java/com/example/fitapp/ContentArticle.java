package com.example.fitapp;

import java.util.Map;

public class ContentArticle {
    private String articleId;
    private String title;
    private Map<String, Integer> category;
    private String bodyText;
    private String videoUrl;

    public ContentArticle() {
    }

    public ContentArticle(String articleId, String title, Map<String, Integer> category, String bodyText, String videoUrl) {
        this.articleId = articleId;
        this.title = title;
        this.category = category;
        this.bodyText = bodyText;
        this.videoUrl = videoUrl;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Integer> getCategory() {
        return category;
    }

    public void setCategory(Map<String, Integer> category) {
        this.category = category;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
