package com.boxing.api.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConfiguracionCronometroRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "El número de rondas no puede ser nulo")
    @Min(value = 1, message = "Debe haber al menos 1 ronda")
    private Integer rondas;

    @NotNull(message = "La duración de ronda no puede ser nula")
    @Min(value = 1, message = "La duración de ronda debe ser al menos 1 segundo")
    private Integer duracionRonda;

    @NotNull(message = "El tiempo de descanso no puede ser nulo")
    @Min(value = 0, message = "El descanso no puede ser negativo")
    private Integer descanso;

    public ConfiguracionCronometroRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getRondas() { return rondas; }
    public void setRondas(Integer rondas) { this.rondas = rondas; }

    public Integer getDuracionRonda() { return duracionRonda; }
    public void setDuracionRonda(Integer duracionRonda) { this.duracionRonda = duracionRonda; }

    public Integer getDescanso() { return descanso; }
    public void setDescanso(Integer descanso) { this.descanso = descanso; }
}
