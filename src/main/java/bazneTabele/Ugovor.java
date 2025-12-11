package com.example.newnomads;

import java.sql.Date;
import java.sql.Timestamp;

public class Ugovor {

    private int idUgovora;
    private int idFirme;
    private int idRadnika;
    private Timestamp datumKreiranja;
    private int drzavaRadaId;
    private Date datumPocetkaRada;
    private Date datumKrajaRada;
    private String statusUgovora;
    private String opis;

    public Ugovor(int idUgovora,
                  int idFirme,
                  int idRadnika,
                  Timestamp datumKreiranja,
                  int drzavaRadaId,
                  Date datumPocetkaRada,
                  Date datumKrajaRada,
                  String statusUgovora,
                  String opis) {

        this.idUgovora = idUgovora;
        this.idFirme = idFirme;
        this.idRadnika = idRadnika;
        this.datumKreiranja = datumKreiranja;
        this.drzavaRadaId = drzavaRadaId;
        this.datumPocetkaRada = datumPocetkaRada;
        this.datumKrajaRada = datumKrajaRada;
        this.statusUgovora = statusUgovora;
        this.opis = opis;
    }

    public int getIdUgovora() { return idUgovora; }
    public int getIdFirme() { return idFirme; }
    public int getIdRadnika() { return idRadnika; }
    public Timestamp getDatumKreiranja() { return datumKreiranja; }
    public int getDrzavaRadaId() { return drzavaRadaId; }
    public Date getDatumPocetkaRada() { return datumPocetkaRada; }
    public Date getDatumKrajaRada() { return datumKrajaRada; }
    public String getStatusUgovora() { return statusUgovora; }
    public String getOpis() { return opis; }

    public void setStatusUgovora(String statusUgovora) { this.statusUgovora = statusUgovora; }
    public void setOpis(String opis) { this.opis = opis; }
}
