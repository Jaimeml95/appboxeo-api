package com.boxing.api.service;

import com.boxing.api.controller.dto.ExerciseRequestDTO;
import com.boxing.api.controller.dto.ExerciseResponseDTO;
import com.boxing.api.controller.dto.WorkoutRequestDTO;
import com.boxing.api.controller.dto.WorkoutResponseDTO;
import com.boxing.api.model.Difficulty;
import com.boxing.api.model.Exercise;
import com.boxing.api.model.Workout;
import com.boxing.api.repository.ExerciseRepository;
import com.boxing.api.repository.WorkoutRepository;
import com.boxing.api.service.implementation.WorkoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceImplTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    private Workout workout;
    private WorkoutRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        workout = new Workout("Basic Cardio", "Cardio session", Difficulty.BEGINNER, 30);
        workout.setId(1L);

        requestDTO = new WorkoutRequestDTO();
        requestDTO.setName("Basic Cardio");
        requestDTO.setDescription("Cardio session");
        requestDTO.setDifficulty(Difficulty.BEGINNER);
        requestDTO.setEstimatedDuration(30);
    }

    @Test
    void getAll_returnsListOfWorkouts() {
        when(workoutRepository.findAll()).thenReturn(List.of(workout));

        List<WorkoutResponseDTO> result = workoutService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Basic Cardio");
        assertThat(result.get(0).getDifficulty()).isEqualTo(Difficulty.BEGINNER);
    }

    @Test
    void getAll_emptyList_returnsEmptyList() {
        when(workoutRepository.findAll()).thenReturn(List.of());

        List<WorkoutResponseDTO> result = workoutService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_existing_returnsWorkout() {
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));

        WorkoutResponseDTO result = workoutService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Basic Cardio");
        assertThat(result.getEstimatedDuration()).isEqualTo(30);
    }

    @Test
    void getById_nonExisting_throwsException() {
        when(workoutRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.getById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Workout not found");
    }

    @Test
    void create_savesAndReturnsWorkout() {
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);

        WorkoutResponseDTO result = workoutService.create(requestDTO);

        assertThat(result.getName()).isEqualTo("Basic Cardio");
        assertThat(result.getDifficulty()).isEqualTo(Difficulty.BEGINNER);
        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    void update_existing_updatesFields() {
        WorkoutRequestDTO updatedDTO = new WorkoutRequestDTO();
        updatedDTO.setName("Advanced Strength");
        updatedDTO.setDescription("Strength session");
        updatedDTO.setDifficulty(Difficulty.ADVANCED);
        updatedDTO.setEstimatedDuration(60);

        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any(Workout.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkoutResponseDTO result = workoutService.update(1L, updatedDTO);

        assertThat(result.getName()).isEqualTo("Advanced Strength");
        assertThat(result.getDifficulty()).isEqualTo(Difficulty.ADVANCED);
        assertThat(result.getEstimatedDuration()).isEqualTo(60);
    }

    @Test
    void update_nonExisting_throwsException() {
        when(workoutRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.update(99L, requestDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Workout not found");
    }

    @Test
    void delete_existing_deletesWorkout() {
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));

        workoutService.delete(1L);

        verify(workoutRepository, times(1)).delete(workout);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(workoutRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Workout not found");
    }

    @Test
    void addExercise_existing_savesAndReturnsExercise() {
        ExerciseRequestDTO exerciseDTO = new ExerciseRequestDTO();
        exerciseDTO.setName("Squats");
        exerciseDTO.setDescription("Weighted squats");
        exerciseDTO.setSets(4);
        exerciseDTO.setReps(12);
        exerciseDTO.setRest(60);

        Exercise exercise = new Exercise("Squats", "Weighted squats", 4, 12, 60, workout);
        exercise.setId(1L);

        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        ExerciseResponseDTO result = workoutService.addExercise(1L, exerciseDTO);

        assertThat(result.getName()).isEqualTo("Squats");
        assertThat(result.getSets()).isEqualTo(4);
        verify(exerciseRepository, times(1)).save(any(Exercise.class));
    }

    @Test
    void addExercise_workoutNotExisting_throwsException() {
        when(workoutRepository.findById(99L)).thenReturn(Optional.empty());

        ExerciseRequestDTO exerciseDTO = new ExerciseRequestDTO();

        assertThatThrownBy(() -> workoutService.addExercise(99L, exerciseDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Workout not found");
    }

    @Test
    void deleteExercise_nonExisting_throwsException() {
        when(exerciseRepository.findByIdAndWorkoutId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.deleteExercise(1L, 99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Exercise not found");
    }
}
