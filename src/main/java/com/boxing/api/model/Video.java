package com.boxing.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VideoType type;

    @NotBlank(message = "URL must not be blank")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String url;

    @NotNull(message = "Category must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VideoCategory category;

    public Video() {}

    public Video(String title, String description, VideoType type, String url, VideoCategory category) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.url = url;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
