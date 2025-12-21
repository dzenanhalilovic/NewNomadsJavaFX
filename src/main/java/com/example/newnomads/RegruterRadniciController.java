package com.example.newnomads;

import bazneTabele.Radnik;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegruterRadniciController {

    @FXML private TableView<Radnik> radniciTable;
    @FXML private TableColumn<Radnik, String> imeCol, prezimeCol, drzavaCol, granaCol, statusCol;
    @FXML private TableColumn<Radnik, Void> ugovorCol; // Kolona za dugme
    @FXML private ComboBox<String> comboDrzava, comboGrana;
    @FXML private Button logout;

    private ObservableList<Radnik> radnici = FXCollections.observableArrayList();
    private int proslijedjenIdFirme = -1; // Služi za "Matchmaking"

    @FXML
    private void initialize() {
        // Povezivanje osnovnih kolona
        imeCol.setCellValueFactory(data -> data.getValue().imeProperty());
        prezimeCol.setCellValueFactory(data -> data.getValue().prezimeProperty());

        // Izvlačenje države i grane iz status stringa "Država | Grana"
        drzavaCol.setCellValueFactory(data -> {
            String status = data.getValue().getStatus();
            if (status != null && status.contains(" | ")) {
                return new SimpleStringProperty(status.split(" \\| ")[0]);
            }
            return new SimpleStringProperty("");
        });

        granaCol.setCellValueFactory(data -> {
            String status = data.getValue().getStatus();
            if (status != null && status.contains(" | ")) {
                return new SimpleStringProperty(status.split(" \\| ")[1]);
            }
            return new SimpleStringProperty("");
        });
        statusCol.setCellValueFactory(data -> {
            String s = data.getValue().getStatus();
            // Uzimamo treći dio stringa (index 2)
            if (s != null && s.split(" \\| ").length > 2) {
                return new SimpleStringProperty(s.split(" \\| ")[2]);
            }
            return new SimpleStringProperty("");
        });
        // INICIJALIZACIJA DUGMETA ZA UGOVOR
        dodajDugmeZaUgovor();

        loadCombos();
        loadRadnici(null, null); // Učitaj sve na početku
    }

    private void dodajDugmeZaUgovor() {
        ugovorCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Radnik, Void> call(final TableColumn<Radnik, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Sastavi ugovor");
                    {
                        btn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            Radnik r = getTableView().getItems().get(getIndex());
                            otvoriProzorZaUgovor(r);
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

    private void otvoriProzorZaUgovor(Radnik r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/dodajUgovor.fxml"));
            Scene scene = new Scene(loader.load());

            // Uzimamo kontroler prozora za dodavanje ugovora
            DodajUgovorController ctrl = loader.getController();

            // Proslijeđujemo selektovanog radnika i firmu (ako je došla iz potražnje)
            ctrl.setPodaci(r, proslijedjenIdFirme);

            Stage stage = new Stage();
            stage.setTitle("Novi ugovor: " + r.getIme() + " " + r.getPrezime());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filtrirajPoGrani(String nazivGrane, int idFirme) {
        this.proslijedjenIdFirme = idFirme; // Pamti firmu za koju tražimo radnika
        comboGrana.setValue(nazivGrane);
        loadRadnici(null, nazivGrane);
    }

    @FXML
    private void filterRadnici() {
        loadRadnici(comboDrzava.getValue(), comboGrana.getValue());
    }

    @FXML
    private void resetFilters() {
        comboDrzava.setValue(null);
        comboGrana.setValue(null);
        loadRadnici(null, null);
    }

    private void loadRadnici(String drzavaFilter, String granaFilter) {
        try (Connection conn = DB.getConnection()) {
            // Dodali smo r.nazivStatusa u SELECT dio
            String sql = """
            SELECT r.idRadnika, r.ime, r.prezime, r.nazivStatusa, 
                   d.nazivDrzave AS drzava, g.nazivGraneRada AS grana
            FROM radnici r
            LEFT JOIN drzave d ON r.drzavaId = d.drzavaId
            LEFT JOIN granaRada g ON r.idGraneRada = g.idGraneRada
            WHERE (? IS NULL OR d.nazivDrzave = ?)
              AND (? IS NULL OR g.nazivGraneRada = ?)
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, drzavaFilter);
            stmt.setString(2, drzavaFilter);
            stmt.setString(3, granaFilter);
            stmt.setString(4, granaFilter);

            ResultSet rs = stmt.executeQuery();
            radnici.clear();

            while (rs.next()) {
                String d = rs.getString("drzava") != null ? rs.getString("drzava") : "N/A";
                String g = rs.getString("grana") != null ? rs.getString("grana") : "Neregulisan";
                String s = rs.getString("nazivStatusa") != null ? rs.getString("nazivStatusa") : "slobodan";

                // Pakujemo Državu, Granu i Status u jedan string razdvojen sa " | "
                // Ovo omogućava onom tvom split-u u initialize() da radi za sve 3 kolone
                String spojeniPodaci = d + " | " + g + " | " + s;

                Radnik r = new Radnik(
                        rs.getInt("idRadnika"),
                        "", // brojPasosa
                        rs.getString("ime"),
                        rs.getString("prezime"),
                        0, 0, "", null, null, null,
                        spojeniPodaci // Ovo ide u status polje objekta Radnik
                );
                radnici.add(r);
            }
            radniciTable.setItems(radnici);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadCombos() {
        try (Connection conn = DB.getConnection()) {
            comboDrzava.getItems().clear();
            ResultSet rsDrz = conn.prepareStatement("SELECT nazivDrzave FROM drzave").executeQuery();
            while (rsDrz.next()) comboDrzava.getItems().add(rsDrz.getString("nazivDrzave"));

            comboGrana.getItems().clear();
            ResultSet rsGrana = conn.prepareStatement("SELECT nazivGraneRada FROM granaRada").executeQuery();
            while (rsGrana.next()) comboGrana.getItems().add(rsGrana.getString("nazivGraneRada"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) radniciTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/regruter.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) { e.printStackTrace(); }
    }
}