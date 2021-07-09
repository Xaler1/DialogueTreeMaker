package Helpers;

import Frames.MainWindow;

import javax.swing.*;
import java.awt.*;

public class InConnector extends JLabel {

    private final NodePanel parent;
    private final MainWindow window;

    public InConnector(NodePanel parent, MainWindow window) {
        this.parent = parent;
        ImageIcon icon = new ImageIcon("imgs/in_connector.png");
        setIcon(icon);
        this.window = window;
        addMouseListener(new InListener(window, this));
    }

    @Override
    public NodePanel getParent() {
        return parent;
    }

    public Point getCenter() {
        Point point = parent.getLocation();
        point.translate(getX(), getY());
        point.translate(getWidth()/2, getHeight()/2);
        return point;
    }
}
