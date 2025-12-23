package bazneTabele;

import javafx.beans.property.*;
import java.sql.Date;
import java.sql.Timestamp;

public class Ugovor {

    private final IntegerProperty idUgovora;
    private final IntegerProperty idFirme;
    private final IntegerProperty idRadnika;
    private final ObjectProperty<Timestamp> datumKreiranja;
    private final IntegerProperty drzavaRadaId;
    private final ObjectProperty<Date> datumPocetkaRada;
    private final ObjectProperty<Date> datumKrajaRada;
    private final StringProperty statusUgovora;
    private final StringProperty opis;
    private final StringProperty radnik;   // ime radnika
    private final StringProperty firma;    // ime firme

    // NOVO: id potražnje radnika
    private final IntegerProperty idPotraznjeRadnika;

    public Ugovor(int idUgovora,
                  int idFirme,
                  int idRadnika,
                  Timestamp datumKreiranja,
                  int drzavaRadaId,
                  Date datumPocetkaRada,
                  Date datumKrajaRada,
                  String statusUgovora,
                  String opis,
                  String radnik,
                  String firma,
                  int idPotraznjeRadnika) {

        this.idUgovora = new SimpleIntegerProperty(idUgovora);
        this.idFirme = new SimpleIntegerProperty(idFirme);
        this.idRadnika = new SimpleIntegerProperty(idRadnika);
        this.datumKreiranja = new SimpleObjectProperty<>(datumKreiranja);
        this.drzavaRadaId = new SimpleIntegerProperty(drzavaRadaId);
        this.datumPocetkaRada = new SimpleObjectProperty<>(datumPocetkaRada);
        this.datumKrajaRada = new SimpleObjectProperty<>(datumKrajaRada);
        this.statusUgovora = new SimpleStringProperty(statusUgovora != null ? statusUgovora : "");
        this.opis = new SimpleStringProperty(opis != null ? opis : "");
        this.radnik = new SimpleStringProperty(radnik != null ? radnik : "");
        this.firma = new SimpleStringProperty(firma != null ? firma : "");
        this.idPotraznjeRadnika = new SimpleIntegerProperty(idPotraznjeRadnika);
    }

    // Property metode
    public IntegerProperty idUgovoraProperty() { return idUgovora; }
    public IntegerProperty idFirmeProperty() { return idFirme; }
    public IntegerProperty idRadnikaProperty() { return idRadnika; }
    public ObjectProperty<Timestamp> datumKreiranjaProperty() { return datumKreiranja; }
    public IntegerProperty drzavaRadaIdProperty() { return drzavaRadaId; }
    public ObjectProperty<Date> datumPocetkaRadaProperty() { return datumPocetkaRada; }
    public ObjectProperty<Date> datumKrajaRadaProperty() { return datumKrajaRada; }
    public StringProperty statusUgovoraProperty() { return statusUgovora; }
    public StringProperty opisProperty() { return opis; }
    public StringProperty radnikProperty() { return radnik; }
    public StringProperty firmaProperty() { return firma; }
    public IntegerProperty idPotraznjeRadnikaProperty() { return idPotraznjeRadnika; }

    // Getteri
    public int getIdUgovora() { return idUgovora.get(); }
    public int getIdFirme() { return idFirme.get(); }
    public int getIdRadnika() { return idRadnika.get(); }
    public Timestamp getDatumKreiranja() { return datumKreiranja.get(); }
    public int getDrzavaRadaId() { return drzavaRadaId.get(); }
    public Date getDatumPocetkaRada() { return datumPocetkaRada.get(); }
    public Date getDatumKrajaRada() { return datumKrajaRada.get(); }
    public String getStatusUgovora() { return statusUgovora.get(); }
    public String getOpis() { return opis.get(); }
    public String getRadnik() { return radnik.get(); }
    public String getFirma() { return firma.get(); }
    public int getIdPotraznjeRadnika() { return idPotraznjeRadnika.get(); }

    // Setteri
    public void setStatusUgovora(String statusUgovora) { this.statusUgovora.set(statusUgovora); }
    public void setOpis(String opis) { this.opis.set(opis); }
    public void setIdPotraznjeRadnika(int id) { this.idPotraznjeRadnika.set(id); }

    @Override
    public String toString() {
        return "Ugovor " + idUgovora.get() + " (Radnik: " + radnik.get() + ", Firma: " + firma.get() + ")";
    }
}
