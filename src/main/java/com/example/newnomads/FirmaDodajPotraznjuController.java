package com.example.newnomads;

import bazneTabele.PotraznjaRadnika;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class FirmaDodajPotraznjuController {

    @FXML private TextField naslovField;
    @FXML private TextArea opisField;
    @FXML private DatePicker rokPicker;
    @FXML private ComboBox<String> granaCombo;
    @FXML private TextField brojRadnikaField;

    @FXML
    public void initialize() {
        // Pozivamo metodu koja puni ComboBox podacima iz baze
        ucitajGrane();
    }

    private void ucitajGrane() {
        // SQL upit za tvoju tabelu granaRada
        String sql = "SELECT nazivGraneRada FROM granaRada ORDER BY nazivGraneRada ASC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            granaCombo.getItems().clear();

            while (rs.next()) {
                // Dodajemo nazive iz kolone nazivGraneRada u ComboBox
                granaCombo.getItems().add(rs.getString("nazivGraneRada"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Alert u slučaju da baza nije dostupna
            System.out.println("Greška pri učitavanju grana rada.");
        }
    }

    @FXML
    private void objaviPotraznju() {
        // Osnovna validacija da polja nisu prazna
        if (naslovField.getText().isEmpty() || granaCombo.getValue() == null || rokPicker.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Popunite sva polja!").show();
            return;
        }

        try {
            String naslov = naslovField.getText();
            String opis = opisField.getText();
            Date rok = Date.valueOf(rokPicker.getValue());
            String grana = granaCombo.getValue();
            int brojRadnika = Integer.parseInt(brojRadnikaField.getText());

            int idFirme = Session.getIdFirme();

            PotraznjaRadnika p = new PotraznjaRadnika(
                    0,
                    new Timestamp(System.currentTimeMillis()),
                    1,
                    idFirme,
                    rok,
                    naslov,
                    opis,
                    "Aktivna",
                    grana,
                    brojRadnika
            );

            boolean uspjeh = PotraznjaDAO.dodajPotraznju(p);

            if (uspjeh) {
                vratiNaListu();
            } else {
                new Alert(Alert.AlertType.ERROR, "Greška pri dodavanju potražnje!").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vratiNaListu() {
        try {
            Stage stage = (Stage) naslovField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newnomads/firmaPotraznje.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}