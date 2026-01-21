package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class RegisterController {

    @FXML private TextField imeField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label message;

    @FXML private TextField brojTelefonaField;
    @FXML private TextField vlasnikFirmeField;
    @FXML private TextArea oFirmiField;



    @FXML
    private void register() throws Exception {
        String ime = imeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        String brojTelefona = brojTelefonaField.getText();

        if (ime.isEmpty() || email.isEmpty() || password.isEmpty() || brojTelefona.isEmpty()) {
            message.setText("Sva obavezna polja moraju biti popunjena!");
            return;
        }

        if (!email.contains("@")) {
            message.setText("Email nije validan.");
            return;
        }

        // ROLE JE UVIJEK FIRMA
        User user = new User(ime, email, password, "firma");
        user.setBrojTelefona(brojTelefona);
        user.setVlasnikFirme(vlasnikFirmeField.getText());
        user.setoFirmi(oFirmiField.getText());

        if (UserDAO.registerUser(user)) {
            message.setText("Registracija uspješna! Prebacujem na login...");
            Stage stage = (Stage) imeField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setMaximized(true);
        } else {
            message.setText("Registracija nije uspjela!");
        }
    }

    @FXML
    private void goToLogin() throws Exception {
        Stage stage = (Stage) imeField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/login.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);

        // Forsiraj maksimizaciju
        stage.setMaximized(false); // Resetuj kratko (opcionalno, pomaže kod nekih OS-ova)
        stage.setMaximized(true);
    }

    @FXML
    private ImageView registerImage;

    public void initialize() {
        registerImage.setImage(new Image(getClass().getResource("/images/Logo.png").toExternalForm()));
    }

}
