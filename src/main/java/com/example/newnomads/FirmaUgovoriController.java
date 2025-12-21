package com.example.newnomads;

import bazneTabele.Ugovor;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML private Button btnPrihvati;
    @FXML private Button btnOdbij;

    private int idFirme;

    public void setIdFirme(int idFirme) {
        this.idFirme = idFirme;
        loadUgovori();
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

        // Početno sakrij dugmad
        azurirajVidljivostDugmadi(null);

        // Dodajemo listener na selekciju u tabeli
        ugovoriTable.getSelectionModel().selectedItemProperty().addListener((obs, staraSelekcija, novaSelekcija) -> {
            azurirajVidljivostDugmadi(novaSelekcija);
        });

        ugovoriTable.setFixedCellSize(25);
        ugovoriTable.setPrefHeight(25 * 5 + 28);
    }

    private void azurirajVidljivostDugmadi(Ugovor ugovor) {
        if (ugovor == null) {
            btnPrihvati.setVisible(false);
            btnPrihvati.setManaged(false);
            btnOdbij.setVisible(false);
            btnOdbij.setManaged(false);
            return;
        }

        String status = ugovor.getStatusUgovora();

        // Provjera uz ignorisanje velikih/malih slova i razmaka
        boolean naCekanju = status != null && status.trim().equalsIgnoreCase("naCekanju");

        btnPrihvati.setVisible(naCekanju);
        btnPrihvati.setManaged(naCekanju);
        btnOdbij.setVisible(naCekanju);
        btnOdbij.setManaged(naCekanju);
    }

    private void loadUgovori() {
        if (idFirme <= 0) return;
        ObservableList<Ugovor> lista = UgovorDAO.getUgovoriByFirmaId(idFirme);
        ugovoriTable.setItems(lista);
    }

    @FXML
    private void prihvatiUgovor() {
        Ugovor selektovan = ugovoriTable.getSelectionModel().getSelectedItem();
        if (selektovan != null) {
            UgovorDAO.updateStatusUgovora(selektovan.getIdUgovora(), "aktivan");
            loadUgovori();
        }
    }

    @FXML
    private void odbijUgovor() {
        Ugovor selektovan = ugovoriTable.getSelectionModel().getSelectedItem();
        if (selektovan != null) {
            UgovorDAO.updateStatusUgovora(selektovan.getIdUgovora(), "odbijen");
            loadUgovori();
        }
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) ugovoriTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/firma.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}