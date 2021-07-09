package Panels;

import Frames.MainWindow;
import Helpers.InConnector;
import Helpers.OutConnector;

import javax.swing.*;
import java.awt.*;

public class NodePanel extends JPanel {
    protected OutConnector out_connector;
    protected InConnector in_connector;
    protected NodePanel parent = null;
    protected final MainWindow window;

    public NodePanel(MainWindow window) {
        setLayout(null);
        this.window = window;
    }

    public NodePanel(MainWindow window, NodePanel parent) {
        setLayout(null);
        this.parent = parent;
        this.window = window;
    }

    public void setOutConnector(OutConnector connector) {
        out_connector = (connector);
    }

    public void setInConnector(InConnector connector) {
        in_connector = connector;
    }

    public InConnector getInConnector() {
        return in_connector;
    }

    public OutConnector getOutConnector() {
        return out_connector;
    }


    @Override
    public Point getLocation() {
        Point loc = super.getLocation();
        if (parent != null) {
            loc.translate(parent.getX(), parent.getY());
        }
        return loc;
    }
}
