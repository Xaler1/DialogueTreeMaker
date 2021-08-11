package Panels;

import Frames.MainWindow;
import Helpers.OutConnector;
import Managers.Graph;
import Nodes.AnswerNode;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;

/*
    This is a visual representation of an answer node. Users can use this to add answer text, remove this node or connect it
    to another node. Conceptually it is the same as a dialogue node, however visually it is stored directly inside a choice
    to which it is connected directly.
 */
public class AnswerPanel extends NodePanel {

    private final JTextArea text_entry;
    private final JScrollPane pane;
    private AnswerNode node;

    /*
        This assembles the panel using a gridbag.
     */
    public AnswerPanel(MainWindow window, NodePanel parent, Graph graph) {
        super(window, parent, graph);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.5;
        constraints.weighty = 1;
        setBackground(Color.orange);

        text_entry = new JTextArea("Hello world");
        text_entry.setLineWrap(true);
        pane = new JScrollPane(text_entry);
        pane.setPreferredSize(new Dimension(150, 20));
        add(pane, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.3;
        out_connector = new OutConnector(this, window);
        out_connector.setPreferredSize(new Dimension(30, 30));
        add(out_connector, constraints);

        setFocusable(true);
        rescale(1, new Point(0, 0));
    }

    @Override
    public void setNode(Node node) {
        this.node = (AnswerNode) node;
        text_entry.setText(this.node.getAnswerText());
    }

    /*
        Rescales the font and asks the out connector to rescale to fit the new zoom level.
        //TODO: add border resizing.
     */
    @Override
    public void rescale(float mod, Point source) {
        text_entry.setFont(window.main_font.deriveFont((float) (18 * canvas.scale.getX())));
        out_connector.rescale();
    }
}
