package com.boxing.api.controller.dto;

import com.boxing.api.model.VideoCategory;
import com.boxing.api.model.VideoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VideoRequestDTO {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Type must not be null")
    private VideoType type;

    @NotBlank(message = "URL must not be blank")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    private String url;

    @NotNull(message = "Category must not be null")
    private VideoCategory category;

    public VideoRequestDTO() {}

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
