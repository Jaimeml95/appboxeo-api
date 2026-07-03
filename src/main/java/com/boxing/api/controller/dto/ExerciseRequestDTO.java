package com.boxing.api.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExerciseRequestDTO {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    private String description;

    @NotNull(message = "Sets must not be null")
    @Min(value = 1, message = "There must be at least 1 set")
    private Integer sets;

    @NotNull(message = "Reps must not be null")
    @Min(value = 1, message = "There must be at least 1 rep")
    private Integer reps;

    @NotNull(message = "Rest must not be null")
    @Min(value = 0, message = "Rest must not be negative")
    private Integer rest;

    public ExerciseRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Integer getRest() { return rest; }
    public void setRest(Integer rest) { this.rest = rest; }
}
