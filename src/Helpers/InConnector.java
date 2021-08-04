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

public class InConnector extends JLabel {

    private final NodePanel parent;
    private final MainWindow window;
    public final List<OutConnector> connections;

    public InConnector(NodePanel parent, MainWindow window) {
        this.parent = parent;
        this.window = window;
        connections = new LinkedList<>();
        rescale();
        addMouseListener(new InListener(window, this));
    }

    public void rescale() {
        Image img = null;
        try {
            img = ImageIO.read(new File("imgs/in_connector.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Image scaled = img.getScaledInstance((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()), Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        setIcon(icon);
        setSize((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()));
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

    public void removeConnections() {
        parent.removeAllInConnections();
    }

    public void addConnection(NodePanel panel) {
        connections.add(panel.getOutConnector());
    }
}
