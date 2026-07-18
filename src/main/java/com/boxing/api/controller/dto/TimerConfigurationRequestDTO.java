package com.boxing.api.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TimerConfigurationRequestDTO {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Number of rounds must not be null")
    @Min(value = 1, message = "There must be at least 1 round")
    private Integer rounds;

    @NotNull(message = "Round duration must not be null")
    @Min(value = 1, message = "Round duration must be at least 1 second")
    private Integer roundDuration;

    @NotNull(message = "Rest time must not be null")
    @Min(value = 0, message = "Rest must not be negative")
    private Integer rest;

    @NotNull(message = "warnBeforeEnd must not be null")
    private Boolean warnBeforeEnd;

    @NotNull(message = "bellSound must not be null")
    private Boolean bellSound;

    public TimerConfigurationRequestDTO() {}

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
}
