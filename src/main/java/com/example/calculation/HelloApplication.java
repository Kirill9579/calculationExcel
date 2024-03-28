package com.example.calculation;

import com.example.calculation.utils.ViewBuilder;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 350, 350);

        ViewBuilder viewBuilder = new ViewBuilder(stage);
        viewBuilder.buildView(root);

        stage.setTitle("Calculation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}