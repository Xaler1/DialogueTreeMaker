package Helpers;

import Frames.MainWindow;
import Panels.NodePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
/*
    This represents the in connector for a particular panel on the canvas.
 */
public class InConnector extends JLabel {

    private final NodePanel parent;
    private final MainWindow window;
    private Image source_img;
    public final List<OutConnector> connections;

    /*
        This sets up the defaults and adds a mouse listener.
     */
    public InConnector(NodePanel parent, MainWindow window) {
        this.parent = parent;
        this.window = window;
        setPreferredSize(new Dimension(30, 30));
        connections = new LinkedList<>();
        try {
            source_img = ImageIO.read(new File("imgs/in_connector.png"));
        } catch (IOException ex) {}
        rescale();
        addMouseListener(new InListener(window, this));
    }

    /*
        This reloads the image and scales it appropriately for the current zoom level.
     */
    public void rescale() {
        Image scaled = source_img.getScaledInstance((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()), Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        setIcon(icon);
    }

    /*
        Instead of returning the component-wise parent returns a NodePanel class parent.
     */
    @Override
    public NodePanel getParent() {
        return parent;
    }

    /*
        This returns the center point of the in connector. Used for getting the point to which to draw a connection line.
     */
    public Point getCenter() {
        Point point = parent.getLocation();
        point.translate(getX(), getY());
        point.translate(getWidth()/2, getHeight()/2);
        return point;
    }

    /*
        Asks the parent o remove all incoming connections
     */
    public void removeConnections() {
        parent.removeAllInConnections();
    }

    /*
        Adds a new link to an out connector.
     */
    public void addConnection(NodePanel panel) {
        connections.add(panel.getOutConnector());
    }
}
