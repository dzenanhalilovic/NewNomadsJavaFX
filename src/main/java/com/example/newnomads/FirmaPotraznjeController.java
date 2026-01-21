package com.example.newnomads;

import bazneTabele.PotraznjaRadnika;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;


import java.util.List;

public class FirmaPotraznjeController {

    @FXML private TableView<PotraznjaRadnika> tablePotraznje;
    @FXML private Button btnDodajPotraznju;
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

        tablePotraznje.setFocusTraversable(false);
        // Učitaj potražnje iz baze
        loadPotraznje();

    }

    private void loadPotraznje() {
        int firmaId = Session.getIdFirme();
        List<PotraznjaRadnika> potraznje = PotraznjaDAO.getPotraznjeByFirma(firmaId);
        ObservableList<PotraznjaRadnika> obsList = FXCollections.observableArrayList(potraznje);
        tablePotraznje.setItems(obsList);

    }


    @FXML
    private void openDodajPotraznju() {
        try {
            // Dobavi trenutnu scenu
            Stage stage = (Stage) btnDodajPotraznju.getScene().getWindow();
            Scene scene = stage.getScene();

            // Pronađi BorderPane (glavni layout dashboarda)
            BorderPane borderPane = (BorderPane) scene.getRoot();

            // Pronađi contentPane unutar BorderPane-a
            StackPane contentPane = (StackPane) borderPane.getCenter();

            // Učitaj formu za dodavanje potražnje
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/firmaDodajPotraznju.fxml"));
            Parent dodajPotraznjuView = loader.load();

            // Očisti contentPane i dodaj formu
            contentPane.getChildren().clear();
            contentPane.getChildren().add(dodajPotraznjuView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Dodaj ovu metodu za osvježavanje tabele
    private void refreshTable() {
        try {
            // Ovdje pozovi metodu koja puni tabelu potražnjama
            // Na primjer: ucitajPotraznje();
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
