package com.boxing.api.controller.dto;

import com.boxing.api.model.VideoCategory;
import com.boxing.api.model.VideoType;

import java.util.UUID;

public class VideoResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private VideoType type;
    private String url;
    private VideoCategory category;

    public VideoResponseDTO() {}

    public VideoResponseDTO(UUID id, String title, String description, VideoType type, String url, VideoCategory category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.url = url;
        this.category = category;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public VideoType getType() { return type; }
    public void setType(VideoType type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public VideoCategory getCategory() { return category; }
    public void setCategory(VideoCategory category) { this.category = category; }
}
