package com.boxing.api.repository;

import com.boxing.api.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
}
