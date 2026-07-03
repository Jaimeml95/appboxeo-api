package com.boxing.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "timer_configurations")
public class TimerConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public TimerConfiguration() {}

    public TimerConfiguration(String name, Integer rounds, Integer roundDuration, Integer rest, User user) {
        this.name = name;
        this.rounds = rounds;
        this.roundDuration = roundDuration;
        this.rest = rest;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getRounds() { return rounds; }
    public void setRounds(Integer rounds) { this.rounds = rounds; }

    public Integer getRoundDuration() { return roundDuration; }
    public void setRoundDuration(Integer roundDuration) { this.roundDuration = roundDuration; }

    public Integer getRest() { return rest; }
    public void setRest(Integer rest) { this.rest = rest; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
