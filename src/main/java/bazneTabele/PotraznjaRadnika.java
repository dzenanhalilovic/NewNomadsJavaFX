package com.example.newnomads;

import java.sql.Date;
import java.sql.Timestamp;

public class PotraznjaRadnika {

    private int idPotraznjeRadnika;
    private Timestamp datumKreiranjaPotraznje;
    private int drzavaId;
    private int idFirme;
    private Date krajnjiRok;
    private String naslovPotraznje;
    private String opisPotraznje;
    private String statusPotraznje; // 'aktivna' / 'neaktivna'

    public PotraznjaRadnika(int idPotraznjeRadnika,
                            Timestamp datumKreiranjaPotraznje,
                            int drzavaId,
                            int idFirme,
                            Date krajnjiRok,
                            String naslovPotraznje,
                            String opisPotraznje,
                            String statusPotraznje) {
        this.idPotraznjeRadnika = idPotraznjeRadnika;
        this.datumKreiranjaPotraznje = datumKreiranjaPotraznje;
        this.drzavaId = drzavaId;
        this.idFirme = idFirme;
        this.krajnjiRok = krajnjiRok;
        this.naslovPotraznje = naslovPotraznje;
        this.opisPotraznje = opisPotraznje;
        this.statusPotraznje = statusPotraznje;
    }

    public int getIdPotraznjeRadnika() { return idPotraznjeRadnika; }
    public Timestamp getDatumKreiranjaPotraznje() { return datumKreiranjaPotraznje; }
    public int getDrzavaId() { return drzavaId; }
    public int getIdFirme() { return idFirme; }
    public Date getKrajnjiRok() { return krajnjiRok; }
    public String getNaslovPotraznje() { return naslovPotraznje; }
    public String getOpisPotraznje() { return opisPotraznje; }
    public String getStatusPotraznje() { return statusPotraznje; }

    public void setStatusPotraznje(String statusPotraznje) { this.statusPotraznje = statusPotraznje; }

    @Override
    public String toString() {
        return naslovPotraznje + " (rok: " + krajnjiRok + ")";
    }
}
