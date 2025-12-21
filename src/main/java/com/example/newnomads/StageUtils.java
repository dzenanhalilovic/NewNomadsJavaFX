package com.example.newnomads;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class StageUtils {

    // Otvara full screen Stage sa datim FXML root-om
    public static void openFullScreenStage(Parent root) {
        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setFullScreen(true);      // full screen (sakriva taskbar)
        // ili alternativno: stage.setMaximized(true); // samo maksimizirano
        stage.show();
    }

    // Ako već imaš Stage (primarni ili iz kontrolera)
    public static void setFullScreen(Stage stage) {
        stage.setFullScreen(true);
        // ili stage.setMaximized(true);
    }
}