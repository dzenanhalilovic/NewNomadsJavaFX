package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button; // OBAVEZNO OVAJ IMPORT, NE java.awt.*
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class RegruterController {

    // @FXML referenca na dugme iz FXML-a (fx:id="logout")
    @FXML private Button logout;

    @FXML
    private void openPotraznje(ActionEvent event) {
        switchScene(event, "/com/example/newnomads/regruter_potraznje.fxml", "Potražnje za radnicima");
    }

    @FXML
    private void openRadnici(ActionEvent event) {
        switchScene(event, "/com/example/newnomads/regruter_radnici.fxml", "Pregled radnika");
    }

    @FXML
    private void openUgovori(ActionEvent event) {
        switchScene(event, "/com/example/newnomads/regruter_ugovori.fxml", "Ugovori");
    }

    @FXML
    private void openZahtjevi(ActionEvent event) {
        switchScene(event, "/com/example/newnomads/regruter_zahtjevi.fxml", "Zahtjevi firmi");
    }

    @FXML
    private void logout(ActionEvent event) {
        Session.clear(); // Očisti sesiju pri odjavi
        switchScene(event, "/com/example/newnomads/login.fxml", "Login");
    }

    /**
     * Univerzalna metoda za promjenu scene koristeći ActionEvent
     */
    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            // Dohvatamo Stage preko izvora događaja (dugmeta koje je kliknuto)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
}