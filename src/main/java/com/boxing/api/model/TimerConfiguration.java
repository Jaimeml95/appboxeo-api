package com.boxing.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "timer_configurations")
public class TimerConfiguration {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Number of rounds must not be null")
    @Min(value = 1, message = "There must be at least 1 round")
    @Column(nullable = false)
    private Integer rounds;

    @NotNull(message = "Round duration must not be null")
    @Min(value = 1, message = "Round duration must be at least 1 second")
    @Column(name = "round_duration", nullable = false)
    private Integer roundDuration;

    @NotNull(message = "Rest time must not be null")
    @Min(value = 0, message = "Rest must not be negative")
    @Column(nullable = false)
    private Integer rest;

    @NotNull(message = "warnBeforeEnd must not be null")
    @Column(name = "warn_before_end", nullable = false)
    private Boolean warnBeforeEnd;

    @NotNull(message = "bellSound must not be null")
    @Column(name = "bell_sound", nullable = false)
    private Boolean bellSound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public TimerConfiguration() {}

    public TimerConfiguration(String name, Integer rounds, Integer roundDuration, Integer rest,
                               Boolean warnBeforeEnd, Boolean bellSound, User user) {
        this.name = name;
        this.rounds = rounds;
        this.roundDuration = roundDuration;
        this.rest = rest;
        this.warnBeforeEnd = warnBeforeEnd;
        this.bellSound = bellSound;
        this.user = user;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getRounds() { return rounds; }
    public void setRounds(Integer rounds) { this.rounds = rounds; }

    public Integer getRoundDuration() { return roundDuration; }
    public void setRoundDuration(Integer roundDuration) { this.roundDuration = roundDuration; }

    public Integer getRest() { return rest; }
    public void setRest(Integer rest) { this.rest = rest; }

    public Boolean getWarnBeforeEnd() { return warnBeforeEnd; }
    public void setWarnBeforeEnd(Boolean warnBeforeEnd) { this.warnBeforeEnd = warnBeforeEnd; }

    public Boolean getBellSound() { return bellSound; }
    public void setBellSound(Boolean bellSound) { this.bellSound = bellSound; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
