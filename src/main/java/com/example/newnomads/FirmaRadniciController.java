package com.example.newnomads;

import bazneTabele.Radnik;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FirmaRadniciController {

    @FXML private TableView<Radnik> tableRadnici;
    @FXML private TableColumn<Radnik, String> colIme;
    @FXML private TableColumn<Radnik, String> colPrezime;
    @FXML private TableColumn<Radnik, String> colSpol;
    @FXML private TableColumn<Radnik, String> colDrzava; // Promijenjeno u String
    @FXML private TableColumn<Radnik, String> colGrana;
    @FXML private TableColumn<Radnik, String> colPasos;
    @FXML private TableColumn<Radnik, String> colViza;
    @FXML private TableColumn<Radnik, Void> colAkcija;

    @FXML
    public void initialize() {
        // Standardne kolone
        colIme.setCellValueFactory(new PropertyValueFactory<>("ime"));
        colPrezime.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        colSpol.setCellValueFactory(new PropertyValueFactory<>("spol"));

        // DRŽAVA (Raspakivanje iz spojene vrijednosti)
        colDrzava.setCellValueFactory(data -> {
            String s = data.getValue().getStatus();
            return new SimpleStringProperty((s != null && s.contains(" | ")) ? s.split(" \\| ")[0] : "N/A");
        });

        // GRANA (Raspakivanje iz spojene vrijednosti)
        colGrana.setCellValueFactory(data -> {
            String s = data.getValue().getStatus();
            return new SimpleStringProperty((s != null && s.contains(" | ")) ? s.split(" \\| ")[1] : "N/A");
        });

        // PASOŠ - Eksplicitno pretvaranje datuma u String
        colPasos.setCellValueFactory(data -> {
            Object d = data.getValue().getDoKadTrajePasos();
            return new SimpleStringProperty(d != null ? d.toString() : "N/A");
        });

        // VIZA - Eksplicitno pretvaranje datuma u String
        colViza.setCellValueFactory(data -> {
            Object d = data.getValue().getDoKadTrajeViza();
            return new SimpleStringProperty(d != null ? d.toString() : "N/A");
        });

        dodajDugmeZatrazi();
        učitajPodatke();
    }
    private void učitajPodatke() {
        // Umjesto starog poziva, koristimo lokalnu metodu koja radi JOIN sa nazivima
        ObservableList<Radnik> lista = FXCollections.observableArrayList(getSlobodniRadniciSaNazivima());
        tableRadnici.setItems(lista);
    }

    private List<Radnik> getSlobodniRadniciSaNazivima() {
        List<Radnik> radnici = new ArrayList<>();
        // Važno: Koristimo JOIN da dobijemo imena država i grana umjesto ID-eva
        String sql = """
        SELECT r.*, d.nazivDrzave, g.nazivGraneRada 
        FROM radnici r
        LEFT JOIN drzave d ON r.drzavaId = d.drzavaId
        LEFT JOIN granaRada g ON r.idGraneRada = g.idGraneRada
        WHERE r.nazivStatusa = 'slobodan'
    """;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String drzava = rs.getString("nazivDrzave") != null ? rs.getString("nazivDrzave") : "Nepoznato";
                String grana = rs.getString("nazivGraneRada") != null ? rs.getString("nazivGraneRada") : "Nepoznato";
                String spojeno = drzava + " | " + grana;

                // PAZI: Redoslijed u konstruktoru mora biti IDENTIČAN onome u Radnik.java
                Radnik r = new Radnik(
                        rs.getInt("idRadnika"),
                        rs.getString("brojPasosa"),
                        rs.getString("ime"),
                        rs.getString("prezime"),
                        rs.getInt("drzavaId"),
                        rs.getInt("idGraneRada"),
                        rs.getString("spol"),
                        rs.getDate("doKadTrajePasos"),
                        rs.getDate("doKadTrajeViza"),
                        rs.getDate("datumRodjenja"),
                        spojeno // Ovo privremeno čuvamo u polju 'nazivStatusa' ili 'status'
                );
                radnici.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return radnici;
    }
    private void dodajDugmeZatrazi() {
        colAkcija.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Radnik, Void> call(final TableColumn<Radnik, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Zatraži");
                    {
                        btn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            Radnik r = getTableView().getItems().get(getIndex());
                            otvoriDijalogZaZahtjev(r);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(btn);
                    }
                };
            }
        });
    }

    private void otvoriDijalogZaZahtjev(Radnik r) {
        int idFirme = Session.getIdFirme();
        List<String> mojePotraznje = dohvatiPotraznjeFirme(idFirme);

        if (mojePotraznje.isEmpty()) {
            prikaziAlert("Nema potražnji", "Morate prvo kreirati potražnju da biste zatražili radnika.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(mojePotraznje.get(0), mojePotraznje);
        dialog.setTitle("Zahtjev za radnika");
        dialog.setHeaderText("Angažovanje radnika: " + r.getIme() + " " + r.getPrezime());
        dialog.setContentText("Odaberite Vašu aktivnu potražnju:");

        dialog.showAndWait().ifPresent(odabrana -> {
            int idPotraznje = Integer.parseInt(odabrana.split(" - ")[0]);
            snimiZahtjevUBazu(r.getIdRadnika(), idFirme, idPotraznje);
        });
    }

    private void snimiZahtjevUBazu(int idRadnika, int idFirme, int idPotraznje) {
        // Dodali smo datumPocetkaRada u SQL upit
        String sql = """
        INSERT INTO ugovor (idFirme, idRadnika, statusUgovora, opis, datumKreiranja, drzavaRadaId, datumPocetkaRada) 
        VALUES (?, ?, 'naCekanju', ?, CURRENT_TIMESTAMP, 1, CURRENT_DATE)
    """;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFirme);
            stmt.setInt(2, idRadnika);
            stmt.setString(3, "Zahtjev firme preko potražnje br: " + idPotraznje);

            stmt.executeUpdate();
            prikaziAlert("Uspjeh", "Zahtjev je poslat regruteru na odobrenje.");
            učitajPodatke();

        } catch (Exception e) {
            e.printStackTrace();
            prikaziAlert("Greška", "Nije moguće poslati zahtjev: " + e.getMessage());
        }
    }

    private List<String> dohvatiPotraznjeFirme(int idFirme) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT idPotraznjeRadnika, naslovPotraznje FROM potraznjaRadnika WHERE idFirme = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFirme);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(rs.getInt("idPotraznjeRadnika") + " - " + rs.getString("naslovPotraznje"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    private void prikaziAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) tableRadnici.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/com/example/newnomads/firma.fxml"))));
        } catch (Exception e) { e.printStackTrace(); }
    }
}