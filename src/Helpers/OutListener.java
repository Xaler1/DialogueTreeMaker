package Helpers;

import Frames.MainWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

public class OutListener extends MouseAdapter implements Serializable {
    private final MainWindow window;
    private final OutConnector component;

    public OutListener(MainWindow window, OutConnector component) {
        this.window = window;
        this.component = component;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            window.trackConnect(component, component.getCenter());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        window.endMouse();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.isAltDown()) {
            component.removeConnection();
        }
    }
}
