package com.boxing.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El tipo no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVideo tipo;

    @NotBlank(message = "La URL no puede estar vacía")
    @Size(max = 500, message = "La URL no puede superar los 500 caracteres")
    @Column(nullable = false, length = 500)
    private String url;

    @NotNull(message = "La categoría no puede ser nula")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaVideo categoria;

    public Video() {}

    public Video(String titulo, String descripcion, TipoVideo tipo, String url, CategoriaVideo categoria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.url = url;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoVideo getTipo() { return tipo; }
    public void setTipo(TipoVideo tipo) { this.tipo = tipo; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public CategoriaVideo getCategoria() { return categoria; }
    public void setCategoria(CategoriaVideo categoria) { this.categoria = categoria; }
}
