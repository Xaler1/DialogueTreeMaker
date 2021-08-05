package Helpers;

import Frames.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
    This handles mouse operations on in connectors - setting it as a potential end point for a link being created,
    deleting all the connections.
 */
public class InListener extends MouseAdapter {
    private final MainWindow window;
    private final InConnector component;

    public InListener(MainWindow window, InConnector component) {
        this.window = window;
        this.component = component;
    }

    /*
        This detects when the user's mouse enters the in connector and sets it as a potential end point for a connection
        being made.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        window.potential_end_component = component.getParent();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        window.potential_end_component = null;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        window.endMouse();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.isAltDown()) {
            component.removeConnections();
        }
    }
}
