package com.almasb.cf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CopyFinderApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));

        Parent root = loader.load();
        loader.<Controller>getController().setModel(new Model());

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("CopyFinder 1.0");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
