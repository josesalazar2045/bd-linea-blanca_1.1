package com.example.application.data.entity;

import jakarta.persistence.Entity;

@Entity
public class LineaBlanca extends AbstractEntity {

    private String modelo;
    private Integer cant_existencias;
    private Integer precio_unitario;
    private String ubicacion;
    private String estado;

    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public Integer getCant_existencias() {
        return cant_existencias;
    }
    public void setCant_existencias(Integer cant_existencias) {
        this.cant_existencias = cant_existencias;
    }
    public Integer getPrecio_unitario() {
        return precio_unitario;
    }
    public void setPrecio_unitario(Integer precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

}
