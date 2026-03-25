package com.example.fitapp.classes;

import java.util.Map;

/**
 * Represents a content article or tip in the application.
 * Articles can belong to categories like training, nutrition, or rest.
 */
public class ContentArticle {
    /** Unique identifier for the article. */
    private String articleId;
    /** The title of the article or tip. */
    private String title;
    /** Map representing the category of the article (e.g., training, nutrition, rest). */
    private Map<String, Integer> category;
    /** The main text content of the article. */
    private String bodyText;
    /** Optional URL to a video related to the article. */
    private String videoUrl;

    /**
     * Default constructor for Firebase serialization.
     */
    public ContentArticle() {
    }

    /**
     * Constructs a new ContentArticle with all fields.
     * @param articleId The unique ID of the article.
     * @param title The title of the article.
     * @param category The category map.
     * @param bodyText The main text of the article.
     * @param videoUrl The URL for an associated video.
     */
    public ContentArticle(String articleId, String title, Map<String, Integer> category, String bodyText, String videoUrl) {
        this.articleId = articleId;
        this.title = title;
        this.category = category;
        this.bodyText = bodyText;
        this.videoUrl = videoUrl;
    }

    /**
     * @return The article's unique ID.
     */
    public String getArticleId() {
        return articleId;
    }

    /**
     * @param articleId The article's unique ID to set.
     */
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    /**
     * @return The title of the article.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The category map of the article.
     */
    public Map<String, Integer> getCategory() {
        return category;
    }

    /**
     * @param category The category map to set.
     */
    public void setCategory(Map<String, Integer> category) {
        this.category = category;
    }

    /**
     * @return The main text content of the article.
     */
    public String getBodyText() {
        return bodyText;
    }

    /**
     * @param bodyText The text content to set.
     */
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    /**
     * @return The URL of the associated video.
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * @param videoUrl The video URL to set.
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
