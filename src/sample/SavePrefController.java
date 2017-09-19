package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class SavePrefController {

    @FXML
    private TextField nameNewPrefTextField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private Main main;
    private ObservableList<BalancesPrefModel> balancesPrefList;
    private Stage dialogStage;



    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMain (Main main){
        this.main = main;
    }

    public ObservableList<BalancesPrefModel> getBalancesPrefList() {
        return balancesPrefList;
    }

    public void setBalancesPrefList(ObservableList<BalancesPrefModel> balancesPrefList) {
        this.balancesPrefList = balancesPrefList;
    }

    @FXML
    private void onOkButtonClick(){
       String text = nameNewPrefTextField.getText();
       if (!text.equals("") ){

           balancesPrefList.add(main.getPrefModel(text));
           main.setBalancesPrefList(balancesPrefList);

           dialogStage.close();
       } else {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Error");
           alert.setHeaderText("");
           alert.setContentText("Введите значение в поле!");
           alert.showAndWait();
       }
    }

    @FXML
    private void onCancelButtonClock(){
        dialogStage.close();
    }


}
