package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        //balancesPrefTableColumn.setCellValueFactory(cellData -> cellData.getValue().nameProductProperty());
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
}
