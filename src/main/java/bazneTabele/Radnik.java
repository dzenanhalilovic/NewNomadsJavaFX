package com.example.newnomads;

import java.sql.Date;

public class Radnik {

    private int idRadnika;
    private String brojPasosa;
    private String ime;
    private String prezime;
    private int drzavaId;
    private int idGraneRada;
    private String spol;              // 'musko' ili 'zensko'
    private Date doKadTrajePasos;
    private Date doKadTrajeViza;      // moze biti null
    private Date datumRodjenja;
    private String nazivStatusa;      // enum vrijednosti iz baze

    public Radnik(int idRadnika,
                  String brojPasosa,
                  String ime,
                  String prezime,
                  int drzavaId,
                  int idGraneRada,
                  String spol,
                  Date doKadTrajePasos,
                  Date doKadTrajeViza,
                  Date datumRodjenja,
                  String nazivStatusa) {

        this.idRadnika = idRadnika;
        this.brojPasosa = brojPasosa;
        this.ime = ime;
        this.prezime = prezime;
        this.drzavaId = drzavaId;
        this.idGraneRada = idGraneRada;
        this.spol = spol;
        this.doKadTrajePasos = doKadTrajePasos;
        this.doKadTrajeViza = doKadTrajeViza;
        this.datumRodjenja = datumRodjenja;
        this.nazivStatusa = nazivStatusa;
    }

    public int getIdRadnika() { return idRadnika; }
    public String getBrojPasosa() { return brojPasosa; }
    public String getIme() { return ime; }
    public String getPrezime() { return prezime; }
    public int getDrzavaId() { return drzavaId; }
    public int getIdGraneRada() { return idGraneRada; }
    public String getSpol() { return spol; }
    public Date getDoKadTrajePasos() { return doKadTrajePasos; }
    public Date getDoKadTrajeViza() { return doKadTrajeViza; }
    public Date getDatumRodjenja() { return datumRodjenja; }
    public String getNazivStatusa() { return nazivStatusa; }

    public void setNazivStatusa(String nazivStatusa) { this.nazivStatusa = nazivStatusa; }

    @Override
    public String toString() {
        return ime + " " + prezime + " (" + brojPasosa + ")";
    }
}
