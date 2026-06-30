package com.boxing.api.controller.dto;

import com.boxing.api.model.CategoriaVideo;
import com.boxing.api.model.TipoVideo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VideoRequestDTO {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    private String titulo;

    private String descripcion;

    @NotNull(message = "El tipo no puede ser nulo")
    private TipoVideo tipo;

    @NotBlank(message = "La URL no puede estar vacía")
    @Size(max = 500, message = "La URL no puede superar los 500 caracteres")
    private String url;

    @NotNull(message = "La categoría no puede ser nula")
    private CategoriaVideo categoria;

    public VideoRequestDTO() {}

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
