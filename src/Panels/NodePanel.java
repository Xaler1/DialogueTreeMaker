package Panels;

import Frames.MainWindow;
import Helpers.InConnector;
import Helpers.OutConnector;
import Managers.Graph;

import javax.swing.*;
import java.awt.*;

import Frames.Canvas;
import Managers.TreeKeeper;
import Nodes.Node;

public abstract class NodePanel extends JPanel {
    protected OutConnector out_connector;
    protected InConnector in_connector;
    protected NodePanel parent = null;
    protected final MainWindow window;
    protected final Canvas canvas;
    protected Graph graph = null;
    protected final TreeKeeper keeper;

    public NodePanel(MainWindow window) {
        setLayout(null);
        this.window = window;
        this.keeper = window.keeper;
        canvas = window.current_canvas;
        setup();
    }

    public NodePanel(MainWindow window, NodePanel parent) {
        setLayout(null);
        this.parent = parent;
        this.window = window;
        this.keeper = window.keeper;
        canvas = window.current_canvas;
        setup();
    }

    public NodePanel(MainWindow window, Graph graph) {
        setLayout(null);
        this.window = window;
        this.graph = graph;
        this.keeper = window.keeper;
        canvas = window.current_canvas;
        setup();
    }

    public NodePanel(MainWindow window, NodePanel parent, Graph graph) {
        setLayout(null);
        this.window = window;
        this.parent = parent;
        this.graph = graph;
        this.keeper = window.keeper;
        canvas = window.current_canvas;
        setup();
    }

    public abstract void setNode(Node node);

    public void refresh(){}

    private void setup() {

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

    public void rescale(float mod, Point source) {
        Point start = this.getLocation(true);
        start.translate(-source.x, -source.y);
        start.setLocation(start.getX() * mod, start.getY() * mod);
        start.translate(source.x, source.y);
        this.setLocation(start);
        this.setSize((int)(this.getWidth() * mod), (int)(this.getHeight() * mod));
    }

    @Override
    public Point getLocation() {
        Point loc = super.getLocation();
        if (parent != null) {
            loc.translate(parent.getX(), parent.getY());
        }
        return loc;
    }

    public Point getLocation(boolean relative) {
        if (relative) {
            return super.getLocation();
        } else {
            return getLocation();
        }
    }

    public void removeAllOutConnections() {
        if (out_connector == null) return;
        canvas.removeOutConnections(this);
    }

    public void removeAllInConnections() {
        if (in_connector == null) return;
        for (OutConnector connector : in_connector.connections) {
            connector.destination = null;
            canvas.removeOutConnections(connector.getParent());
        }
    }
}
