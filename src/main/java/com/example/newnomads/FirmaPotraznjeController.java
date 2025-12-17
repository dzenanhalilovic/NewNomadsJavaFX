package com.example.newnomads;

import bazneTabele.PotraznjaRadnika;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;


import java.util.List;

public class FirmaPotraznjeController {

    @FXML private TableView<PotraznjaRadnika> tablePotraznje;
    @FXML private TableColumn<PotraznjaRadnika, String> colNaslov;
    @FXML private TableColumn<PotraznjaRadnika, String> colOpis;
    @FXML private TableColumn<PotraznjaRadnika, String> colGrana;
    @FXML private TableColumn<PotraznjaRadnika, Integer> colBrojRadnika;
    @FXML private TableColumn<PotraznjaRadnika, String> colStatus;
    @FXML private TableColumn<PotraznjaRadnika, String> colRok;

    @FXML
    public void initialize() {
        // Povezivanje kolona sa property-ima
        colNaslov.setCellValueFactory(new PropertyValueFactory<>("naslovPotraznje"));
        colOpis.setCellValueFactory(new PropertyValueFactory<>("opisPotraznje"));
        colGrana.setCellValueFactory(new PropertyValueFactory<>("grana"));
        colBrojRadnika.setCellValueFactory(new PropertyValueFactory<>("brojRadnika"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusPotraznje"));
        colRok.setCellValueFactory(new PropertyValueFactory<>("krajnjiRok"));
        tablePotraznje.setFixedCellSize(25);
        tablePotraznje.setPrefHeight(25 * 5 + 28);
        // Učitaj potražnje iz baze
        loadPotraznje();
    }

    private void loadPotraznje() {
        int firmaId = Session.getIdFirme();
        List<PotraznjaRadnika> potraznje = PotraznjaDAO.getPotraznjeByFirma(firmaId);
        ObservableList<PotraznjaRadnika> obsList = FXCollections.observableArrayList(potraznje);
        tablePotraznje.setItems(obsList);
    }


    @FXML private Button btnDodajPotraznju; // fx:id dugmeta u FXML-u

    @FXML
    private void openDodajPotraznju() {
        try {
            Stage stage = (Stage) btnDodajPotraznju.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newnomads/firmaDodajPotraznju.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) btnDodajPotraznju.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/com/example/newnomads/firma.fxml"))
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
