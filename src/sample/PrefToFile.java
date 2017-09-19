package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;

public  class PrefToFile {

    private File file;
    private ObservableList<BalancesPrefModel> list;

    public PrefToFile() {
    }

    public PrefToFile (File file) {
        this.file = file;
    }

    public PrefToFile(File file, ObservableList<BalancesPrefModel> list){
        this.file = file;
        this.list = list;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ObservableList<?> getList() {
        return list;
    }

    public void setList(ObservableList<BalancesPrefModel> list) {
        this.list = list;
    }

    public void saveListToFile(){
        try {
            FileWriter fileWriter = new FileWriter(file,false);
            Iterator<BalancesPrefModel> listIterator = list.iterator();
            while (listIterator.hasNext()){
                BalancesPrefModel prefModel = listIterator.next();
                fileWriter.write(prefModel.getNameProduct()+";"+
                        prefModel.getFirstWeight()+";"+
                        prefModel.getSecondWeight()+";"+
                        prefModel.getTimeOnSecR()+";"+
                        prefModel.getTimeOffSecR()+";"+
                        prefModel.getDeltaLimit()+"\r\n");
            }
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<BalancesPrefModel> readListFromFile() {
        list = FXCollections.observableArrayList();
        try (FileReader fileReader = new FileReader(file)) {
            Scanner fileScanner = new Scanner(fileReader);
            while (fileScanner.hasNextLine()) {
                String str = fileScanner.nextLine();
                int i = str.indexOf(";");
                BalancesPrefModel prefModel = new BalancesPrefModel(str.substring(0, i));

                str = str.substring(i + 1);
                i = str.indexOf(";");
                prefModel.setFirstWeight(str.substring(0, i));

                str = str.substring(i + 1);
                i = str.indexOf(";");
                prefModel.setSecondWeight(str.substring(0, i));

                str = str.substring(i + 1);
                i = str.indexOf(";");
                prefModel.setTimeOnSecR(str.substring(0, i));

                str = str.substring(i + 1);
                i = str.indexOf(";");
                prefModel.setTimeOffSecR(str.substring(0, i));

                str = str.substring(i + 1);
                prefModel.setDeltaLimit(str.substring(0, str.length()));

                list.add(prefModel);
            }
            fileReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
