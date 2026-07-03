package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.ExerciseRequestDTO;
import com.boxing.api.controller.dto.ExerciseResponseDTO;
import com.boxing.api.controller.dto.WorkoutRequestDTO;
import com.boxing.api.controller.dto.WorkoutResponseDTO;
import com.boxing.api.model.Exercise;
import com.boxing.api.model.Workout;
import com.boxing.api.repository.ExerciseRepository;
import com.boxing.api.repository.WorkoutRepository;
import com.boxing.api.service.WorkoutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository,
                               ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutResponseDTO> getAll() {
        return workoutRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutResponseDTO getById(Long id) {
        return toResponse(findWorkoutById(id));
    }

    @Override
    @Transactional
    public WorkoutResponseDTO create(WorkoutRequestDTO dto) {
        Workout workout = new Workout(
                dto.getName(), dto.getDescription(), dto.getDifficulty(), dto.getEstimatedDuration()
        );
        return toResponse(workoutRepository.save(workout));
    }

    @Override
    @Transactional
    public WorkoutResponseDTO update(Long id, WorkoutRequestDTO dto) {
        Workout workout = findWorkoutById(id);
        workout.setName(dto.getName());
        workout.setDescription(dto.getDescription());
        workout.setDifficulty(dto.getDifficulty());
        workout.setEstimatedDuration(dto.getEstimatedDuration());
        return toResponse(workoutRepository.save(workout));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        workoutRepository.delete(findWorkoutById(id));
    }

    @Override
    @Transactional
    public ExerciseResponseDTO addExercise(Long workoutId, ExerciseRequestDTO dto) {
        Workout workout = findWorkoutById(workoutId);
        Exercise exercise = new Exercise(
                dto.getName(), dto.getDescription(), dto.getSets(), dto.getReps(), dto.getRest(), workout
        );
        return toExerciseResponse(exerciseRepository.save(exercise));
    }

    @Override
    @Transactional
    public ExerciseResponseDTO updateExercise(Long workoutId, Long exerciseId, ExerciseRequestDTO dto) {
        Exercise exercise = exerciseRepository.findByIdAndWorkoutId(exerciseId, workoutId)
                .orElseThrow(() -> new NoSuchElementException("Exercise not found"));
        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setSets(dto.getSets());
        exercise.setReps(dto.getReps());
        exercise.setRest(dto.getRest());
        return toExerciseResponse(exerciseRepository.save(exercise));
    }

    @Override
    @Transactional
    public void deleteExercise(Long workoutId, Long exerciseId) {
        Exercise exercise = exerciseRepository.findByIdAndWorkoutId(exerciseId, workoutId)
                .orElseThrow(() -> new NoSuchElementException("Exercise not found"));
        exerciseRepository.delete(exercise);
    }

    private Workout findWorkoutById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workout not found"));
    }

    private WorkoutResponseDTO toResponse(Workout w) {
        List<ExerciseResponseDTO> exercises = w.getExercises()
                .stream()
                .map(this::toExerciseResponse)
                .toList();
        return new WorkoutResponseDTO(w.getId(), w.getName(), w.getDescription(), w.getDifficulty(), w.getEstimatedDuration(), exercises);
    }

    private ExerciseResponseDTO toExerciseResponse(Exercise ex) {
        return new ExerciseResponseDTO(ex.getId(), ex.getName(), ex.getDescription(), ex.getSets(), ex.getReps(), ex.getRest());
    }
}
