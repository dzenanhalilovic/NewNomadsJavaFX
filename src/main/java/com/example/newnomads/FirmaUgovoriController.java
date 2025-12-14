package com.example.newnomads;

import bazneTabele.Ugovor;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class FirmaUgovoriController {

    @FXML private TableView<Ugovor> ugovoriTable;
    @FXML private TableColumn<Ugovor, Number> colId;
    @FXML private TableColumn<Ugovor, String> colRadnik;
    @FXML private TableColumn<Ugovor, java.sql.Timestamp> colDatumKreiranja;
    @FXML private TableColumn<Ugovor, java.sql.Date> colDatumPocetka;
    @FXML private TableColumn<Ugovor, java.sql.Date> colDatumKraja;
    @FXML private TableColumn<Ugovor, String> colStatus;
    @FXML private TableColumn<Ugovor, String> colOpis;

    private int idFirme; // uklonjen hardkodirani 1

    // Setter za idFirme koji se poziva pri otvaranju ove stranice
    public void setIdFirme(int idFirme) {
        this.idFirme = idFirme;
        loadUgovori(); // odmah učitamo ugovore kad postavimo firmu
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(data -> data.getValue().idUgovoraProperty());
        colRadnik.setCellValueFactory(data -> data.getValue().radnikProperty());
        colDatumKreiranja.setCellValueFactory(data -> data.getValue().datumKreiranjaProperty());
        colDatumPocetka.setCellValueFactory(data -> data.getValue().datumPocetkaRadaProperty());
        colDatumKraja.setCellValueFactory(data -> data.getValue().datumKrajaRadaProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusUgovoraProperty());
        colOpis.setCellValueFactory(data -> data.getValue().opisProperty());
    }

    private void loadUgovori() {
        if (idFirme <= 0) return; // ako nije postavljeno, ne radimo ništa
        ObservableList<Ugovor> lista = UgovorDAO.getUgovoriByFirmaId(idFirme);
        ugovoriTable.setItems(lista);
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) ugovoriTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/firmaDashboard.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
