package com.example.newnomads;

public class User {

    private String ime;          // ime ili ime firme
    private String email;
    private String password;
    private String role;         // "regruter" ili "firma"

    // dodatna polja za firmu
    private String brojTelefona;
    private String vlasnikFirme;
    private String oFirmi;

    public User(String ime, String email, String password, String role) {
        this.ime = ime;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // GETTERI I SETTERI
    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public String getVlasnikFirme() {
        return vlasnikFirme;
    }

    public void setVlasnikFirme(String vlasnikFirme) {
        this.vlasnikFirme = vlasnikFirme;
    }

    public String getoFirmi() {
        return oFirmi;
    }

    public void setoFirmi(String oFirmi) {
        this.oFirmi = oFirmi;
    }
}
