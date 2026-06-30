package com.boxing.api.controller.dto;

import com.boxing.api.model.Dificultad;

import java.util.List;

public class EntrenamientoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Dificultad dificultad;
    private Integer duracionEstimada;
    private List<EjercicioResponseDTO> ejercicios;

    public EntrenamientoResponseDTO() {}

    public EntrenamientoResponseDTO(Long id, String nombre, String descripcion, Dificultad dificultad, Integer duracionEstimada, List<EjercicioResponseDTO> ejercicios) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.dificultad = dificultad;
        this.duracionEstimada = duracionEstimada;
        this.ejercicios = ejercicios;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Dificultad getDificultad() { return dificultad; }
    public void setDificultad(Dificultad dificultad) { this.dificultad = dificultad; }

    public Integer getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(Integer duracionEstimada) { this.duracionEstimada = duracionEstimada; }

    public List<EjercicioResponseDTO> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<EjercicioResponseDTO> ejercicios) { this.ejercicios = ejercicios; }
}
