package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class RegruterController {

    @FXML private StackPane contentPane;
    @FXML private ImageView regruterLogo;

    @FXML private Button potraznjeBtn;
    @FXML private Button radniciBtn;
    @FXML private Button ugovoriBtn;
    @FXML private Button zahtjeviBtn;
    @FXML private Button logoutBtn;

    public void initialize() {
        // Postavi logo
        regruterLogo.setImage(new Image(getClass().getResource("/images/Logo.png").toExternalForm()));

        // Po defaultu učitaj Potražnje u StackPane
        try {
            loadContent("/com/example/newnomads/regruter_potraznje.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ==== StackPane učitavanje ==== */
    @FXML
    private void openPotraznje() {
        try {
            loadContent("/com/example/newnomads/regruter_potraznje.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRadnici() {
        try {
            loadContent("/com/example/newnomads/regruter_radnici.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openUgovori() {
        try {
            loadContent("/com/example/newnomads/regruter_ugovori.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openZahtjevi() {
        try {
            loadContent("/com/example/newnomads/regruter_zahtjevi.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ==== Logout preko pune promjene scene ==== */
    @FXML
    private void logout(ActionEvent event) {
        try {
            Session.clear(); // očisti sesiju
            switchScene(event, "/com/example/newnomads/login.fxml", "Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Helper za StackPane učitavanje */
    private void loadContent(String fxmlPath) throws Exception {
        Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
        contentPane.getChildren().setAll(content);
    }

    /** Helper za punu promjenu scene */
    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
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
