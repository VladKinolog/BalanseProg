package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


/**
 * Контроллер для класса настройки
 */
public class BalancesPropController {

    @FXML
    private TableView<BalancesPrefModel> balancesPrefTable;
    @FXML
    private TableColumn<BalancesPrefModel, String> balancesPrefTableColumn;

    @FXML
    private Label firstWeight;
    @FXML
    private Label secondWeight;
    @FXML
    private Label setTimeOnSecR;
    @FXML
    private Label setTimeOffSecR;
    @FXML
    private Label setDeltaLimit;
    @FXML
    private Button okButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button cancelButton;

    private Main main;

    private ObservableList<BalancesPrefModel> balancesPrefList;
    private Stage dialogStage;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {
        // Инициализация таблицы адресатов с двумя столбцами.
        balancesPrefTableColumn.setCellValueFactory(cellData -> cellData.getValue().nameProductProperty());

        showSavedPrefDetail(null);

        balancesPrefTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showSavedPrefDetail(newValue));
    }

    /*
     ссылка на главное приложение
      */
    public void setMain (Main main){
        this.main = main;

        balancesPrefTable.setItems(main.getBalancesPrefList());
    }

    public ObservableList<BalancesPrefModel> getBalancesPrefList() {
        return balancesPrefList;
    }

    public void setBalancesPrefList(ObservableList<BalancesPrefModel> balancesPrefList) {
        this.balancesPrefList = balancesPrefList;
    }

    @FXML
    private void onOkButtonClick(){
        int selectIndex = balancesPrefTable.getSelectionModel().getSelectedIndex();
        if (selectIndex < 0){
            dialogStage.close();
        } else {
            main.setPrefModel(balancesPrefTable.getItems().get(selectIndex));
            dialogStage.close();
        }
    }

    @FXML
    private void onDeleteButtonClick(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удалить запись");
        alert.setHeaderText("Удалить сохраненную запись?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int selectIndex = balancesPrefTable.getSelectionModel().getSelectedIndex();
                if (selectIndex >= 0) {
                    balancesPrefTable.getItems().remove(selectIndex);
                    main.setBalancesPrefList(balancesPrefTable.getItems());
                }
            }
        });


    }

    @FXML
    private void onCancelButtonClick(){
        dialogStage.close();
    }

    private void showSavedPrefDetail (BalancesPrefModel balancesPref) {

        if (balancesPref != null){
            firstWeight.setText(balancesPref.getFirstWeight());
            secondWeight.setText(balancesPref.getSecondWeight());
            setTimeOnSecR.setText(balancesPref.getTimeOnSecR());
            setTimeOffSecR.setText(balancesPref.getTimeOffSecR());
            setDeltaLimit.setText(balancesPref.getDeltaLimit());

        }else {
            firstWeight.setText("");
            secondWeight.setText("");
            setTimeOnSecR.setText("");
            setTimeOffSecR.setText("");
            setDeltaLimit.setText("");
        }
    }
}
