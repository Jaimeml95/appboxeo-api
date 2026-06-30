package com.boxing.api.controller.dto;

import com.boxing.api.model.CategoriaVideo;
import com.boxing.api.model.TipoVideo;

public class VideoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private TipoVideo tipo;
    private String url;
    private CategoriaVideo categoria;

    public VideoResponseDTO() {}

    public VideoResponseDTO(Long id, String titulo, String descripcion, TipoVideo tipo, String url, CategoriaVideo categoria) {
        this.id = id;
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
