package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.OutConnector;
import Nodes.Node;
import Nodes.StartNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StartPanel extends NodePanel{

    private final JLabel start_label;
    private JButton test_out;
    private StartNode node;

    public StartPanel(MainWindow window, Point start) {
        super(window);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        setLocation(start.x, start.y);
        addMouseListener(new ComponentListener(window, this));
        setBackground(Color.lightGray);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        start_label = new JLabel("Start");
        start_label.setPreferredSize(new Dimension(100, 30));
        add(start_label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.3;
        out_connector = new OutConnector(this, window);
        out_connector.setPreferredSize(new Dimension(30, 30));
        add(out_connector, constraints);
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
        out_connector.rescale();
        repaint();
    }
}
