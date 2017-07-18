package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jssc.SerialPortException;

import java.util.prefs.Preferences;

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

        getPreferences();





    }

    /**
     * Получение последних значений в текстовых графах.
     */
    private void getPreferences () {
        Preferences pref = Preferences.userNodeForPackage(Main.class);

        String firstWeight = pref.get("firstWeight", "0");
        String secondWeight = pref.get( "secondWeight", "0");

        TextField firstW = controller.getFirstWeight();
        TextField secondW = controller.getSecondWeight();

        firstW.setText(firstWeight);
        secondW.setText(secondWeight);

        int comNumber;

    }

    /**
     * Сохранение последних введеных параметров
     */
    private void setPreferences() {
        Preferences pref = Preferences.userNodeForPackage(Main.class);

        String firstWeight = controller.getFirstWeight().getText();
        String secondWeight = controller.getSecondWeight().getText();

        firstWeight = firstWeight.trim();
        secondWeight = secondWeight.trim();

        if (!firstWeight.equals("")) {
            pref.put("firstWeight", firstWeight);
        }
        if (!secondWeight.equals("")) {
            pref.put("secondWeight", secondWeight);
        }
        int comNumber;

    }


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void stop() throws SerialPortException {
        System.out.println("Приложение закрыто");
        setPreferences();
        controller.stopTask();
    }
}