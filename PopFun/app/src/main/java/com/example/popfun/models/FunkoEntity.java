package com.example.popfun.models;

public class FunkoEntity {

    private String imagenes;
    private String textos;
    private String descripcion;
    private String idUsuario;

    public  FunkoEntity(){

    }
    public FunkoEntity(String imagenes, String textos, String descripcion,String idUsuario) {
        this.imagenes = imagenes;
        this.textos = textos;
        this.descripcion = descripcion;
        this.idUsuario = idUsuario;
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
