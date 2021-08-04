package Panels;

import Frames.MainWindow;
import Helpers.OutConnector;
import Managers.Graph;
import Nodes.AnswerNode;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;

public class AnswerPanel extends NodePanel{

    private final JTextArea text_entry;
    private final JScrollPane pane;
    private AnswerNode node;

    public AnswerPanel(MainWindow window, NodePanel parent, Graph graph, Point start) {
        super(window, parent, graph);
        setLocation(start);
        setBackground(Color.orange);

        text_entry = new JTextArea("Hello world");
        text_entry.setLineWrap(true);
        pane = new JScrollPane(text_entry);
        add(pane);

        out_connector = new OutConnector(this, window);
        add(out_connector);

        setFocusable(true);
        rescale(1, new Point(0, 0));
    }

    @Override
    public void setNode(Node node) {
        this.node = (AnswerNode) node;
    }

    @Override
    public void rescale(float mod, Point source) {
        setSize((int)(280 * canvas.scale.getX()), (int)(50 * canvas.scale.getY()));
        pane.setBounds((int)(35 * canvas.scale.getX()), (int)(5 * canvas.scale.getY()), (int)(210 * canvas.scale.getX()), (int)(40 * canvas.scale.getY()));
        text_entry.setFont(window.main_font.deriveFont((float) (18 * canvas.scale.getX())));
        out_connector.setLocation((int)(250 * canvas.scale.getX()), (int)(10 * canvas.scale.getY()));
        out_connector.rescale();
    }
}
