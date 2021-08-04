package Helpers;

import Frames.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InListener extends MouseAdapter {
    private final MainWindow window;
    private final InConnector component;

    public InListener(MainWindow window, InConnector component) {
        this.window = window;
        this.component = component;
    }

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
