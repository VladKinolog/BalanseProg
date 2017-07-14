package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jssc.SerialPortException;

public class Main extends Application {


    private Stage primaryStage;
    private BorderPane rootLayout;
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Hello World");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("sample.fxml"));
        rootLayout = (BorderPane) loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        primaryStage.setResizable(false);
        primaryStage.show();



        controller = loader.getController();
        controller.setMainApp(this);





    }


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void stop() throws SerialPortException {
        System.out.println("Приложение закрыто");
        controller.stopTask();
    }
}