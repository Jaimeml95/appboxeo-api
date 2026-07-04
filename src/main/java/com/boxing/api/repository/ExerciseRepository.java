package com.boxing.api.repository;

import com.boxing.api.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

    Optional<Exercise> findByIdAndWorkoutId(UUID id, UUID workoutId);
}
