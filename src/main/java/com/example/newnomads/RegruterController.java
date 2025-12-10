package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RegruterController {

    @FXML
    private Button logout; // ovo se mapira na fx:id="logout" u FXML

    @FXML
    private void logout() {
        try {
            // Dohvati trenutni stage
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
