package sample;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

/**
 * Created by VLAD on 10.07.2017.
 * формат посылки с весов  53 23 23 24 84 64 84 85 65 33 23 21 03 32 13 10
 *
 * Включение реле 1 - 01 05 00 00 FF 00 8C 3A
 * Выключение реле 1 - 01 05 00 00 00 00 CD CA
 */
public class Balances {
    public static final String REQUEST_WEIGHT = "SI" + "\r" + "\n";
    public static final String REQUEST_TOR = "ST" + "\r" + "\n";
    public static final String REQUEST_ON_OFF = "SS" + "\r" + "\n";
    public static final int []  REQUEST_ON_RELAY1 = {0x01,0x05,0x00,0x00,0xFF,0x00,0x8C,0x3A};
    public static final int []  REQUEST_OFF_RELAY1 = {0x01,0x05,0x00,0x00,0x00,0x00,0xCD,0xCA};
    public static final int []  REQUEST_ON_RELAY2 = {0x01,0x05,0x00,0x01,0xFF,0x00,0xDD,0xFA};
    public static final int []  REQUEST_OFF_RELAY2 = {0x01,0x05,0x00,0x01,0x00,0x00,0x9C,0x0A};

    private SerialPort serialPort;
    private byte [] response;
    //private static boolean checkSumCheck;

    public Balances() {
        this(1);

    }

    public Balances(int numberComPort) {
        String comPort = "COM"+numberComPort;

        serialPort = new SerialPort(comPort);


        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    public boolean sendRequest (String request) throws SerialPortException {
        if (serialPort != null) {
            serialPort.writeBytes(request.getBytes());
            return true;
        }else return false;

    }

    public boolean sendRequest (int [] request) throws SerialPortException {
        if (serialPort != null) {
            serialPort.writeIntArray(request);
            return true;
        }else return false;

    }

    public void getResponse () {
        try {
            if (serialPort != null) {
                response = serialPort.readBytes(16, 200);
            }
        } catch (SerialPortTimeoutException e){
            System.out.println("Превышение интервала ожидания");
            e.printStackTrace();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void closePort () throws SerialPortException {
        if (serialPort.isOpened()) serialPort.closePort();

    }

    public void clearPortBuffer () throws SerialPortException {
        if (serialPort.isOpened()) serialPort.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
    }


    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }
}

