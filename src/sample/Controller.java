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
import jssc.SerialPortTimeoutException;
import sun.rmi.runtime.Log;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import static jssc.SerialPortList.getPortNames;

public class Controller {

    private final static int TIME_TREED_SLEEP = 10;


    @FXML
    private Label weightLabel;
    @FXML
    private Label weightGramLabel;
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
    @FXML
    private ProgressIndicator progressIndicator;

    private double weight;// = 0;
    private double firstW;
    private double secondW;

    private ObservableList<String> listCom;



    private int numberCom;

    private Balances balances;
    private Timer timer = new Timer();
    MyTimerTask myTimerTask = new MyTimerTask();
    private Main mainApp;

    private static volatile boolean  pause = false;
    private static volatile boolean programIsRun = false;



    public Controller(){
    }


    @FXML
    private void initialize(){

        System.out.println("Инициализация класса контроллера");
        progressIndicator.setVisible(false);

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

    public void createPort (int numComPort) {
        if ( balances != null && balances.getSerialPort().isOpened()) {
            try {
                balances.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            balances = null;
        }

        balances = new Balances(numComPort);

        if (timer != null && balances.getSerialPort().isOpened()) {
            timer.cancel();
            timer.purge();

            timer = new Timer();
            myTimerTask = new MyTimerTask();


            timer.schedule(myTimerTask, 500,500);
        }
    }

    public void startBalancesWeight () throws SerialPortException {

        String firstWStr = firstWeight.getText();
        String secondWStr = secondWeight.getText();
        firstWStr = firstWStr.replace(",",".").trim();
        secondWStr = secondWStr.replace(",",".").trim();

        if (firstWStr.equals("") || secondWStr.equals("") ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("");
            alert.setContentText("Введите величины веса в соответсвующие поля");
            alert.showAndWait();
            return;
        }

        try {
            firstW = Double.parseDouble(firstWStr);
            secondW = Double.parseDouble(secondWStr);
            programIsRun = true;
        } catch (NumberFormatException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("");
            alert.setContentText("Неверный формат введеных данных в поле веса, должны быть числа.");
            alert.showAndWait();

        }
        progressIndicator.setVisible(true);

        System.out.println("Запуск работы программы");



//        mainApp.balances.sendRequest(Balances.REQUEST_ON_RELAY1);
//        weightLabel.setText("ON");
//        mainApp.balances.clearPortBuffer();

    }

    public void endButtonPush() {

        programIsRun = false;
        progressIndicator.setVisible(false);

        System.out.println("Остановка работы программы");

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

        numberCom = Integer.parseInt(comIndex);

        createPort(Integer.parseInt(comIndex));

    }

    public void stopTask() throws SerialPortException {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (balances != null && balances.getSerialPort().isOpened()) {
            balances.closePort();
            balances = null;
        }
    }

    public TextField getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(TextField firstWeight) {
        this.firstWeight = firstWeight;
    }

    public TextField getSecondWeight() {
        return secondWeight;
    }

    public int getNumberCom() {
        return numberCom;
    }

    public void setNumberCom(int numberCom) {
        this.numberCom = numberCom;
    }

    public void setSecondWeight(TextField secondWeight) {
        this.secondWeight = secondWeight;
    }


    /**
     * Класс таймер для циклического опроса.
     */
    class MyTimerTask extends TimerTask {

        private boolean relayOneOn = false;
        private boolean relayTwoOn = false;
        private boolean finalPause = false;

        @Override
        public void run() {


            if (!finalPause) {
                try {
                    balances.sendRequest(Balances.REQUEST_WEIGHT);
                    String str = "";

                    try {
                        str = new String(Balances.convertResponse(balances.getResponse()));
                    } catch (SerialPortTimeoutException e) {
                        System.out.println("Ошибк времени соеденения");
                        if (timer != null) {
                            timer.cancel();
                            timer.purge();
                        }


                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Ошибка комуникации");
                                alert.setContentText("Превышен интервал опроса. Нет ответа от устройства.");
                                alert.showAndWait();
                                e.printStackTrace();
                            }
                        });

                    }

                    if (!str.equals("")) {
                        str = str.trim();
                        System.out.println(str);
                        weight = Double.parseDouble(str);
                        System.out.println(weight);
                        weight = ConvertGrainGram.gramToGrain(weight);
                        String weightStr = Double.toString(weight);//.substring(0,7);
                        weightStr = new DecimalFormat("#0.00000").format(weight);
                        String finalStr = weightStr;
                        String finalGramWeight = str;
                        System.out.println(finalStr);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                weightGramLabel.setText(finalGramWeight);
                                weightLabel.setText(finalStr);
                            }
                        });
                    }

                } catch (SerialPortException e) {
                    System.out.println("Ошибк порта в методе на запрос ответ веса");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Ошибка комуникации");
                            alert.setContentText("Ошибка работы с COM портом устройства");

                            alert.showAndWait();
                            e.printStackTrace();
                        }
                    });
                }
                finalPause = pause;
            }
            /*
             * блок управления реле.
             */
                try {
                    if (programIsRun) {

                        //weight = weight + 0.01; //TODO отладка

                        if (weight < firstW && !relayOneOn) {
                            System.out.println("Включение первого реле");

                            pause = true;
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_ON_RELAY1);
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_OFF_RELAY2);
                            pause = false;
                            relayOneOn = true;
                            relayTwoOn = false;

                        } else if (firstW <= weight && weight < secondW && !relayTwoOn) {
                            System.out.println("Включение второго реле");

                            pause = true;
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_ON_RELAY2);

                            pause = false;
                            relayOneOn = false;
                            relayTwoOn = true;

                        } else if ((secondW <= weight || weight < 0) && (relayOneOn || relayTwoOn)) {
                            System.out.println("Отключение обоих реле");

                            pause = true;
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_OFF_RELAY2);
                            pause = false;
                            relayOneOn = false;
                            relayTwoOn = false;
                        }
                    } else if (relayOneOn || relayTwoOn) {
                        relayOneOn = false;
                        relayTwoOn = false;
                        Thread.sleep(TIME_TREED_SLEEP);
                        balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                        Thread.sleep(TIME_TREED_SLEEP);
                        balances.sendRequest(Balances.REQUEST_OFF_RELAY2);
                    }
                } catch (SerialPortException e) {
                    System.out.println("Ошибк порта в методе включение и выключение реле");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
