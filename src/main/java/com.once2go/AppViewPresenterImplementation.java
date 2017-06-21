package com.once2go;

import jssc.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by once2go on 14/06/17.
 */
public class AppViewPresenterImplementation implements IApplicationViewPresenter, SerialPortEventListener {

    private static final long DELAY = 400;
    private IApplicationView mView;
    private SerialPort mSerialPort;
    private String mProjectPath;
    private ArrayList<String> mProjectFileList;
    private String mUartRxBuilder = "";

    public void setApplicationView(IApplicationView view) {
        if (view == null) {
            throw new IllegalArgumentException("View can't be null");
        }
        mView = view;
    }

    public void discoverPorts() {
        String[] names = SerialPortList.getPortNames();
        if (mView != null) {
            mView.onDevicesReceived(names, new int[]{
                    SerialPort.BAUDRATE_1200,
                    SerialPort.BAUDRATE_4800,
                    SerialPort.BAUDRATE_9600,
                    SerialPort.BAUDRATE_57600,
                    SerialPort.BAUDRATE_115200
            });
        }
    }

    public boolean connectToPort(String portName, int portSpeed) {
        try {
            disconnect();
            mSerialPort = new SerialPort(portName);
            mSerialPort.openPort();
            mSerialPort.setParams(portSpeed,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            mSerialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            mSerialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
            return true;
        } catch (SerialPortException ex) {
            if (mView != null) {
                mView.onErrorReceived(ex.getExceptionType());
            }
            return false;
        }
    }

    public void findInitFile(String path) {
        mProjectPath = path;
        mProjectFileList = new ArrayList<String>();
        File curDir = new File(path);
        File[] filesList = curDir.listFiles();
        for (File f : filesList) {
            if (f.isFile()) {
                String name = f.getName();
                if (name.substring(name.length() - 4, name.length()).equals(".lua")) {
                    mProjectFileList.add(f.getName());
                }
            }
        }
    }

    public void flash() {
        new Thread(() -> {
            mView.onFlashingStarted();
            for (String file : mProjectFileList) {
                LinkedList<String> cmdList = Converter.convert(mProjectPath, file);
                int iterator = 0;
                if (cmdList != null) {
                    int cmdSize = cmdList.size();
                    for (String cmd : cmdList) {
                        iterator++;
                        try {
                            mSerialPort.writeString(cmd);
                            int progress = (int) (((float) iterator / (float) cmdSize) * 100);
                            mView.onFlashProgress(progress, "Flashing: " + file);
                            Thread.sleep(DELAY);
                        } catch (SerialPortException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            mView.onFlashingFinished();
        }).start();
    }

    @Override
    public void disconnect() {
        if (mSerialPort != null) {
            try {
                mSerialPort.closePort();
                mSerialPort = null;
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        try {
            mUartRxBuilder += mSerialPort.readString(serialPortEvent.getEventValue());
            if (mUartRxBuilder.contains("\r")) {
                String[] split = mUartRxBuilder.split("\r");
                int splitSize = split.length;
                for (int i = 0; i < splitSize - 1; i++) {
                    mView.onLineFromUartReceived(split[i]);
                }
                mUartRxBuilder = split[splitSize - 1];
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}

