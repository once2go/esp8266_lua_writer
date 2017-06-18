package com.once2go;

import javax.swing.*;

/**
 * Created by once2go on 14/06/17.
 */
public class ApplicationView {

    private IApplicationViewPresenter mIApplicationViewPresenter;

    public ApplicationView() {
        mIApplicationViewPresenter = new AppViewPresenterImplementation();
        JFrame frame = new JFrame("ESP flash tool");
        frame.setContentPane(new EspFlashUIController(mIApplicationViewPresenter).getMainWindow());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        mIApplicationViewPresenter.discoverPorts();
    }
}
