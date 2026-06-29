package com.boxing.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "configuraciones_cronometro")
public class ConfiguracionCronometro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "El número de rondas no puede ser nulo")
    @Min(value = 1, message = "Debe haber al menos 1 ronda")
    @Column(nullable = false)
    private Integer rondas;

    @NotNull(message = "La duración de ronda no puede ser nula")
    @Min(value = 1, message = "La duración de ronda debe ser al menos 1 segundo")
    @Column(name = "duracion_ronda", nullable = false)
    private Integer duracionRonda;

    @NotNull(message = "El tiempo de descanso no puede ser nulo")
    @Min(value = 0, message = "El descanso no puede ser negativo")
    @Column(nullable = false)
    private Integer descanso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public ConfiguracionCronometro() {}

    public ConfiguracionCronometro(String nombre, Integer rondas, Integer duracionRonda, Integer descanso, Usuario usuario) {
        this.nombre = nombre;
        this.rondas = rondas;
        this.duracionRonda = duracionRonda;
        this.descanso = descanso;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getRondas() { return rondas; }
    public void setRondas(Integer rondas) { this.rondas = rondas; }

    public Integer getDuracionRonda() { return duracionRonda; }
    public void setDuracionRonda(Integer duracionRonda) { this.duracionRonda = duracionRonda; }

    public Integer getDescanso() { return descanso; }
    public void setDescanso(Integer descanso) { this.descanso = descanso; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
