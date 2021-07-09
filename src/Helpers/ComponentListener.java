package Helpers;

import Frames.MainWindow;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentListener extends MouseAdapter {
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
            System.out.println("here");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            window.endMouse();
        }
    }
}
