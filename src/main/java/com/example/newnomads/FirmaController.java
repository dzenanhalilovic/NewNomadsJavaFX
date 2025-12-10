package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class FirmaController {

    @FXML
    private Button logout; // mora odgovarati fx:id u FXML

    @FXML
    private void logout() {
        try {
            // Dohvati trenutni stage preko dugmeta
            Stage stage = (Stage) logout.getScene().getWindow();

            // Učitaj login scenu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/login.fxml"));
            Scene scene = new Scene(loader.load());

            // Postavi login scenu
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
