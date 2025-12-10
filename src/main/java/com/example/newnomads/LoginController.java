package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label message;

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

                case "regruter":
                    fxmlLoader = new FXMLLoader(getClass().getResource(
                            "/com/example/newnomads/regruter.fxml"
                    ));
                    break;

                case "firma":
                    fxmlLoader = new FXMLLoader(getClass().getResource(
                            "/com/example/newnomads/firma.fxml"
                    ));
                    break;

                default:
                    message.setText("Nepoznata uloga: " + role);
                    return;
            }

            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Greška pri otvaranju dashboarda!");
        }
    }

    @FXML
    private void goToRegister() throws Exception {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/newnomads/register.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
}
