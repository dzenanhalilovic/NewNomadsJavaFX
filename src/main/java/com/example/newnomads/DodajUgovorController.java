package com.example.newnomads;

import bazneTabele.Radnik;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DodajUgovorController {

    @FXML private ComboBox<String> comboFirma;
    @FXML private ComboBox<String> comboRadnik;
    @FXML private DatePicker datePocetak;
    @FXML private DatePicker dateKraj;
    // @FXML private ComboBox<String> comboStatus; // OBRISANO - ne treba nam više
    @FXML private TextArea textOpis;
    @FXML private Button btnPosalji;

    private ObservableList<String> firmeList = FXCollections.observableArrayList();
    private ObservableList<String> radniciList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // comboStatus.getItems().addAll(...); // OBRISANO
        loadFirme();
        loadRadnici();
    }

    private void loadFirme() {
        try (Connection conn = DB.getConnection()) {
            firmeList.clear();
            ResultSet rs = conn.prepareStatement("SELECT idFirme, imeFirme FROM firmeKlijenti").executeQuery();
            while (rs.next()) {
                firmeList.add(rs.getInt("idFirme") + " - " + rs.getString("imeFirme"));
            }
            comboFirma.setItems(firmeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRadnici() {
        try (Connection conn = DB.getConnection()) {
            radniciList.clear();
            ResultSet rs = conn.prepareStatement("SELECT idRadnika, ime, prezime FROM radnici").executeQuery();
            while (rs.next()) {
                radniciList.add(rs.getInt("idRadnika") + " - " + rs.getString("ime") + " " + rs.getString("prezime"));
            }
            comboRadnik.setItems(radniciList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void posaljiUgovor() {
        try (Connection conn = DB.getConnection()) {

            // Provjera - izbačen uslov za comboStatus
            if (comboFirma.getValue() == null || comboRadnik.getValue() == null || datePocetak.getValue() == null) {
                showAlert("Greška", "Popunite sva obavezna polja (Firma, Radnik, Datum početka)!");
                return;
            }

            int idFirme = Integer.parseInt(comboFirma.getValue().split(" - ")[0]);
            int idRadnika = Integer.parseInt(comboRadnik.getValue().split(" - ")[0]);
            Date datumPocetka = Date.valueOf(datePocetak.getValue());
            Date datumKraja = dateKraj.getValue() != null ? Date.valueOf(dateKraj.getValue()) : null;

            // HARDKODIRAN STATUS: Uvijek ide kao naČekanju
            String status = "naČekanju";
            String opis = textOpis.getText();

            String sql = "INSERT INTO ugovor (idFirme, idRadnika, datumPocetkaRada, datumKrajaRada, statusUgovora, opis, drzavaRadaId, datumKreiranja) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idFirme);
            stmt.setInt(2, idRadnika);
            stmt.setDate(3, datumPocetka);
            stmt.setDate(4, datumKraja);
            stmt.setString(5, status); // Ovdje se šalje "naČekanju"
            stmt.setString(6, opis);
            stmt.setInt(7, 1);

            int inserted = stmt.executeUpdate();
            if (inserted > 0) {
                showAlert("Uspjeh", "Ugovor je uspješno kreiran sa statusom 'na čekanju'!");
                Stage stage = (Stage) btnPosalji.getScene().getWindow();
                stage.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Greška", "Došlo je do greške pri upisu u bazu.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}