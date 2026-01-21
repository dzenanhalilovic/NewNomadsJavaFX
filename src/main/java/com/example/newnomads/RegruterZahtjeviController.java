package com.example.newnomads;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegruterZahtjeviController {
    @FXML private TableView<ZahtjevRed> tableZahtjevi;
    @FXML private TableColumn<ZahtjevRed, String> colFirma, colRadnik, colOpis;
    @FXML private TableColumn<ZahtjevRed, Void> colAkcija;

    @FXML
    public void initialize() {
        colFirma.setCellValueFactory(d -> d.getValue().firma);
        colRadnik.setCellValueFactory(d -> d.getValue().radnik);
        colOpis.setCellValueFactory(d -> d.getValue().opis);

        dodajDugmad();
        loadData();
    }

    private void loadData() {
        ObservableList<ZahtjevRed> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT u.idUgovora, f.imeFirme, r.ime, r.prezime, u.opis, u.idRadnika 
            FROM ugovor u 
            JOIN firmeKlijenti f ON u.idFirme = f.idFirme 
            JOIN radnici r ON u.idRadnika = r.idRadnika 
            WHERE u.statusUgovora = 'naCekanju'
            """;
        try (Connection conn = DB.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while(rs.next()) {
                lista.add(new ZahtjevRed(
                        rs.getInt("idUgovora"),
                        rs.getString("imeFirme"),
                        rs.getString("ime") + " " + rs.getString("prezime"),
                        rs.getString("opis"),
                        rs.getInt("idRadnika")
                ));
            }
            tableZahtjevi.setItems(lista);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void dodajDugmad() {
        colAkcija.setCellFactory(param -> new TableCell<>() {
            private final Button btnOk = new Button("✔ Odobri");
            private final Button btnNo = new Button("✘ Odbij");
            private final HBox box = new HBox(15, btnOk, btnNo); // Povećan razmak između dugmadi na 15
            {
                // PADDING I PORAVNANJE ZA HBOX
                box.setAlignment(Pos.CENTER);
                box.setPadding(new Insets(5, 10, 5, 10)); // Gornji, desni, donji, lijevi padding

                btnOk.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;");
                btnNo.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;");

                btnOk.setOnAction(e -> obradi(getTableView().getItems().get(getIndex()), "aktivan", "zaposlen"));
                btnNo.setOnAction(e -> obradi(getTableView().getItems().get(getIndex()), "odbijen", "slobodan"));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                    // Centriranje same ćelije
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void obradi(ZahtjevRed z, String statusUgovora, String statusRadnika) {
        try (Connection conn = DB.getConnection()) {
            // 1. Ažuriraj ugovor
            PreparedStatement st1 = conn.prepareStatement("UPDATE ugovor SET statusUgovora = ? WHERE idUgovora = ?");
            st1.setString(1, statusUgovora);
            st1.setInt(2, z.id);
            st1.executeUpdate();

            // 2. Ažuriraj status radnika
            PreparedStatement st2 = conn.prepareStatement("UPDATE radnici SET nazivStatusa = ? WHERE idRadnika = ?");
            st2.setString(1, statusRadnika);
            st2.setInt(2, z.idRadnika);
            st2.executeUpdate();

            loadData(); // Osvježi tabelu nakon akcije
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * POPRAVLJENA METODA ZA POVRATAK
     */
    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/regruter.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Regruter Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Pomoćna klasa za tabelu
    public static class ZahtjevRed {
        int id, idRadnika;
        SimpleStringProperty firma, radnik, opis;
        public ZahtjevRed(int id, String f, String r, String o, int idR) {
            this.id = id; this.idRadnika = idR;
            this.firma = new SimpleStringProperty(f);
            this.radnik = new SimpleStringProperty(r);
            this.opis = new SimpleStringProperty(o);
        }
    }
}