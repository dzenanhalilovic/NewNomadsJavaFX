package com.example.newnomads;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import bazneTabele.Ugovor;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class RegruterUgovoriController {

    @FXML private TableView<Ugovor> ugovoriTable;
    @FXML private TableColumn<Ugovor, String> radnikCol;
    @FXML private TableColumn<Ugovor, String> firmaCol;
    @FXML private TableColumn<Ugovor, String> datumPocetkaCol;
    @FXML private TableColumn<Ugovor, String> datumKrajaCol;
    @FXML private TableColumn<Ugovor, String> statusCol;
    @FXML private TableColumn<Ugovor, String> opisCol; // DODATO: Nova kolona za opis

    private ObservableList<Ugovor> ugovori = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Postavljanje cellValueFactory za svaku kolonu
        radnikCol.setCellValueFactory(data -> data.getValue().radnikProperty());
        firmaCol.setCellValueFactory(data -> data.getValue().firmaProperty());

        datumPocetkaCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDatumPocetkaRada() != null ? data.getValue().getDatumPocetkaRada().toString() : ""
                )
        );

        datumKrajaCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDatumKrajaRada() != null ? data.getValue().getDatumKrajaRada().toString() : "-"
                )
        );

        statusCol.setCellValueFactory(data -> data.getValue().statusUgovoraProperty());

        // POVEZIVANJE OPISA:
        opisCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOpis() != null ? data.getValue().getOpis() : "")
        );

        loadUgovori();
    }

    @FXML
    private void loadUgovori() {
        try (Connection conn = DB.getConnection()) {

            String sql = """
                SELECT u.idUgovora, u.idFirme, u.idRadnika, u.datumPocetkaRada, u.datumKrajaRada,
                       u.statusUgovora, u.opis, r.ime, r.prezime, f.imeFirme, u.drzavaRadaId, u.datumKreiranja
                FROM ugovor u
                JOIN radnici r ON u.idRadnika = r.idRadnika
                JOIN firmeKlijenti f ON u.idFirme = f.idFirme
                ORDER BY u.datumKreiranja DESC
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            ugovori.clear();
            while (rs.next()) {
                String imePrezime = rs.getString("ime") + " " + rs.getString("prezime");

                // Izvlačenje SQL datuma direktno
                Date sqlDatumPocetka = rs.getDate("datumPocetkaRada");
                Date sqlDatumKraja = rs.getDate("datumKrajaRada");
                Timestamp datumKreiranja = rs.getTimestamp("datumKreiranja");

                ugovori.add(new Ugovor(
                        rs.getInt("idUgovora"),
                        rs.getInt("idFirme"),
                        rs.getInt("idRadnika"),
                        datumKreiranja,
                        rs.getInt("drzavaRadaId"),
                        sqlDatumPocetka,
                        sqlDatumKraja,
                        rs.getString("statusUgovora"),
                        rs.getString("opis"),
                        imePrezime,
                        rs.getString("imeFirme")
                ));
            }

            ugovoriTable.setItems(ugovori);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void dodajUgovor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/dodajUgovor.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Dodaj novi ugovor");
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();

            loadUgovori(); // Osvježi tabelu nakon što se prozor zatvori
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) ugovoriTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/regruter.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        try {
            Stage stage = (Stage) ugovoriTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/login.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}