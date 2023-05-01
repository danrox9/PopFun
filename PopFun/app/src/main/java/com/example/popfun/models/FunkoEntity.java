package com.example.popfun.models;

import java.sql.Timestamp;
import java.util.Date;

public class FunkoEntity implements Comparable<FunkoEntity>{

    public int compareTo(FunkoEntity other) {
        // Ordenar por fecha en orden descendente
        return other.fecha.compareTo(this.fecha);
    }

    private String imagenes;
    private String textos;
    private String descripcion;
    private String idUsuario;

    private Date fecha;

    public  FunkoEntity(){

    }
    public FunkoEntity(String imagenes, String textos, String descripcion,String idUsuario,Date fecha) {
        this.imagenes = imagenes;
        this.textos = textos;
        this.descripcion = descripcion;
        this.idUsuario = idUsuario;
        this.fecha = fecha;

    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getImagenes() {
        return imagenes;
    }

    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }

    public String getTextos() {
        return textos;
    }

    public void setTextos(String textos) {
        this.textos = textos;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }


}
