package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import sun.rmi.runtime.Log;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import static jssc.SerialPortList.getPortNames;

public class Controller {


    @FXML
    private Label weightLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button endButton;
    @FXML
    private Button torirovkaButton;
    @FXML
    private TextField firstWeight;
    @FXML
    private TextField secondWeight;
    @FXML
    private ChoiceBox choiceCom;
    @FXML
    private Menu menuFile;

    private double weight = 0;
    private double firstW;
    private double secondW;

    private ObservableList<String> listCom;

    private Balances balances;
    private Timer timer = new Timer();
    MyTimerTask myTimerTask = new MyTimerTask();
    private Main mainApp;

    private boolean pause = false;
    private boolean programIsRun = false;


    public Controller(){
    }


    @FXML
    private void initialize(){

        System.out.println("Инициализация класса контроллера");

        // слушатель выбора необходимолго ком порта
        choiceCom.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() >= 0)
                    try {
                        onChoiceCom(newValue.intValue());


                    } catch (SerialPortException e) {
                        e.printStackTrace();
                }

            }
        });

    }



    void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    public void startBalancesWeight () throws SerialPortException {

        firstW = Double.parseDouble (firstWeight.getText());
        secondW = Double.parseDouble (secondWeight.getText());

        programIsRun = true;

        System.out.println("Запуск работы программы");



//        mainApp.balances.sendRequest(Balances.REQUEST_ON_RELAY1);
//        weightLabel.setText("ON");
//        mainApp.balances.clearPortBuffer();

    }

    public void endButtonPush() throws SerialPortException {

        weight = 0; //TODO отладка
        programIsRun = false;

        System.out.println("Остановка работы программы");
//        timer.cancel();
//        balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
//        weightLabel.setText("OFF");
//        balances.clearPortBuffer();
    }

    /**
     * Обработка нажатия кнопки меню для получения списка ком портов
     */
    public void onClickMenuItem (){
        System.out.println("Нажал кнопку меню");
        listCom = FXCollections.observableArrayList(SerialPortList.getPortNames());
        choiceCom.setItems(listCom);
    }

    /**
     * Обработка нажатия кнопки торировки
     */
    public void onClickTorrirovka() throws SerialPortException {
        System.out.println("Нажата кнопка торировки");

        if (balances != null && balances.getSerialPort().isOpened()) {
            pause = true;
            balances.sendRequest(Balances.REQUEST_TOR);
            pause = false;
        }
    }

    /**
     * Обработка нажатия кнопки включения и выключения весов
     */
    public void onClickOnOff () throws SerialPortException {
        System.out.println("Нажата кнопка Вкл/Выкл");

        if (balances != null && balances.getSerialPort().isOpened()) {
            pause = true;
            balances.sendRequest(Balances.REQUEST_ON_OFF);
            pause = false;
        }
    }


    /**
     * Обработка выбраного ком порта с пересозданием нового класса.
     * @param choiceIndex Выбраный индекс в списке
     * @throws SerialPortException
     */
    private void onChoiceCom (int choiceIndex) throws SerialPortException {
        String comIndex = listCom.get(choiceIndex) ;
        System.out.println("Выбран новый ком порт - "+comIndex);

        comIndex = comIndex.trim();
        comIndex = comIndex.replace("COM","");

        if ( balances != null && balances.getSerialPort().isOpened()) {
            balances.closePort();
            balances = null;
        }

        balances = new Balances(Integer.parseInt(comIndex));

        if (timer != null && balances.getSerialPort().isOpened()) {
            timer.cancel();
            timer.purge();

            timer = new Timer();
            myTimerTask = new MyTimerTask();


        timer.schedule(myTimerTask, 500,500);
        }

    }

    public void stopTask() throws SerialPortException {
        timer.cancel();
        timer.purge();
        balances.closePort();
        balances = null;
    }

    /**
     * Класс таймер для циклического опроса.
     */
    class MyTimerTask extends TimerTask {

        private boolean relayOneOn = false;
        private boolean relayTwoOn = false;

        @Override
        public void run() {

            Platform.runLater(new Runnable() {
                @Override public void run() {
                    weightLabel.setText(Double.toString(weight));  //TODO отладка
                }
            });


            if (!pause) {
//                try {
//                    balances.sendRequest(Balances.REQUEST_WEIGHT);
//
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
            }
            try {
                if (programIsRun) {

                    weight = weight + 0.01; //TODO отладка

                    if (weight < firstW && !relayOneOn) {
                        System.out.println("Включение первого реле");

                        pause = true;
                        balances.sendRequest(Balances.REQUEST_ON_RELAY1);
                        Thread.sleep(200);
                        balances.sendRequest(Balances.REQUEST_OFF_RELAY2);
                        pause = false;
                        relayOneOn = true;
                        relayTwoOn = false;

                    } else if (firstW <= weight && weight < secondW && !relayTwoOn) {
                        System.out.println("Включение второго реле");

                        pause = true;
                        balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                        Thread.sleep(200);
                        balances.sendRequest(Balances.REQUEST_ON_RELAY2);

                        pause = false;
                        relayOneOn = false;
                        relayTwoOn = true;

                    } else if ( (secondW <= weight || weight < 0) && (relayOneOn || relayTwoOn)){
                        System.out.println("Отключение обоих реле");

                        pause = true;
                        balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                        Thread.sleep(200);
                        balances.sendRequest(Balances.REQUEST_OFF_RELAY2);
                        pause = false;
                        relayOneOn = false;
                        relayTwoOn = false;
                    }
                }else if (relayOneOn || relayTwoOn){
                    relayOneOn = false;
                    relayTwoOn = false;
                    balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                    Thread.sleep(5000);
                    balances.sendRequest(Balances.REQUEST_OFF_RELAY2);
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
