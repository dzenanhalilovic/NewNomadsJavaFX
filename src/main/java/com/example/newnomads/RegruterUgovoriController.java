package com.example.newnomads;

import bazneTabele.Ugovor;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
    @FXML private TableColumn<Ugovor, String> opisCol;

    // NOVO: export kolona (dodaj je i u FXML)
    @FXML private TableColumn<Ugovor, Void> exportCol;

    private final ObservableList<Ugovor> ugovori = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        radnikCol.setCellValueFactory(data -> data.getValue().radnikProperty());
        firmaCol.setCellValueFactory(data -> data.getValue().firmaProperty());

        datumPocetkaCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDatumPocetkaRada() != null
                                ? data.getValue().getDatumPocetkaRada().toString()
                                : ""
                )
        );

        datumKrajaCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDatumKrajaRada() != null
                                ? data.getValue().getDatumKrajaRada().toString()
                                : "-"
                )
        );

        statusCol.setCellValueFactory(data -> data.getValue().statusUgovoraProperty());

        opisCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOpis() != null ? data.getValue().getOpis() : "")
        );

        // NOVO: setup export kolone
        setupExportColumn();

        loadUgovori();
    }

    private void setupExportColumn() {
        if (exportCol == null) return; // ako zaboraviš dodati u FXML, da ne puca

        exportCol.setCellFactory(col -> new TableCell<>() {
            private final Button exportBtn = new Button("Export");
            private final ContextMenu menu = new ContextMenu();

            {
                // Opcije u meniju
                MenuItem pdf = new MenuItem("Export kao PDF");
                pdf.setOnAction(e -> exportSelectedRow(ExportType.PDF));

                MenuItem word = new MenuItem("Export kao Word");
                word.setOnAction(e -> exportSelectedRow(ExportType.WORD));

                menu.getItems().addAll(pdf, word);

                exportBtn.setOnAction(e -> {
                    if (!menu.isShowing()) {
                        menu.show(exportBtn, Side.BOTTOM, 0, 0);
                    } else {
                        menu.hide();
                    }
                });

                exportBtn.setMinWidth(90);
            }

            private void exportSelectedRow(ExportType type) {
                Ugovor row = getTableView().getItems().get(getIndex());
                if (row == null) return;

                // Povuci ugovor iz baze po ID (da bude tačan i kompletan)
                Ugovor ugovor = UgovorDAO.getUgovorById(row.getIdUgovora());
                if (ugovor == null) {
                    showAlert(Alert.AlertType.ERROR, "Greška", "Ugovor nije pronađen u bazi.");
                    return;
                }

                FileChooser fc = new FileChooser();
                fc.setInitialFileName(buildFileName(ugovor, type));

                if (type == ExportType.PDF) {
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
                } else {
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word (*.docx)", "*.docx"));
                }

                Stage stage = (Stage) ugovoriTable.getScene().getWindow();
                File out = fc.showSaveDialog(stage);
                if (out == null) return;

                try {
                    if (type == ExportType.PDF) {
                        PdfUgovorExporter.export(ugovor, out);
                    } else {
                        WordUgovorExporter.export(ugovor, out);
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Ugovor je exportovan.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Greška", "Neuspješan export: " + ex.getMessage());
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : exportBtn);
            }
        });
    }

    @FXML
    private void loadUgovori() {
        try (Connection conn = DB.getConnection()) {

            String sql = """
                SELECT u.idUgovora, u.idFirme, u.idRadnika, u.idPotraznjeRadnika,
                       u.datumPocetkaRada, u.datumKrajaRada, u.statusUgovora, u.opis,
                       r.ime, r.prezime, f.imeFirme, u.drzavaRadaId, u.datumKreiranja
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
                        rs.getString("imeFirme"),
                        rs.getInt("idPotraznjeRadnika")
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

            loadUgovori();
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

    // Helpers

    private String buildFileName(Ugovor u, ExportType type) {
        String safeRadnik = (u.getRadnik() == null ? "radnik" : u.getRadnik())
                .replaceAll("[\\\\/:*?\"<>|]", "_");
        String ext = (type == ExportType.PDF) ? ".pdf" : ".docx";
        return "Ugovor_" + u.getIdUgovora() + "_" + safeRadnik + ext;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private enum ExportType { PDF, WORD }
}
