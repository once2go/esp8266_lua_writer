package com.once2go;

/**
 * Created by once2go on 14/06/17.
 */
public interface IApplicationViewPresenter {

    void setApplicationView(IApplicationView view);

    void discoverPorts();

    boolean connectToPort(String portName, int portSpeed);

    void findInitFile(String pathToInit);

    void flash();

    void disconnect();

}
