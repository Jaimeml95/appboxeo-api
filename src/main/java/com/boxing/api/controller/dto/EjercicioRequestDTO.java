package com.boxing.api.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EjercicioRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "Las series no pueden ser nulas")
    @Min(value = 1, message = "Debe haber al menos 1 serie")
    private Integer series;

    @NotNull(message = "Las repeticiones no pueden ser nulas")
    @Min(value = 1, message = "Debe haber al menos 1 repetición")
    private Integer repeticiones;

    @NotNull(message = "El descanso no puede ser nulo")
    @Min(value = 0, message = "El descanso no puede ser negativo")
    private Integer descanso;

    public EjercicioRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    public Integer getDescanso() { return descanso; }
    public void setDescanso(Integer descanso) { this.descanso = descanso; }
}
