package com.boxing.api.service;

import com.boxing.api.controller.dto.ExerciseRequestDTO;
import com.boxing.api.controller.dto.ExerciseResponseDTO;
import com.boxing.api.controller.dto.WorkoutRequestDTO;
import com.boxing.api.controller.dto.WorkoutResponseDTO;

import java.util.List;
import java.util.UUID;

public interface WorkoutService {

    List<WorkoutResponseDTO> getAll();

    WorkoutResponseDTO getById(UUID id);

    WorkoutResponseDTO create(WorkoutRequestDTO dto);

    WorkoutResponseDTO update(UUID id, WorkoutRequestDTO dto);

    void delete(UUID id);

    ExerciseResponseDTO addExercise(UUID workoutId, ExerciseRequestDTO dto);

    ExerciseResponseDTO updateExercise(UUID workoutId, UUID exerciseId, ExerciseRequestDTO dto);

    void deleteExercise(UUID workoutId, UUID exerciseId);
}
