package com.boxing.api.controller.dto;

import java.util.UUID;

public class TimerConfigurationResponseDTO {

    private UUID id;
    private String name;
    private Integer rounds;
    private Integer roundDuration;
    private Integer rest;

    public TimerConfigurationResponseDTO() {}

    public TimerConfigurationResponseDTO(UUID id, String name, Integer rounds, Integer roundDuration, Integer rest) {
        this.id = id;
        this.name = name;
        this.rounds = rounds;
        this.roundDuration = roundDuration;
        this.rest = rest;
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
}
