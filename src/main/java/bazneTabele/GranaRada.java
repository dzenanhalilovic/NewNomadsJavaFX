package com.example.newnomads;

public class GranaRada {
    private int idGraneRada;
    private String nazivGraneRada;

    public GranaRada(int idGraneRada, String nazivGraneRada) {
        this.idGraneRada = idGraneRada;
        this.nazivGraneRada = nazivGraneRada;
    }

    public int getIdGraneRada() { return idGraneRada; }
    public String getNazivGraneRada() { return nazivGraneRada; }

    @Override
    public String toString() {
        return nazivGraneRada;
    }
}
