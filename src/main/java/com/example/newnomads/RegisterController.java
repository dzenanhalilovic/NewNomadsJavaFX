package com.example.newnomads;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class RegisterController {

    @FXML private TextField imeField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label message;

    @FXML private TextField brojTelefonaField;
    @FXML private TextField vlasnikFirmeField;
    @FXML private TextArea oFirmiField;

    @FXML
    private void initialize() {
        roleBox.getItems().addAll("regruter", "firma");
        roleBox.setValue("regruter"); // default

        // Sakrij/Prikaži polja za firmu ovisno o roli
        roleBox.setOnAction(e -> {
            boolean isFirma = roleBox.getValue().equalsIgnoreCase("firma");
            brojTelefonaField.setVisible(isFirma);
            vlasnikFirmeField.setVisible(isFirma);
            oFirmiField.setVisible(isFirma);
        });
    }

    @FXML
    private void register() throws Exception {
        String ime = imeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (ime.isEmpty() || email.isEmpty() || password.isEmpty()) {
            message.setText("Sva polja su obavezna!");
            return;
        }
        if (!email.contains("@")) {
            message.setText("Email nije validan.");
            return;
        }

        User user = new User(ime, email, password, role);

        // Za firmu dodaj obavezna polja
        if (role.equalsIgnoreCase("firma")) {
            String brojTelefona = brojTelefonaField.getText();
            if (brojTelefona.isEmpty()) {
                message.setText("Broj telefona je obavezan za firmu!");
                return;
            }
            user.setBrojTelefona(brojTelefona);
            user.setVlasnikFirme(vlasnikFirmeField.getText());
            user.setoFirmi(oFirmiField.getText());
        }

        if (UserDAO.registerUser(user)) {
            message.setText("Registracija uspješna! Prebacujem na login...");
            Stage stage = (Stage) imeField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/newnomads/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
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
    }
}
