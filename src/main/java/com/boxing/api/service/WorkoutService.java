package com.boxing.api.service;

import com.boxing.api.controller.dto.ExerciseRequestDTO;
import com.boxing.api.controller.dto.ExerciseResponseDTO;
import com.boxing.api.controller.dto.WorkoutRequestDTO;
import com.boxing.api.controller.dto.WorkoutResponseDTO;

import java.util.List;

public interface WorkoutService {

    List<WorkoutResponseDTO> getAll();

    WorkoutResponseDTO getById(Long id);

    WorkoutResponseDTO create(WorkoutRequestDTO dto);

    WorkoutResponseDTO update(Long id, WorkoutRequestDTO dto);

    void delete(Long id);

    ExerciseResponseDTO addExercise(Long workoutId, ExerciseRequestDTO dto);

    ExerciseResponseDTO updateExercise(Long workoutId, Long exerciseId, ExerciseRequestDTO dto);

    void deleteExercise(Long workoutId, Long exerciseId);
}
