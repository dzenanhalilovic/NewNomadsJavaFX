package com.example.newnomads;

public class Regruter {

    private int regruterId;
    private String ime;
    private String mail;
    private String lozinka;
    private boolean potvrdjen;

    public Regruter(int regruterId, String ime, String mail, String lozinka, boolean potvrdjen) {
        this.regruterId = regruterId;
        this.ime = ime;
        this.mail = mail;
        this.lozinka = lozinka;
        this.potvrdjen = potvrdjen;
    }

    public int getRegruterId() { return regruterId; }
    public String getIme() { return ime; }
    public String getMail() { return mail; }
    public String getLozinka() { return lozinka; }
    public boolean isPotvrdjen() { return potvrdjen; }

    public void setLozinka(String lozinka) { this.lozinka = lozinka; }
    public void setPotvrdjen(boolean potvrdjen) { this.potvrdjen = potvrdjen; }
}
