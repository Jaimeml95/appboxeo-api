package com.boxing.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Sets must not be null")
    @Min(value = 1, message = "There must be at least 1 set")
    @Column(nullable = false)
    private Integer sets;

    @NotNull(message = "Reps must not be null")
    @Min(value = 1, message = "There must be at least 1 rep")
    @Column(nullable = false)
    private Integer reps;

    @NotNull(message = "Rest must not be null")
    @Min(value = 0, message = "Rest must not be negative")
    @Column(nullable = false)
    private Integer rest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    public Exercise() {}

    public Exercise(String name, String description, Integer sets, Integer reps, Integer rest, Workout workout) {
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.rest = rest;
        this.workout = workout;
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

    public Workout getWorkout() { return workout; }
    public void setWorkout(Workout workout) { this.workout = workout; }
}
