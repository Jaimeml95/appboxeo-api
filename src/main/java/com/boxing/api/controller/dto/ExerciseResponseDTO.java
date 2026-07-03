package com.boxing.api.controller.dto;

public class ExerciseResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Integer sets;
    private Integer reps;
    private Integer rest;

    public ExerciseResponseDTO() {}

    public ExerciseResponseDTO(Long id, String name, String description, Integer sets, Integer reps, Integer rest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.rest = rest;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
