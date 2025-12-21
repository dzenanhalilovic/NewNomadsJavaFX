package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label message;
    @FXML private ImageView loginImage;

    public void initialize() {
        loginImage.setImage(new Image(getClass().getResource("/images/Logo.png").toExternalForm()));
    }

    @FXML
    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            message.setText("Unesite email i password!");
            return;
        }

        User user = UserDAO.login(email, password);
        if (user != null) {

            // postavi osnovne podatke
            Session.setUser(
                    0, // ako ne koristiš userId sad
                    user.getRole(),
                    user.getIme()
            );

            // ako je firma – pamti idFirme
            if (user.getRole().equalsIgnoreCase("firma")) {
                Session.setFirma(user.getIdFirme());
            }

            openDashboard(user.getRole());
        } else {
            message.setText("Pogrešan email ili password!");
        }
    }

    private void openDashboard(String role) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader fxmlLoader;

            switch (role.toLowerCase()) {
                case "admin":
                    fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newnomads/admin.fxml"));
                    break;
                case "regruter":
                    fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newnomads/regruter.fxml"));
                    break;
                case "firma":
                    fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newnomads/firma.fxml"));
                    break;
                default:
                    message.setText("Nepoznata uloga: " + role);
                    return;
            }

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            // prvo postavi scenu
            stage.setScene(scene);

            // tek nakon toga full screen / maximized
            StageUtils.setFullScreen(stage);

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Greška pri otvaranju dashboarda!");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/newnomads/register.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            // prvo postavi scenu
            stage.setScene(scene);

            // tek nakon toga full screen / maximized
            StageUtils.setFullScreen(stage);

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Greška pri otvaranju registracije!");
        }
    }
}