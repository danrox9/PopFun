package com.example.popfun.models;

public class FunkoEntity {

    private String imagenes;
    private String textos;

    public  FunkoEntity(){

    }
    public FunkoEntity(String imagenes, String textos) {
        this.imagenes = imagenes;
        this.textos = textos;
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


}
