package Helpers;

import Frames.MainWindow;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

/*
    This detects when a user presses on a panel on a canvas and starts the relocation loop for that panel in the main window.
 */
public class ComponentListener extends MouseAdapter implements Serializable {
    private final MainWindow window;
    private final JPanel panel;

    public ComponentListener(MainWindow window, JPanel panel) {
        this.window = window;
        this.panel = panel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            window.trackRelocate(panel);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            window.endMouse();
        }
    }
}
