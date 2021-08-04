package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.OutConnector;
import Nodes.Node;
import Nodes.StartNode;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends NodePanel{

    private final JLabel start_label;
    private StartNode node;

    public StartPanel(MainWindow window, Point start) {
        super(window);
        setLocation(start.x, start.y);
        addMouseListener(new ComponentListener(window, this));
        setBackground(Color.lightGray);

        start_label = new JLabel("Start");
        add(start_label);

        out_connector = new OutConnector(this, window);
        add(out_connector);
        rescale(1, new Point(0, 0));
    }

    @Override
    public void setNode(Node node) {
        this.node = (StartNode) node;
    }

    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        setSize((int)(120 * canvas.scale.getX()), (int)(50 * canvas.scale.getY()));
        setBorder(BorderFactory.createMatteBorder((int)(2 * canvas.scale.getY()), (int)(5 * canvas.scale.getX()), (int)(2 * canvas.scale.getY()), (int)(2 * canvas.scale.getX()), Color.green));
        start_label.setFont(window.main_font.deriveFont((float) (28 * canvas.scale.getY())));
        start_label.setBounds((int)(10 * canvas.scale.getX()), (int)(5 * canvas.scale.getY()), (int)(70 * canvas.scale.getX()), (int)(40 * canvas.scale.getY()));
        out_connector.setLocation((int)(85 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()));
        out_connector.rescale();
    }
}
