package com.example.newnomads;

public class FirmeKlijent {

    private int regruterId;
    private String imeFirme;
    private int idGraneRada;
    private String mail;
    private String lozinka;
    private String oFirmi;
    private String vlasnikFirme;
    private String brojTelefona;
    private String drugiKontakt;
    private int drzavaId;

    public FirmeKlijent(int regruterId, String imeFirme, int idGraneRada, String mail,
                        String lozinka, String oFirmi, String vlasnikFirme,
                        String brojTelefona, String drugiKontakt, int drzavaId) {
        this.regruterId = regruterId;
        this.imeFirme = imeFirme;
        this.idGraneRada = idGraneRada;
        this.mail = mail;
        this.lozinka = lozinka;
        this.oFirmi = oFirmi;
        this.vlasnikFirme = vlasnikFirme;
        this.brojTelefona = brojTelefona;
        this.drugiKontakt = drugiKontakt;
        this.drzavaId = drzavaId;
    }

    public int getRegruterId() { return regruterId; }
    public String getImeFirme() { return imeFirme; }
    public int getIdGraneRada() { return idGraneRada; }
    public String getMail() { return mail; }
    public String getLozinka() { return lozinka; }
    public String getOFirmi() { return oFirmi; }
    public String getVlasnikFirme() { return vlasnikFirme; }
    public String getBrojTelefona() { return brojTelefona; }
    public String getDrugiKontakt() { return drugiKontakt; }
    public int getDrzavaId() { return drzavaId; }

    public void setLozinka(String lozinka) { this.lozinka = lozinka; }
    public void setImeFirme(String imeFirme) { this.imeFirme = imeFirme; }
    public void setOFirmi(String oFirmi) { this.oFirmi = oFirmi; }
    public void setVlasnikFirme(String vlasnikFirme) { this.vlasnikFirme = vlasnikFirme; }
    public void setBrojTelefona(String brojTelefona) { this.brojTelefona = brojTelefona; }
    public void setDrugiKontakt(String drugiKontakt) { this.drugiKontakt = drugiKontakt; }
    public void setDrzavaId(int drzavaId) { this.drzavaId = drzavaId; }
    public void setIdGraneRada(int idGraneRada) { this.idGraneRada = idGraneRada; }
}
