package com.example.newnomads;

import bazneTabele.Radnik;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
    @FXML private ComboBox<String> comboPotraznje;
    @FXML private DatePicker datePocetak;
    @FXML private DatePicker dateKraj;
    @FXML private TextArea textOpis;
    @FXML private Button btnPosalji;

    private ObservableList<String> firmeList = FXCollections.observableArrayList();
    private ObservableList<String> radniciList = FXCollections.observableArrayList();
    private ObservableList<String> potraznjeList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadFirme();
        loadRadnici();

        // Postavi listener za comboFirma da filtrira potražnje po firmi
        comboFirma.setOnAction(event -> loadPotraznjeZaFirmu());

        // Postavi današnji datum kao podrazumevani početak rada
        datePocetak.setValue(LocalDate.now());
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

    // NOVO: učitava samo potražnje za odabranu firmu
    private void loadPotraznjeZaFirmu() {
        String selectedFirma = comboFirma.getValue();
        if (selectedFirma == null) {
            comboPotraznje.getItems().clear();
            return;
        }

        int idFirme = Integer.parseInt(selectedFirma.split(" - ")[0]);

        try (Connection conn = DB.getConnection()) {
            potraznjeList.clear();
            String sql = "SELECT idPotraznjeRadnika, naslovPotraznje, brojRadnika " +
                    "FROM potraznjaRadnika " +
                    "WHERE idFirme = ? AND statusPotraznje = 'aktivna' AND brojRadnika > 0";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idFirme);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                potraznjeList.add(rs.getInt("idPotraznjeRadnika") + " - " +
                        rs.getString("naslovPotraznje") + " (" + rs.getInt("brojRadnika") + " preostalo)");
            }

            comboPotraznje.setItems(potraznjeList);

        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void posaljiUgovor() {
        try (Connection conn = DB.getConnection()) {

            if (comboFirma.getValue() == null || comboRadnik.getValue() == null ||
                    datePocetak.getValue() == null || comboPotraznje.getValue() == null) {
                showAlert("Greška", "Popunite sva obavezna polja (Firma, Radnik, Potražnja, Datum početka)!");
                return;
            }

            int idFirme = Integer.parseInt(comboFirma.getValue().split(" - ")[0]);
            int idRadnika = Integer.parseInt(comboRadnik.getValue().split(" - ")[0]);
            int idPotraznje = Integer.parseInt(comboPotraznje.getValue().split(" - ")[0]);
            Date datumPocetka = Date.valueOf(datePocetak.getValue());
            Date datumKraja = dateKraj.getValue() != null ? Date.valueOf(dateKraj.getValue()) : null;

            String status = "naCekanju";
            String opis = textOpis.getText();

            String sql = """
                INSERT INTO ugovor (idFirme, idRadnika, idPotraznjeRadnika, datumPocetkaRada, datumKrajaRada, statusUgovora, opis, drzavaRadaId, datumKreiranja) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idFirme);
            stmt.setInt(2, idRadnika);
            stmt.setInt(3, idPotraznje);
            stmt.setDate(4, datumPocetka);
            stmt.setDate(5, datumKraja);
            stmt.setString(6, status);
            stmt.setString(7, opis);
            stmt.setInt(8, 1);

            int inserted = stmt.executeUpdate();
            if (inserted > 0) {
                azurirajStatusRadnika(idRadnika, "zaposlen");
                showAlert("Uspjeh", "Ugovor je uspješno kreiran!");
                Stage stage = (Stage) btnPosalji.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/newnomads/regruter_ugovori.fxml")
                );
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                StageUtils.setFullScreen(stage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Greška", "Greška pri upisu: " + e.getMessage());
        }
    }
    public void setPodaci(Radnik radnik, int idFirme) {
        if (radnik != null) {
            String radnikString = radnik.getIdRadnika() + " - " + radnik.getIme() + " " + radnik.getPrezime();
            if (!comboRadnik.getItems().contains(radnikString)) comboRadnik.getItems().add(radnikString);
            comboRadnik.setValue(radnikString);
        }

        if (idFirme != -1) {
            for (String f : comboFirma.getItems()) {
                if (f.startsWith(idFirme + " -")) {
                    comboFirma.setValue(f);
                    loadPotraznjeZaFirmu(); // odmah prikaz potražnji za firmu
                    break;
                }
            }
        }
    }


    private void azurirajStatusRadnika(int idRadnika, String noviStatus) {
        try (Connection conn = DB.getConnection()) {
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
