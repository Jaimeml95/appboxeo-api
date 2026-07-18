ALTER TABLE timer_configurations
    ADD COLUMN warn_before_end BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN bell_sound BOOLEAN NOT NULL DEFAULT true;
