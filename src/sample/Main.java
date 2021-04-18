package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
        primaryStage.setTitle("Tool Download Drive");
        primaryStage.setScene(new Scene(root, 600, 306));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
