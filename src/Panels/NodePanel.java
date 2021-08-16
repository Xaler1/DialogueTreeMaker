package Panels;

import Frames.Canvas;
import Frames.MainWindow;
import Helpers.InConnector;
import Helpers.OutConnector;
import Managers.Conditional;
import Managers.Graph;
import Managers.TreeKeeper;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
    This is the parent class of all the node panels. It handles the movement of the panels during zoom. (Since zooming
    doesn't really zoom, but just makes everything smaller - all the panels need to be spread out/grouped together to
    create the illusion of zoom)
 */
public abstract class NodePanel extends JPanel implements Serializable {
    protected OutConnector out_connector;
    protected InConnector in_connector;
    protected NodePanel parent = null;
    protected final MainWindow window;
    protected final Canvas canvas;
    protected Graph graph = null;
    protected final TreeKeeper keeper;
    protected Map<Conditional, ConditionalBlock> conditional_panels;

    /*
        Several constructor to account for the fact that not all panels will have default parents or need a reference to
        the graph.
     */
    public NodePanel(MainWindow window) {
        this.window = window;
        this.keeper = window.keeper;
        canvas = window.current_canvas;
        setup();
    }

    public NodePanel(MainWindow window, NodePanel parent) {
        this.parent = parent;
        this.window = window;
        this.keeper = window.keeper;
        canvas = window.current_canvas;
        setup();
    }

    public NodePanel(MainWindow window, Graph graph) {
        this.window = window;
        this.graph = graph;
        this.keeper = window.keeper;
        canvas = window.current_canvas;
        setup();
    }

    public NodePanel(MainWindow window, NodePanel parent, Graph graph) {
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
        conditional_panels = new HashMap<>();
    }

    public NodePanel getConditionalPanel(Conditional conditional) {
        return conditional_panels.get(conditional);
    }

    //Sets the out connector.
    public void setOutConnector(OutConnector connector) {
        out_connector = connector;
    }

    //Sets the in connector
    public void setInConnector(InConnector connector) {
        in_connector = connector;
    }

    //Returns the in connector.
    public InConnector getInConnector() {
        return in_connector;
    }

    //Returns the out connector.
    public OutConnector getOutConnector() {
        return out_connector;
    }

    /*
        Moves the panel relative to the source of the zoom - either towards or away from it to create an illusion of zooming.
     */
    public void rescale(float mod, Point source) {
        Point start = this.getLocation(true);
        start.translate(-source.x, -source.y);
        start.setLocation(start.getX() * mod, start.getY() * mod);
        start.translate(source.x, source.y);
        this.setLocation(start);
        this.setSize((int)(this.getWidth() * mod), (int)(this.getHeight() * mod));
    }

    public void rescale() {
        rescale(1, new Point(0, 0));
    }

    /*
        Overrides the default method to return the location of the node on the canvas instead of the location inside the
        parent. (In case it is a nested panel where the canvas is not the parrent.)
     */
    @Override
    public Point getLocation() {
        Point loc = super.getLocation();
        if (parent != null) {
            loc.translate(parent.getX(), parent.getY());
        }
        return loc;
    }

    /*
        Conditionally returns the relative location or the absolute location.
        //TODO: is this really needed?
     */
    public Point getLocation(boolean relative) {
        if (relative) {
            return super.getLocation();
        } else {
            return getLocation();
        }
    }

    /*
        If the panel has an out connector then it asks the canvas to remove all the out connections.
     */
    public void removeAllOutConnections() {
        if (out_connector == null) return;
        canvas.removeOutConnections(this);
    }

    /*
        If the panel has an in connector then it goes through all the incomming connections and asks the canvas to
        remove them one by one.
     */
    public void removeAllInConnections() {
        if (in_connector == null) return;
        for (OutConnector connector : in_connector.connections) {
            connector.destination = null;
            canvas.removeOutConnections(connector.getParent());
        }
    }

    public void removeChild(NodePanel panel) {}

    public void removeAllChildren() {}
}
