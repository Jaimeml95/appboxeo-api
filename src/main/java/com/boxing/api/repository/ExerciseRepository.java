package com.boxing.api.repository;

import com.boxing.api.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Optional<Exercise> findByIdAndWorkoutId(Long id, Long workoutId);
}
