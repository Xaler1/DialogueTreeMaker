package Helpers;

import Frames.MainWindow;
import Panels.NodePanel;

import javax.swing.*;
import java.awt.*;

public class OutConnector extends JLabel {
    private final NodePanel parent;
    private final MainWindow window;
    public InConnector destination;

    public OutConnector(NodePanel parent, MainWindow window) {
        this.parent = parent;
        ImageIcon icon = new ImageIcon("imgs/out_connector.png");
        setIcon(icon);
        this.window = window;
        addMouseListener(new OutListener(window, this));
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

    public void setDestination(InConnector connector) {
        destination = connector;
    }
}
