package com.boxing.api.controller.dto;

public class ConfiguracionCronometroResponseDTO {

    private Long id;
    private String nombre;
    private Integer rondas;
    private Integer duracionRonda;
    private Integer descanso;

    public ConfiguracionCronometroResponseDTO() {}

    public ConfiguracionCronometroResponseDTO(Long id, String nombre, Integer rondas, Integer duracionRonda, Integer descanso) {
        this.id = id;
        this.nombre = nombre;
        this.rondas = rondas;
        this.duracionRonda = duracionRonda;
        this.descanso = descanso;
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
}
