package com.example.newnomads;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.sql.*;

public class AdminController {

    @FXML private TableView<ObservableList<String>> table;

    // --- Overlay ---
    @FXML private Pane overlay;

    // --- Regruter ---
    @FXML private VBox addRegruterBox;
    @FXML private TextField emailField, imeField;
    @FXML private PasswordField passwordField;
    @FXML private Label msg;

    // --- Radnik ---
    @FXML private ScrollPane addRadnikBox;
    @FXML private TextField imeRadnikField, prezimeRadnikField, brojPasosaField;
    @FXML private DatePicker datumRodjenjaPicker, doKadPasosPicker, doKadVizaPicker;
    @FXML private ComboBox<String> spolCombo, statusCombo, drzavaCombo, granaCombo;
    @FXML private Label msgRadnik;

    @FXML
    private void initialize() {
        showRadnici(); // default view

        spolCombo.setItems(FXCollections.observableArrayList("musko","zensko"));
        statusCombo.setItems(FXCollections.observableArrayList(
                "zaposlen","slobodan","uskoro istice viza","uskoro istice ugovor rada"));

        loadDrzave();
        loadGraneRada();
    }

    // ------------------ SHOW TABLES ------------------
    @FXML void showRadnici() {
        table.setVisible(true);
        hideForms();
        loadTable("SELECT idRadnika, ime, prezime, spol, datumRodjenja, nazivStatusa FROM radnici");
    }

    @FXML void showFirme() {
        table.setVisible(true);
        hideForms();
        loadTable("SELECT idFirme, imeFirme, mail, vlasnikFirme, brojTelefona FROM firmeKlijenti");
    }

    @FXML void showRegruteri() {
        table.setVisible(true);
        hideForms();
        loadTable("""
            SELECT r.regruterId, r.ime, u.email
            FROM regruter r
            JOIN users u ON u.userId = r.userId
        """);
    }

    // ------------------ SHOW MODALS ------------------
    private void showOverlay(Node nodeToShow) {
        overlay.setVisible(true);
        overlay.setManaged(true);

        overlay.getChildren().forEach(n -> n.setVisible(false));

        nodeToShow.setVisible(true);
        nodeToShow.setManaged(true);
    }

    @FXML void showAddRegruter() {
        table.setVisible(false);
        showOverlay(addRegruterBox);
    }

    @FXML void showAddRadnik() {
        table.setVisible(false);
        showOverlay(addRadnikBox);
    }

    // ------------------ ADD RECORDS ------------------
    @FXML
    void addRegruter() {
        try (Connection c = DB.getConnection()) {
            PreparedStatement u = c.prepareStatement(
                    "INSERT INTO users (email, password, role) VALUES (?, ?, 'regruter')",
                    Statement.RETURN_GENERATED_KEYS
            );
            u.setString(1, emailField.getText());
            u.setString(2, passwordField.getText());
            u.executeUpdate();

            ResultSet rs = u.getGeneratedKeys();
            rs.next();
            int userId = rs.getInt(1);

            PreparedStatement r = c.prepareStatement(
                    "INSERT INTO regruter (ime, mail, lozinka, potvrdjen, userId) VALUES (?, ?, ?, ?, ?)"
            );
            r.setString(1, imeField.getText());
            r.setString(2, emailField.getText());
            r.setString(3, passwordField.getText());
            r.setInt(4, 0);
            r.setInt(5, userId);
            r.executeUpdate();

            msg.setText("Regruter dodan ✔");

            emailField.clear();
            passwordField.clear();
            imeField.clear();

            hideForms();

            // Prikaz tabele radnika (ili regrutera ako želiš)
            showRadnici();

        } catch (Exception e) {
            e.printStackTrace();
            msg.setText("Greška!");
        }
    }


