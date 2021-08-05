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
    private Image source_img;

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
        Image scaled = source_img.getScaledInstance((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()), Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        setIcon(icon);
        //setSize((int)(30 * window.current_canvas.scale.getX()), (int)(30 * window.current_canvas.scale.getY()));
    }

    public void setDestination(InConnector connector) {
        destination = connector;
    }

    public void removeConnection() {
        destination = null;
        parent.removeAllOutConnections();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawImage()
    }
}
