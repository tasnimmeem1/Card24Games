package org.example.payingcards;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CardApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                CardApplication.class.getResource("hello-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 650);

        stage.setMinWidth(850);
        stage.setMinHeight(650);

        scene.getStylesheets().add(
                CardApplication.class.getResource("style.css").toExternalForm()
        );

        stage.setTitle("Card 24 Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}