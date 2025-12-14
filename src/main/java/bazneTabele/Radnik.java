package bazneTabele;

import javafx.beans.property.*;

import java.sql.Date;

public class Radnik {

    private final IntegerProperty idRadnika;
    private final StringProperty brojPasosa;
    private final StringProperty ime;
    private final StringProperty prezime;
    private final IntegerProperty drzavaId;
    private final IntegerProperty granaId; // ovo je grana
    private final StringProperty spol;
    private final ObjectProperty<Date> datumRodjenja;
    private final ObjectProperty<Date> doKadTrajePasos;
    private final ObjectProperty<Date> doKadTrajeViza;
    private final StringProperty status;

    public Radnik(int idRadnika, String brojPasosa, String ime, String prezime,
                  int drzavaId, int granaId, String spol,
                  Date datumRodjenja, Date doKadTrajePasos, Date doKadTrajeViza,
                  String status) {

        this.idRadnika = new SimpleIntegerProperty(idRadnika);
        this.brojPasosa = new SimpleStringProperty(brojPasosa);
        this.ime = new SimpleStringProperty(ime);
        this.prezime = new SimpleStringProperty(prezime);
        this.drzavaId = new SimpleIntegerProperty(drzavaId);
        this.granaId = new SimpleIntegerProperty(granaId); // grana
        this.spol = new SimpleStringProperty(spol);
        this.datumRodjenja = new SimpleObjectProperty<>(datumRodjenja);
        this.doKadTrajePasos = new SimpleObjectProperty<>(doKadTrajePasos);
        this.doKadTrajeViza = new SimpleObjectProperty<>(doKadTrajeViza);
        this.status = new SimpleStringProperty(status != null ? status : "");
    }

    // Property metode
    public IntegerProperty idRadnikaProperty() { return idRadnika; }
    public StringProperty brojPasosaProperty() { return brojPasosa; }
    public StringProperty imeProperty() { return ime; }
    public StringProperty prezimeProperty() { return prezime; }
    public IntegerProperty drzavaIdProperty() { return drzavaId; }
    public IntegerProperty granaProperty() { return granaId; } // ovo je ključno
    public StringProperty spolProperty() { return spol; }
    public ObjectProperty<Date> datumRodjenjaProperty() { return datumRodjenja; }
    public ObjectProperty<Date> doKadTrajePasosProperty() { return doKadTrajePasos; }
    public ObjectProperty<Date> doKadTrajeVizaProperty() { return doKadTrajeViza; }
    public StringProperty statusProperty() { return status; }

    // Getteri
    public int getIdRadnika() { return idRadnika.get(); }
    public String getBrojPasosa() { return brojPasosa.get(); }
    public String getIme() { return ime.get(); }
    public String getPrezime() { return prezime.get(); }
    public int getDrzavaId() { return drzavaId.get(); }

    public int getGrana() { return granaId.get(); }
    public String getSpol() { return spol.get(); }
    public Date getDatumRodjenja() { return datumRodjenja.get(); }
    public Date getDoKadTrajePasos() { return doKadTrajePasos.get(); }
    public Date getDoKadTrajeViza() { return doKadTrajeViza.get(); }
    public String getStatus() { return status.get(); }

    // Setteri
    public void setGrana(int grana) { this.granaId.set(grana); }
    public void setStatus(String status) { this.status.set(status); }

    @Override
    public String toString() {
        return ime.get() + " " + prezime.get();
    }
}
