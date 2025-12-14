package com.example.newnomads;

import bazneTabele.Radnik;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class FirmaRadniciController {

    @FXML private TableView<Radnik> tableRadnici;
    @FXML private TableColumn<Radnik, String> colIme;
    @FXML private TableColumn<Radnik, String> colPrezime;
    @FXML private TableColumn<Radnik, String> colSpol;
    @FXML private TableColumn<Radnik, Integer> colDrzava;
    @FXML private TableColumn<Radnik, Integer> colGrana;
    @FXML private TableColumn<Radnik, java.sql.Date> colPasos;
    @FXML private TableColumn<Radnik, java.sql.Date> colViza;

    @FXML
    public void initialize() {
        colIme.setCellValueFactory(new PropertyValueFactory<>("ime"));
        colPrezime.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        colSpol.setCellValueFactory(new PropertyValueFactory<>("spol"));
        colDrzava.setCellValueFactory(new PropertyValueFactory<>("drzavaId"));
        colGrana.setCellValueFactory(new PropertyValueFactory<>("idGraneRada"));
        colPasos.setCellValueFactory(new PropertyValueFactory<>("doKadTrajePasos"));
        colViza.setCellValueFactory(new PropertyValueFactory<>("doKadTrajeViza"));

        ObservableList<Radnik> lista =
                FXCollections.observableArrayList(RadnikDAO.getSlobodniRadnici());

        tableRadnici.setItems(lista);
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) tableRadnici.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/com/example/newnomads/firmaDashboard.fxml"))
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
