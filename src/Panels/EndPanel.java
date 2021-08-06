package Panels;

import Frames.MainWindow;
import Helpers.ComponentListener;
import Helpers.InConnector;
import Nodes.EndNode;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;

/*
    This is a visual representation of an end panel. It only has an in connector.
 */
public class EndPanel extends NodePanel{

    private final JLabel end_label;
    private EndNode node;


    /*
        This assembles the panel using a gridbag layout.
     */
    public EndPanel(MainWindow window, Point start) {
        super(window);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.3;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 1, 0, 1);
        setLocation(start);

        setBorder(BorderFactory.createMatteBorder(2, 2, 2, 5, Color.red));
        setBackground(Color.lightGray);
        addMouseListener(new ComponentListener(window, this));

        in_connector = new InConnector(this, window);
        in_connector.setPreferredSize(new Dimension(30, 30));
        add(in_connector, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        end_label = new JLabel("End");
        end_label.setPreferredSize(new Dimension(100, 30));
        add(end_label, constraints);

        rescale(1, new Point(0, 0));
    }

    @Override
    public void setNode(Node node) {
        this.node = (EndNode) node;
    }

    /*
        This resizes the panel and the font to fit under the new zoom level.
        //TODO: add border resizing.
     */
    @Override
    public void rescale(float mod, Point source) {
        super.rescale(mod, source);
        setSize((int)(120 * canvas.scale.getX()), (int)(50 * canvas.scale.getY()));
        end_label.setFont(window.main_font.deriveFont((float) (28.0f * canvas.scale.getX())));
        in_connector.rescale();
    }
}
