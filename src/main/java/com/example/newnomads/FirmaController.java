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
    private void openPotraznje() {
        try {
            Stage stage = (Stage) logout.getScene().getWindow(); // koristimo logout dugme samo da uzmemo stage
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newnomads/firmaPotraznje.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            System.out.println("Logovana firma ID = " + Session.getIdFirme());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openDostupniRadnici() {
        try {
            Stage stage = (Stage) logout.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/newnomads/firmaRadnici.fxml")
            );
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
