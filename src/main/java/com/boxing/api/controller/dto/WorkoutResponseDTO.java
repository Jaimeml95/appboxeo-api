package com.boxing.api.controller.dto;

import com.boxing.api.model.Difficulty;

import java.util.List;

public class WorkoutResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Difficulty difficulty;
    private Integer estimatedDuration;
    private List<ExerciseResponseDTO> exercises;

    public WorkoutResponseDTO() {}

    public WorkoutResponseDTO(Long id, String name, String description, Difficulty difficulty, Integer estimatedDuration, List<ExerciseResponseDTO> exercises) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.estimatedDuration = estimatedDuration;
        this.exercises = exercises;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public List<ExerciseResponseDTO> getExercises() { return exercises; }
    public void setExercises(List<ExerciseResponseDTO> exercises) { this.exercises = exercises; }
}
