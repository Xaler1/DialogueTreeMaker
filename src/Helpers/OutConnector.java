package Helpers;

import Frames.MainWindow;
import Panels.NodePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OutConnector extends JLabel {
    private final NodePanel parent;
    private final MainWindow window;
    public InConnector destination;

    public OutConnector(NodePanel parent, MainWindow window) {
        this.parent = parent;
        this.window = window;
        rescale();
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

    public void rescale() {
        Image img = null;
        try {
            img = ImageIO.read(new File("imgs/out_connector.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Image scaled = img.getScaledInstance((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()), Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        setIcon(icon);
        setSize((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()));
    }

    public void setDestination(InConnector connector) {
        destination = connector;
    }

    public void removeConnection() {
        destination = null;
        parent.removeAllOutConnections();
    }
}
