-- Baseline schema, matching the state produced by Hibernate ddl-auto=update
-- at the point this project switched to Flyway-managed migrations.

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255),
    google_id VARCHAR(255),
    picture_url VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_google_id UNIQUE (google_id),
    CONSTRAINT users_role_check CHECK (role IN ('BOXER', 'ADMIN'))
);

CREATE TABLE workouts (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    difficulty VARCHAR(20) NOT NULL,
    estimated_duration INTEGER NOT NULL,
    CONSTRAINT workouts_difficulty_check CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    CONSTRAINT workouts_estimated_duration_check CHECK (estimated_duration >= 1)
);

CREATE TABLE exercises (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    sets INTEGER NOT NULL,
    reps INTEGER NOT NULL,
    rest INTEGER NOT NULL,
    workout_id UUID NOT NULL,
    CONSTRAINT fk_exercises_workout FOREIGN KEY (workout_id) REFERENCES workouts (id),
    CONSTRAINT exercises_sets_check CHECK (sets >= 1),
    CONSTRAINT exercises_reps_check CHECK (reps >= 1),
    CONSTRAINT exercises_rest_check CHECK (rest >= 0)
);

CREATE TABLE videos (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    url VARCHAR(500) NOT NULL,
    category VARCHAR(20) NOT NULL,
    CONSTRAINT videos_type_check CHECK (type IN ('YOUTUBE', 'OWN')),
    CONSTRAINT videos_category_check CHECK (category IN ('TECHNIQUE', 'STRENGTH', 'CARDIO', 'NUTRITION'))
);

CREATE TABLE timer_configurations (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    rounds INTEGER NOT NULL,
    round_duration INTEGER NOT NULL,
    rest INTEGER NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_timer_configurations_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT timer_configurations_rounds_check CHECK (rounds >= 1),
    CONSTRAINT timer_configurations_round_duration_check CHECK (round_duration >= 1),
    CONSTRAINT timer_configurations_rest_check CHECK (rest >= 0)
);
