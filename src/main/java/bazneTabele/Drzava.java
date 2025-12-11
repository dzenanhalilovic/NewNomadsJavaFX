package com.example.newnomads;

public class Drzava {
    private int drzavaId;
    private String nazivDrzave;

    public Drzava(int drzavaId, String nazivDrzave) {
        this.drzavaId = drzavaId;
        this.nazivDrzave = nazivDrzave;
    }

    public int getDrzavaId() { return drzavaId; }
    public String getNazivDrzave() { return nazivDrzave; }

    @Override
    public String toString() {
        return nazivDrzave; // bitno zbog prikaza u ComboBox/Pickere
    }
}
