package com.example.newnomads;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // TEST konekcija na bazu prije otvaranja GUI-ja
        try (Connection conn = DB.getConnection()) {
            System.out.println("Uspješno spojeno na bazu!");
        } catch (Exception e) {
            System.err.println("Greška pri konekciji na bazu:");
            e.printStackTrace();
        }

        // Pokretanje login prozora
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/newnomads/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("NewNomads Login");
        stage.setScene(scene);

        stage.setFullScreen(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
