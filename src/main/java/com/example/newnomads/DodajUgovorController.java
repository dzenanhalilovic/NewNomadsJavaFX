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
import java.time.LocalDate;

public class DodajUgovorController {

    @FXML private ComboBox<String> comboFirma;
    @FXML private ComboBox<String> comboRadnik;
    @FXML private DatePicker datePocetak;
    @FXML private DatePicker dateKraj;
    @FXML private TextArea textOpis;
    @FXML private Button btnPosalji;

    private ObservableList<String> firmeList = FXCollections.observableArrayList();
    private ObservableList<String> radniciList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadFirme();
        loadRadnici();

        // Postavi današnji datum kao podrazumevani početak rada
        datePocetak.setValue(LocalDate.now());
    }

    /**
     * METODA ZA AUTO-POPUNJAVANJE (Poziva se iz RegruterRadniciController)
     */
    public void setPodaci(Radnik radnik, int idFirme) {
        // 1. Selektuj radnika
        if (radnik != null) {
            String radnikString = radnik.getIdRadnika() + " - " + radnik.getIme() + " " + radnik.getPrezime();
            if (!comboRadnik.getItems().contains(radnikString)) {
                comboRadnik.getItems().add(radnikString);
            }
            comboRadnik.setValue(radnikString);
        }

        // 2. Selektuj firmu
        if (idFirme != -1) {
            for (String f : comboFirma.getItems()) {
                if (f.startsWith(idFirme + " -")) {
                    comboFirma.setValue(f);
                    break;
                }
            }
        }
    }

    private void loadFirme() {
        try (Connection conn = DB.getConnection()) {
            firmeList.clear();
            ResultSet rs = conn.prepareStatement("SELECT idFirme, imeFirme FROM firmeKlijenti").executeQuery();
            while (rs.next()) {
                firmeList.add(rs.getInt("idFirme") + " - " + rs.getString("imeFirme"));
            }
            comboFirma.setItems(firmeList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadRadnici() {
        try (Connection conn = DB.getConnection()) {
            radniciList.clear();
            ResultSet rs = conn.prepareStatement("SELECT idRadnika, ime, prezime FROM radnici").executeQuery();
            while (rs.next()) {
                radniciList.add(rs.getInt("idRadnika") + " - " + rs.getString("ime") + " " + rs.getString("prezime"));
            }
            comboRadnik.setItems(radniciList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void posaljiUgovor() {
        try (Connection conn = DB.getConnection()) {

            if (comboFirma.getValue() == null || comboRadnik.getValue() == null || datePocetak.getValue() == null) {
                showAlert("Greška", "Popunite sva obavezna polja (Firma, Radnik, Datum početka)!");
                return;
            }

            int idFirme = Integer.parseInt(comboFirma.getValue().split(" - ")[0]);
            int idRadnika = Integer.parseInt(comboRadnik.getValue().split(" - ")[0]);
            Date datumPocetka = Date.valueOf(datePocetak.getValue());
            Date datumKraja = dateKraj.getValue() != null ? Date.valueOf(dateKraj.getValue()) : null;

            // POPRAVLJENO: Mora biti tačno 'naCekanju' zbog ENUM-a u bazi
            String status = "naCekanju";
            String opis = textOpis.getText();

            String sql = """
                INSERT INTO ugovor (idFirme, idRadnika, datumPocetkaRada, datumKrajaRada, statusUgovora, opis, drzavaRadaId, datumKreiranja) 
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idFirme);
            stmt.setInt(2, idRadnika);
            stmt.setDate(3, datumPocetka);
            stmt.setDate(4, datumKraja);
            stmt.setString(5, status);
            stmt.setString(6, opis);
            stmt.setInt(7, 1);

            int inserted = stmt.executeUpdate();
            if (inserted > 0) {
                // Ažuriramo status radnika u 'zaposlen' (provjeri da li tvoj ENUM u radnicima podržava ovo)
                azurirajStatusRadnika(idRadnika, "zaposlen");

                showAlert("Uspjeh", "Ugovor je uspješno kreiran!");
                Stage stage = (Stage) btnPosalji.getScene().getWindow();
                stage.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Greška", "Greška pri upisu: " + e.getMessage());
        }
    }

    private void azurirajStatusRadnika(int idRadnika, String noviStatus) {
        try (Connection conn = DB.getConnection()) {
            // NAPOMENA: Ako tabela 'radnici' ima ENUM za status, 'noviStatus' mora biti jedna od dozvoljenih vrijednosti
            String sql = "UPDATE radnici SET nazivStatusa = ? WHERE idRadnika = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, noviStatus);
            stmt.setInt(2, idRadnika);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Status radnika nije ažuriran, ali je ugovor kreiran.");
            e.printStackTrace();
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