package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import jssc.SerialPortException;

import java.net.URL;
import java.util.prefs.Preferences;

public class Main extends Application {


    private Stage primaryStage;
    private BorderPane rootLayout;
    private Controller controller;

    AudioClip clip;

    @Override
    public void start(Stage primaryStage) throws Exception{

        System.out.println("Построение макета");
        final URL resource = getClass().getResource("/resources/sound.wav");


        clip = new AudioClip(resource.toString());

        this.primaryStage = primaryStage;
        primaryStage.setTitle("Libra");

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

    @Override
    public void init() throws Exception {
        super.init();

        System.out.println("Инициализация основного класса");
    }

    /**
     * Получение последних значений в текстовых графах.
     */
    private void getPreferences () {
        System.out.println("Получене сохраненных настроек");

        Preferences pref = Preferences.userNodeForPackage(Main.class);

        String firstWeight = pref.get("firstWeight", "0");
        String secondWeight = pref.get( "secondWeight", "0");
        String comNumber = pref.get("comNumber","0");
        String setTimeOnSecRelay = pref.get("setTimeOnSecRelay","0");
        String setTimeOffSecRelay = pref.get("setTimeOffSecRelay", "0");
        String setDeltaLimit = pref.get("setDeltaLimit", "0");


        System.out.println("Загружен каом - COM"+comNumber);

        TextField firstW = controller.getFirstWeight();
        TextField secondW = controller.getSecondWeight();
        TextField setTimeOnSecR = controller.getSetTimeOnSecR();
        TextField setTimeOffSecR = controller.getSetTimeOffSecR();
        TextField setDeltaL = controller.getSetDeltaLimit();

        firstW.setText(firstWeight);
        secondW.setText(secondWeight);
        setTimeOnSecR.setText(setTimeOnSecRelay);
        setTimeOffSecR.setText(setTimeOffSecRelay);
        setDeltaL.setText(setDeltaLimit);


        if (!comNumber.equals("0") ) {
            controller.createPort(Integer.parseInt(comNumber));
            controller.setNumberCom(Integer.parseInt(comNumber));
        }

    }

    /**
     * Сохранение последних введеных параметров
     */
    private void setPreferences() {
        Preferences pref = Preferences.userNodeForPackage(Main.class);

        String firstWeight = controller.getFirstWeight().getText();
        String secondWeight = controller.getSecondWeight().getText();
        String setTimeOnSecRelay = controller.getSetTimeOnSecR().getText();
        String setTimeOffSecRelay = controller.getSetTimeOffSecR().getText();
        String setDeltaLimit = controller.getSetDeltaLimit().getText();
        int comNumber = controller.getNumberCom();

        firstWeight = firstWeight.trim();
        secondWeight = secondWeight.trim();

        if (!firstWeight.equals("")) {
            pref.put("firstWeight", firstWeight);
        }
        if (!secondWeight.equals("")) {
            pref.put("secondWeight", secondWeight);
        }
        if (!setTimeOnSecRelay.equals("")) {
            pref.put("setTimeOnSecRelay",setTimeOnSecRelay);
        }
        if (!setTimeOffSecRelay.equals("")){
            pref.put("setTimeOffSecRelay",setTimeOffSecRelay);
        }
        if (!setDeltaLimit.equals("")) {
            pref.put("setDeltaLimit",setDeltaLimit);
        }
        if (comNumber > 0) {

            System.out.println("Сохраненный ком это - COM"+comNumber);
            pref.put ("comNumber", Integer.toString( comNumber));
        }

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