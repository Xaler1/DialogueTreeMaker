package Helpers;

import Frames.MainWindow;
import Panels.NodePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/*
    This represents the out connector for a particular panel on the canvas.
 */
public class OutConnector extends JLabel implements Serializable {
    private final NodePanel parent;
    private final MainWindow window;
    public InConnector destination;
    private Image source_img;

    /*
        This loads the default out connector image.
     */
    public OutConnector(NodePanel parent, MainWindow window) {
        this.parent = parent;
        this.window = window;
        setPreferredSize(new Dimension(30, 30));
        try {
            source_img = ImageIO.read(new File("imgs/out_connector.png"));
        } catch (IOException ex) {}
        rescale();
        addMouseListener(new OutListener(window, this));
    }

    /*
        This overrides the default get parent function to instead return the parent NodePanel class.
     */
    @Override
    public NodePanel getParent() {
        return parent;
    }

    /*
        This returns the center point of the connector. Used for getting the point from which to draw a connection line.
     */
    public Point getCenter() {
        Point point = parent.getLocation();
        point.translate(getX(), getY());
        point.translate(getWidth()/2, getHeight()/2);
        return point;
    }

    /*
        This rescales the image when zooming.
     */
    public void rescale() {
        Image scaled = source_img.getScaledInstance((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()), Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        setIcon(icon);
    }

    /*
        The sets the end point of an outgoing connection. Mostly used to make it easier to draw a connection line.
     */
    public void setDestination(InConnector connector) {
        destination = connector;
    }

    /*
        Removes the connection on the canvas and asks the parent to remove it on the graph as well.
     */
    public void removeConnection() {
        destination = null;
        parent.removeAllOutConnections();
    }
}
