package com.boxing.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "Las series no pueden ser nulas")
    @Min(value = 1, message = "Debe haber al menos 1 serie")
    @Column(nullable = false)
    private Integer series;

    @NotNull(message = "Las repeticiones no pueden ser nulas")
    @Min(value = 1, message = "Debe haber al menos 1 repetición")
    @Column(nullable = false)
    private Integer repeticiones;

    @NotNull(message = "El descanso no puede ser nulo")
    @Min(value = 0, message = "El descanso no puede ser negativo")
    @Column(nullable = false)
    private Integer descanso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenamiento_id", nullable = false)
    private Entrenamiento entrenamiento;

    public Ejercicio() {}

    public Ejercicio(String nombre, String descripcion, Integer series, Integer repeticiones, Integer descanso, Entrenamiento entrenamiento) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.series = series;
        this.repeticiones = repeticiones;
        this.descanso = descanso;
        this.entrenamiento = entrenamiento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Entrenamiento getEntrenamiento() { return entrenamiento; }
    public void setEntrenamiento(Entrenamiento entrenamiento) { this.entrenamiento = entrenamiento; }
}