    @FXML
    void addRadnik() {
        try (Connection c = DB.getConnection()) {
            int drzavaId = getDrzavaIdByName(drzavaCombo.getValue(), c);
            int granaId = getGranaIdByName(granaCombo.getValue(), c);

            PreparedStatement p = c.prepareStatement(
                    "INSERT INTO radnici (brojPasosa, ime, prezime, drzavaId, idGraneRada, spol, doKadTrajePasos, doKadTrajeViza, datumRodjenja, nazivStatusa) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?)"
            );
            p.setString(1, brojPasosaField.getText());
            p.setString(2, imeRadnikField.getText());
            p.setString(3, prezimeRadnikField.getText());
            p.setInt(4, drzavaId);
            p.setInt(5, granaId);
            p.setString(6, spolCombo.getValue());
            p.setDate(7, Date.valueOf(doKadPasosPicker.getValue()));
            p.setDate(8, doKadVizaPicker.getValue() != null ? Date.valueOf(doKadVizaPicker.getValue()) : null);
            p.setDate(9, Date.valueOf(datumRodjenjaPicker.getValue()));
            p.setString(10, statusCombo.getValue());

            p.executeUpdate();

            msgRadnik.setText("Radnik dodan ✔");

            imeRadnikField.clear();
            prezimeRadnikField.clear();
            brojPasosaField.clear();
            datumRodjenjaPicker.setValue(null);
            doKadPasosPicker.setValue(null);
            doKadVizaPicker.setValue(null);
            spolCombo.setValue(null);
            statusCombo.setValue(null);
            drzavaCombo.setValue(null);
            granaCombo.setValue(null);

            hideForms();

        } catch (Exception e) {
            e.printStackTrace();
            msgRadnik.setText("Greška!");
        }
    }

    @FXML
    public void hideForms() {
        overlay.setVisible(false);
        overlay.setManaged(false);
        addRegruterBox.setVisible(false);
        addRegruterBox.setManaged(false);
        addRadnikBox.setVisible(false);
        addRadnikBox.setManaged(false);
        if (msg != null) msg.setText("");
        if (msgRadnik != null) msgRadnik.setText("");
    }

    @FXML
    public void closeModal() {
        // Sakrij overlay i forme
        hideForms();

        // Prikaži default tabelu radnika
        showRadnici();
    }



    // ------------------ LOAD TABLE ------------------
    private void loadTable(String sql) {
        try (Connection c = DB.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            table.getColumns().clear();
            table.getItems().clear();

            int cols = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= cols; i++) {
                final int colIndex = i;
                TableColumn<ObservableList<String>, String> col =
                        new TableColumn<>(rs.getMetaData().getColumnName(i));

                col.setCellValueFactory(data ->
                        new ReadOnlyStringWrapper(data.getValue().get(colIndex - 1)));

                table.getColumns().add(col);
            }

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= cols; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }

            table.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------ LOAD DRZAVE & GRANE ------------------
    private void loadDrzave() {
        try (Connection c = DB.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT drzavaId, nazivDrzave FROM drzave")) {

            ObservableList<String> drzave = FXCollections.observableArrayList();
            while (rs.next()) {
                drzave.add(rs.getString("nazivDrzave"));
            }
            drzavaCombo.setItems(drzave);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGraneRada() {
        try (Connection c = DB.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT idGraneRada, nazivGraneRada FROM granaRada")) {

            ObservableList<String> grane = FXCollections.observableArrayList();
            while (rs.next()) {
                grane.add(rs.getString("nazivGraneRada"));
            }
            granaCombo.setItems(grane);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getDrzavaIdByName(String naziv, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement("SELECT drzavaId FROM drzave WHERE nazivDrzave=?");
        p.setString(1, naziv);
        ResultSet rs = p.executeQuery();
        rs.next();
        return rs.getInt(1);
    }



    private int getGranaIdByName(String naziv, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement("SELECT idGraneRada FROM granaRada WHERE nazivGraneRada=?");
        p.setString(1, naziv);
        ResultSet rs = p.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    @FXML
    void logout() {
        try {
            Stage stage = (Stage) table.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/com/example/newnomads/login.fxml"))
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
