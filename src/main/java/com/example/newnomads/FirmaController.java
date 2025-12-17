package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FirmaController {

    @FXML
    private ImageView firmaLogo;

    @FXML
    private Button logout;

    @FXML
    private StackPane contentPane;

    public void initialize() {
        // Postavi logo
        try {
            firmaLogo.setImage(new Image(getClass().getResource("/images/Logo.png").toExternalForm()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Opcionalno: možeš odmah učitati default sadržaj
         loadContent("/com/example/newnomads/firmaPotraznje.fxml");
    }

    @FXML
    private void openPotraznje() {
        loadContent("/com/example/newnomads/firmaPotraznje.fxml");
    }

    @FXML
    private void openDostupniRadnici() {
        loadContent("/com/example/newnomads/firmaRadnici.fxml");
    }

    @FXML
    private void otvoriUgovore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/ugovori.fxml"));
            Pane pane = loader.load();

            // Pošalji ID firme kontroleru
            FirmaUgovoriController controller = loader.getController();
            controller.setIdFirme(Session.getIdFirme());

            // Zamijeni sadržaj centralnog panela
            contentPane.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        try {
            Stage stage = (Stage) logout.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setFullScreen(true);

            System.out.println("Firma je logoutovana, ID = " + Session.getIdFirme());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opšta metoda za učitavanje FXML-a u contentPane
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane pane = loader.load();
            contentPane.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
