package com.boxing.api.controller.dto;

import com.boxing.api.model.Dificultad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EntrenamientoRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La dificultad no puede ser nula")
    private Dificultad dificultad;

    @NotNull(message = "La duración estimada no puede ser nula")
    @Min(value = 1, message = "La duración estimada debe ser al menos 1 minuto")
    private Integer duracionEstimada;

    public EntrenamientoRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Dificultad getDificultad() { return dificultad; }
    public void setDificultad(Dificultad dificultad) { this.dificultad = dificultad; }

    public Integer getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(Integer duracionEstimada) { this.duracionEstimada = duracionEstimada; }
}
