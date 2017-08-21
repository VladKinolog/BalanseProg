package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BalancesPrefModel {



    private StringProperty nameProduct;
    private StringProperty firstWeight;
    private StringProperty secondWeight;
    private StringProperty timeOnSecR;
    private StringProperty timeOffSecR;
    private StringProperty deltaLimit;

    public BalancesPrefModel (){
        this(null);
    }

    public BalancesPrefModel (String nameProduct) {
        this.nameProduct = new SimpleStringProperty(nameProduct);
    }

    public String getNameProduct() {
        return nameProduct.get();
    }

    // Сеттеры и геттеры
    public StringProperty nameProductProperty() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct.set(nameProduct);
    }

    public String getFirstWeight() {
        return firstWeight.get();
    }

    public StringProperty firstWeightProperty() {
        return firstWeight;
    }

    public void setFirstWeight(String firstWeight) {
        this.firstWeight.set(firstWeight);
    }

    public String getSecondWeight() {
        return secondWeight.get();
    }

    public StringProperty secondWeightProperty() {
        return secondWeight;
    }

    public void setSecondWeight(String secondWeight) {
        this.secondWeight.set(secondWeight);
    }

    public String getTimeOnSecR() {
        return timeOnSecR.get();
    }

    public StringProperty timeOnSecRProperty() {
        return timeOnSecR;
    }

    public void setTimeOnSecR(String timeOnSecR) {
        this.timeOnSecR.set(timeOnSecR);
    }

    public String getTimeOffSecR() {
        return timeOffSecR.get();
    }

    public StringProperty timeOffSecRProperty() {
        return timeOffSecR;
    }

    public void setTimeOffSecR(String timeOffSecR) {
        this.timeOffSecR.set(timeOffSecR);
    }

    public String getDeltaLimit() {
        return deltaLimit.get();
    }

    public StringProperty deltaLimitProperty() {
        return deltaLimit;
    }

    public void setDeltaLimit(String deltaLimit) {
        this.deltaLimit.set(deltaLimit);
    }


}
