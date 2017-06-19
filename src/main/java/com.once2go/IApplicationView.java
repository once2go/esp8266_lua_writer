package com.once2go;

/**
 * Created by once2go on 14/06/17.
 */
public interface IApplicationView {

    void onDevicesReceived(String[] devices, int[] speedRates);

    void onFlashingStarted();

    void onFlashProgress(int progress, String progressStatus);

    void onErrorReceived(String errorDescription);

    void onFlashingFinished();

    void onLineFromUartReceived(String rxLine);
}
