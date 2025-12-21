package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import bazneTabele.PotraznjaRadnika;

public class RegruterPotraznjeController {

    @FXML
    private TableView<PotraznjaRadnika> potraznjeTable;

    @FXML private TableColumn<PotraznjaRadnika, Number> idCol;
    @FXML private TableColumn<PotraznjaRadnika, String> naslovCol;
    @FXML private TableColumn<PotraznjaRadnika, Number> firmaCol;
    @FXML private TableColumn<PotraznjaRadnika, String> statusCol;
    @FXML private TableColumn<PotraznjaRadnika, String> granaCol;
    @FXML private TableColumn<PotraznjaRadnika, Void> akcijaCol;

    private final ObservableList<PotraznjaRadnika> potraznje = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(cellData -> cellData.getValue().idPotraznjeRadnikaProperty());
        firmaCol.setCellValueFactory(cellData -> cellData.getValue().idFirmeProperty());
        naslovCol.setCellValueFactory(cellData -> cellData.getValue().naslovPotraznjeProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusPotraznjeProperty());
        granaCol.setCellValueFactory(data -> data.getValue().granaProperty());

        dodajDugmeUTableu();

        // OVDJE JE BIO PROBLEM: Dodan idFirme u poziv
        potraznjeTable.setRowFactory(tv -> {
            TableRow<PotraznjaRadnika> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    PotraznjaRadnika p = row.getItem();
                    otvoriRadnikeSaFilterom(p.getGrana(), p.getIdFirme());
                }
            });
            return row;
        });

        potraznjeTable.setItems(potraznje);
        loadPotraznje();
    }

    private void dodajDugmeUTableu() {
        akcijaCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PotraznjaRadnika, Void> call(final TableColumn<PotraznjaRadnika, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Pretraži radnike");
                    {
                        btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            PotraznjaRadnika data = getTableView().getItems().get(getIndex());
                            // OVDJE JE BIO PROBLEM: Proslijeđena oba parametra
                            otvoriRadnikeSaFilterom(data.getGrana(), data.getIdFirme());
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(btn);
                    }
                };
            }
        });
    }

    @FXML
    private void loadPotraznje() {
        String sql = "SELECT idPotraznjeRadnika, naslovPotraznje, idFirme, statusPotraznje, grana FROM potraznjaRadnika";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            potraznje.clear();
            while (rs.next()) {
                PotraznjaRadnika p = new PotraznjaRadnika(
                        rs.getInt("idPotraznjeRadnika"),
                        null, 0,
                        rs.getInt("idFirme"),
                        null,
                        rs.getString("naslovPotraznje"),
                        "",
                        rs.getString("statusPotraznje"),
                        rs.getString("grana"),
                        0
                );
                potraznje.add(p);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ISPRAVLJENA METODA: Sada prima dva parametra
    private void otvoriRadnikeSaFilterom(String nazivGrane, int idFirme) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/regruter_radnici.fxml"));
            Scene scene = new Scene(loader.load());

            RegruterRadniciController radniciCtrl = loader.getController();
            radniciCtrl.filtrirajPoGrani(nazivGrane, idFirme);

            Stage stage = (Stage) potraznjeTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Matching radnika za granu: " + nazivGrane);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void goBack() { switchScene("/com/example/newnomads/regruter.fxml", "Dashboard"); }
    @FXML private void openRadnici() { switchScene("/com/example/newnomads/regruter_radnici.fxml", "Radnici"); }
    @FXML private void openUgovori() { switchScene("/com/example/newnomads/regruter_ugovori.fxml", "Ugovori"); }
    @FXML private void logout() { switchScene("/com/example/newnomads/login.fxml", "Login"); }

    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) potraznjeTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) { e.printStackTrace(); }
    }
}