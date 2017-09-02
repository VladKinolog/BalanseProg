package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

import java.text.DecimalFormat;
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
    private Button refresh;
    @FXML
    private Button offBalanse;
    @FXML
    private TextField firstWeight;
    @FXML
    private TextField secondWeight;
    @FXML
    private TextField setTimeOnSecR;
    @FXML
    private TextField setTimeOffSecR;
    @FXML
    private TextField setDeltaLimit;
    @FXML
    private ChoiceBox choiceCom;
    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane pane;



    @FXML
    private Menu menuFile;
    @FXML
    private ProgressIndicator progressIndicator;

    private double weight;// = 0;
    private double firstW;
    private double secondW;
    private double deltaLimit;

    private ObservableList<String> listCom;



    private int numberCom;

    private Balances balances;
    private Timer timer = new Timer(true);
    MyTimerTask myTimerTask = new MyTimerTask();
    Timer torTimer;
    TarirovkaTimer tarirovkaTimer;
    private Main mainApp;

    private static volatile boolean  pause = false;
    private static volatile boolean programIsRun = false;
    private static volatile boolean onTarirovka = false;
    private static int iterErrorRead = 0;
    private static volatile long setTimeOnSecRelay;
    private static volatile long setTimeOffSecRelay;


    Image imageRefreshBut;

    public Controller(){
    }


    @FXML
    private void initialize(){

        System.out.println("Инициализация класса контроллера");
        progressIndicator.setVisible(false);

//        Tooltip tooltip = new Tooltip();
//        tooltip.setText("Значение веса для быстрой загрузки (grein)");
//        tooltip.setFont(new Font(16));

        firstWeight.setTooltip(new MyToolTip("Значение веса для быстрой загрузки (grein)",16));
        secondWeight.setTooltip(new MyToolTip("Значение веса для точной загрузки (grein)", 16));
        setTimeOffSecR.setTooltip(new MyToolTip("Время стабилизации веса (сек.)",16));
        setTimeOnSecR.setTooltip (new MyToolTip("Время догрузки при медленой подачи (сек.)",16));
        torirovkaButton.setTooltip(new MyToolTip("Тарировка",16));
        refresh.setTooltip(new MyToolTip("Попытка востановить связь с весами",16));
        startButton.setTooltip(new MyToolTip("Запуск взвешивания",16));
        endButton.setTooltip(new MyToolTip("Остановка взвешивания",16));
        offBalanse.setTooltip(new MyToolTip("Выключение весов",16));
        setDeltaLimit.setTooltip(new MyToolTip("Предел точности взвешивания",16));


        imageRefreshBut = new Image(String.valueOf(getClass().getResource("/resources/refresh.png")));

        refresh.setGraphic(new ImageView(imageRefreshBut));




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

    public void createPort (int numComPort)  {
        if ( balances != null && balances.getSerialPort().isOpened()) {
            try {
                balances.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();

            }
            balances = null;
        }

        try {
            balances = new Balances(numComPort);
        } catch (SerialPortException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ошибка комуникации");
            alert.setContentText("COM-"+ numComPort +" Занят другим устройством или не существует");
            alert.showAndWait();
        }

        if (balances != null && timer != null && balances.getSerialPort().isOpened()) {
            timer.cancel();
            timer.purge();

            timer = new Timer(true);
            myTimerTask = new MyTimerTask();

            timer.schedule(myTimerTask, 300,300);
        }
    }

    public void startBalancesWeight () throws SerialPortException {
        int posIndex;

        String firstWStr = firstWeight.getText();
        String secondWStr = secondWeight.getText();
        String setTimeOnSecRText = setTimeOnSecR.getText();
        String setTimeOffSecRText = setTimeOffSecR.getText();
        String setDeltaLimit = this.setDeltaLimit.getText();

        firstWStr = firstWStr.replace(",",".").trim();
        secondWStr = secondWStr.replace(",",".").trim();
        setDeltaLimit = setDeltaLimit.replace(",",".").trim();

        setTimeOnSecRText = setTimeOnSecRText.replace(",",".").trim();

        if (setTimeOnSecRText.contains(".")) {

            setTimeOnSecRText = setTimeOnSecRText.substring(0,setTimeOnSecRText.indexOf(".") + 2);

            if (!setTimeOnSecRText.endsWith("0")) setTimeOnSecRText = (setTimeOnSecRText.substring(0, setTimeOnSecRText.indexOf(".") + 1)) + "5";
        } else setTimeOnSecRText = setTimeOnSecRText + ".0";

        setTimeOffSecRText = setTimeOffSecRText.replace(",",".").trim();

        if (setTimeOffSecRText.contains(".")) {

            setTimeOffSecRText = setTimeOffSecRText.substring(0, setTimeOffSecRText.indexOf(".") + 2);

            if (!setTimeOffSecRText.endsWith("0")) setTimeOffSecRText = (setTimeOffSecRText.substring(0,setTimeOffSecRText.indexOf(".") + 1 )) + "5";

        } else setTimeOffSecRText = setTimeOffSecRText + ".0";

        if (firstWStr.equals("") || secondWStr.equals("") || setDeltaLimit.equals("")){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("");
            alert.setContentText("Введите величины веса в соответсвующие поля");
            alert.showAndWait();
            return;
        }

        if (setTimeOnSecRText.equals("") ) setTimeOnSecRText = "0";
        if (setTimeOffSecRText.equals("")) setTimeOffSecRText = "0";
        if (setDeltaLimit.equals("")) setDeltaLimit = "0";


        try {

            firstW = Double.parseDouble(firstWStr);
            secondW = Double.parseDouble(secondWStr);
            deltaLimit = Double.parseDouble(setDeltaLimit);

            deltaLimit = Math.abs(deltaLimit);

            this.setTimeOnSecR.setText(setTimeOnSecRText);
            setTimeOnSecRText = setTimeOnSecRText.replace(".","");
            this.setTimeOffSecR.setText(setTimeOffSecRText);
            setTimeOffSecRText = setTimeOffSecRText.replace(".","");

            this.setDeltaLimit.setText(Double.toString(deltaLimit));


            if (firstW >= 0 || secondW >= 0) {

                if (firstW < secondW) {

                    setTimeOnSecRelay = Math.abs (Long.parseLong(setTimeOnSecRText));
                    //setTimeOnSecR.setText(Long.toString(setTimeOnSecRelay));
                    setTimeOnSecRelay = setTimeOnSecRelay * 100;
                   // System.out.println(setTimeOnSecRelay );

                    setTimeOffSecRelay = Math.abs (Long.parseLong(setTimeOffSecRText));
                    //setTimeOffSecR.setText(Long.toString(setTimeOffSecRelay));
                    setTimeOffSecRelay = setTimeOffSecRelay * 100 + setTimeOnSecRelay;
                   // System.out.println(setTimeOffSecRelay);

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("");
                    alert.setContentText("Первый вес должен быть меньше второго");
                    alert.showAndWait();
                    return;
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("");
                alert.setContentText("Данные в полях задержки и времени работы должны быть положительными!");
                alert.showAndWait();
                return;
            }

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

    public synchronized void endButtonPush() {

        programIsRun = false;
        progressIndicator.setVisible(false);

        System.out.println("Остановка работы программы");

        try {

        Thread.sleep(TIME_TREED_SLEEP);
        balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
        Thread.sleep(TIME_TREED_SLEEP);
        balances.sendRequest(Balances.REQUEST_OFF_RELAY2);

        } catch (SerialPortException | InterruptedException  e) {
            e.printStackTrace();
        }

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


        if (balances != null && balances.getSerialPort().isOpened() && !programIsRun ) {
            pause = true;
            onTarirovka = true;

            torTimer = new Timer(true);
            tarirovkaTimer = new TarirovkaTimer();

            torTimer.schedule(tarirovkaTimer, 5000);

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

    public void onClickRefresh () {

        createPort(numberCom);
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

    private void playSound(int i){
        PlaySoundThread playThread = new PlaySoundThread(i);

        Thread thread = new Thread(playThread);
        thread.setDaemon(true);
        thread.start();
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

    public TextField getSetTimeOnSecR() {
        return setTimeOnSecR;
    }

    public TextField getSetTimeOffSecR() {
        return setTimeOffSecR;
    }

    public TextField getSetDeltaLimit() {
        return setDeltaLimit;
    }

    public void setSecondWeight(TextField secondWeight) {
        this.secondWeight = secondWeight;
    }

    // утелита для изменения цвета лебла веса в другом потоке
    private void changeLabelColor(Paint paint) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                weightLabel.setTextFill(paint);
                weightGramLabel.setTextFill(paint);
            }
        });
    }

    private void changeLabelColor(String color) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                weightLabel.setStyle("-fx-background-color:"+ color);
            }
        });
    }

    private void changePanelColor(String color) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.setStyle("-fx-background-color:"+ color);
            }
        });
    }


    class TarirovkaTimer extends TimerTask{

        @Override
        public void run(){
            pause = false;
        }
    }

    /**
     * Класс таймер для циклического опроса.
     */
    class MyTimerTask extends TimerTask {

        private boolean relayOneOn = false;
        private boolean relayTwoOn = false;
        private boolean finalPause = false;
        private boolean timeIsSaved = false;
        private boolean soundIsPlayed = false;
        private long timeOnSecondRelay = 0;
        private long timeOffSecondRelay = 0;
        private int iterStopDelay = 0;
        long z;


        @Override
        public void run() {
            System.out.println("Время полного цикла" + (System.currentTimeMillis() - z));
            z = System.currentTimeMillis();


            // Подсвечивание когда значение находится в заданном пределе
            if ( weight > (secondW - deltaLimit) && weight <= (secondW + deltaLimit)) {
                //changeLabelColor(Color.GREEN);
                //changeLabelColor("#33F449;");
                changePanelColor("#33F449;");
                // Остановка программы с задержкой.
                if (iterStopDelay > 4) {
                    endButtonPush();
                }
                iterStopDelay ++;
                System.out.println("iterStopDelay = " + iterStopDelay);
            } else if (weight > (secondW + deltaLimit)) {
                //changeLabelColor(Color.RED);
                //changeLabelColor("#ff4949;");
                changePanelColor("#ff4949;");
                endButtonPush();
            } else {
                iterStopDelay = 0;
                //changeLabelColor(Color.BLACK);
                //changeLabelColor("transparent;");
                changePanelColor("transparent;");
            }


            if (pause && onTarirovka) {
                try {
                    balances.sendRequest(Balances.REQUEST_TOR);
                    onTarirovka = false;
                    Thread.sleep(50);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            weightLabel.setText("Тарировка");
                            weightGramLabel.setText("Тарировка");
                        }
                    });

                } catch (SerialPortException | InterruptedException e) {
                    e.printStackTrace();
                }
            }




            if (programIsRun) {

            } else  {
                timeOnSecondRelay = 0;
                timeIsSaved = false;
                //changePanelColor("transparent;");
                iterStopDelay = 0;
            }

            if (!pause) {
                try {
                    balances.sendRequest(Balances.REQUEST_WEIGHT);
                    String str = "";


                    try {
                        str = new String(Balances.convertResponse(balances.getResponse()));
                        //System.out.println(str);
                        iterErrorRead = 0;

                    } catch (SerialPortTimeoutException e) {
                        iterErrorRead++;
                        System.out.println("iterErrorRead = " + iterErrorRead);
                        System.out.println("Ошибка времени соеденения");


                        if (iterErrorRead == 4) {
                            iterErrorRead = 0;

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    weightLabel.setText("Ошибка");
                                    weightGramLabel.setText("Ошибка");
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Ошибка комуникации");
                                alert.setContentText("Превышен интервал опроса. Нет ответа от устройства.");
                                alert.showAndWait();
                                }
                            });

                            if (timer != null) {
                                timer.cancel();
                                timer.purge();
                            }



                        } else {
                            createPort(numberCom);
                        }

                        e.printStackTrace();

                    }

                    if (!str.equals("")) {
                        str = str.trim();
                        str = str.replace(" ","");
                        try {
                            weight = Double.parseDouble(str);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            weight = 99;
                        }
                        weight = ConvertGrainGram.gramToGrain(weight);
                        String weightStr = new DecimalFormat("#0.00000").format(weight);
                        String finalStr = weightStr;
                        String finalGramWeight = str;

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
                //finalPause = pause;
            }
            /*
             * блок управления реле.
             */
                try {
                    if (programIsRun) {

                        //Если вес меньше первого веса
                        if  (weight < firstW /*&& !relayOneOn*/) {
                            System.out.println("Включение первого реле");

                            pause = true;
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_ON_RELAY1);
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_ON_RELAY2);



                            pause = false;
                            relayOneOn = true;
                            relayTwoOn = true;
                            timeIsSaved = false;
                            soundIsPlayed = true;

                         // Вес в промежутке между первым и вторым
                        } else if (firstW <= weight && weight < (secondW - deltaLimit)) {
                            System.out.println("Включение второго реле" + (System.currentTimeMillis() - timeOnSecondRelay));

                            if (!timeIsSaved) {
                                timeOnSecondRelay = System.currentTimeMillis();
                                Thread.sleep(TIME_TREED_SLEEP);
                                balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                            }

                            if (soundIsPlayed) playSound(1);

                            soundIsPlayed = false;
                            timeIsSaved = true;


                            if ((System.currentTimeMillis() - timeOnSecondRelay) > setTimeOnSecRelay && !relayTwoOn){
                                pause = true;
                                System.out.println("Режим паузы во время догрузки ");
                                Thread.sleep(TIME_TREED_SLEEP);
                                balances.sendRequest(Balances.REQUEST_ON_RELAY2);
                                relayTwoOn = true;
                                pause = false;

                            } else if ((System.currentTimeMillis() - timeOnSecondRelay) > setTimeOffSecRelay && relayTwoOn ) {
                                pause = true;
                                System.out.println("Режим  догрузки ");
                                Thread.sleep(TIME_TREED_SLEEP);
                                if (setTimeOnSecRelay != 0) balances.sendRequest(Balances.REQUEST_OFF_RELAY2);
                                relayTwoOn = false;
                                pause = false;
                                timeIsSaved = false;
                            }



                        //Вес больше второго - интервал достоверности
                        } else if (weight >= (secondW - deltaLimit)  && (relayOneOn || relayTwoOn)) {
                            System.out.println("Отключение обоих реле");

                            pause = true;
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
                            Thread.sleep(TIME_TREED_SLEEP);
                            balances.sendRequest(Balances.REQUEST_OFF_RELAY2);

                            playSound(2);

                            pause = false;
                            relayOneOn = false;
                            relayTwoOn = false;
                            timeIsSaved = false;


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

                System.out.println("Время хода программы опроса" + (System.currentTimeMillis() - z));
            }
        }

        class PlaySoundThread implements Runnable{
        private int i;

        public PlaySoundThread(int i){
            this.i = i;
        }
            @Override
            public void run() {
                switch (i) {
                    case 1:
                        mainApp.clip.play(1.0);
                        break;
                    case 2:
                        mainApp.finichClip.play(1.0);
                        break;
                }
            }
        }


    }
