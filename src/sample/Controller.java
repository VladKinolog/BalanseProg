package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jssc.SerialPortException;

import java.util.Timer;
import java.util.TimerTask;

public class Controller {


    @FXML
    private Label weightLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button endButton;
    @FXML
    private TextField firstWeight;
    @FXML
    private TextField secondWeight;


    private Balances balances;
    Timer timer;


    private Main mainApp;

    public Controller(){

    }

     void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    public void startBalancesWeight () throws SerialPortException {

        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();


        timer.schedule(myTimerTask, 500,500);


//        mainApp.balances.sendRequest(Balances.REQUEST_ON_RELAY1);
//        weightLabel.setText("ON");
//        mainApp.balances.clearPortBuffer();

    }

    public void endButtonPush() throws SerialPortException {

        timer.cancel();
        mainApp.balances.sendRequest(Balances.REQUEST_OFF_RELAY1);
        weightLabel.setText("OFF");
        mainApp.balances.clearPortBuffer();
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                mainApp.balances.sendRequest(Balances.REQUEST_ON_RELAY1);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            //weightLabel.setText("ON");
            //mainApp.balances.clearPortBuffer();
        }
    }


}
