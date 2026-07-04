package com.boxing.api.repository;

import com.boxing.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Query method used for local login lookups; Spring Data JPA
    // implements it automatically from the method name.
    Optional<User> findByEmail(String email);

    // Used to find or provision a user on Google Sign-In.
    Optional<User> findByGoogleId(String googleId);
}
