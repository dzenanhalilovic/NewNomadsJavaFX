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

import java.sql.Connection;
import java.sql.PreparedStatement;

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

        azurirajVidljivostDugmadi(null);

        ugovoriTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            azurirajVidljivostDugmadi(newSel);
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

        boolean naCekanju = ugovor.getStatusUgovora() != null &&
                ugovor.getStatusUgovora().trim().equalsIgnoreCase("naCekanju");

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
            try (Connection conn = DB.getConnection()) {
                // 1. Promjena statusa ugovora
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE ugovor SET statusUgovora = 'aktivan' WHERE idUgovora = ?"
                );
                stmt.setInt(1, selektovan.getIdUgovora());
                stmt.executeUpdate();

                // 2a. Smanji brojRadnika za 1
                stmt = conn.prepareStatement(
                        "UPDATE potraznjaRadnika SET brojRadnika = brojRadnika - 1 WHERE idPotraznjeRadnika = ?"
                );
                stmt.setInt(1, selektovan.getIdPotraznjeRadnika());
                stmt.executeUpdate();

                // 2b. Ako je brojRadnika sada 0, postavi status na 'neaktivna'
                stmt = conn.prepareStatement(
                        "UPDATE potraznjaRadnika SET statusPotraznje = 'neaktivna' " +
                                "WHERE idPotraznjeRadnika = ? AND brojRadnika <= 0"
                );
                stmt.setInt(1, selektovan.getIdPotraznjeRadnika());
                stmt.executeUpdate();

                loadUgovori(); // refresujemo tabelu

            } catch (Exception e) { e.printStackTrace(); }
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
        } catch (Exception e) { e.printStackTrace(); }
    }
}
