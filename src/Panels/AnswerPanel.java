package Panels;

import Frames.MainWindow;
import Helpers.OutConnector;
import Managers.Conditional;
import Managers.Graph;
import Managers.Person;
import Nodes.AnswerNode;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
    This is a visual representation of an answer node. Users can use this to add answer text, remove this node or connect it
    to another node. Conceptually it is the same as a dialogue node, however visually it is stored directly inside a choice
    to which it is connected directly.
 */
public class AnswerPanel extends NodePanel {

    private final JTextArea text_entry;
    private final JScrollPane pane;
    private final JLabel conditional_btn;
    private AnswerNode node;
    private Person person = null;

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
        constraints.weightx = 2;
        constraints.weighty = 1;
        loadBackground("orange");

        text_entry = new JTextArea("Hello world");
        text_entry.setLineWrap(true);
        text_entry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                node.setAnswerText(text_entry.getText());
            }
        });
        pane = new JScrollPane(text_entry);
        pane.setPreferredSize(new Dimension(200, 20));
        add(pane, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.3;
        out_connector = new OutConnector(this, window);
        out_connector.setPreferredSize(new Dimension(30, 30));
        out_connector.setHorizontalAlignment(SwingConstants.CENTER);
        add(out_connector, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.weightx = 2;
        constraints.weighty = 0.2;
        conditional_btn = new JLabel("Add Conditional");
        conditional_btn.setBorder(BorderFactory.createBevelBorder(1));
        conditional_btn.setHorizontalAlignment(SwingConstants.CENTER);
        conditional_btn.setPreferredSize(new Dimension(200, 20));
        conditional_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createConditional(null);
            }
        });
        add(conditional_btn, constraints);

        setFocusable(true);
        rescale();
    }

    private void createConditional(Conditional conditional) {
        if (conditional == null) {
            conditional = node.addConditional();
        } else {
            conditional.reInit();

        }
        ConditionalBlock block = new ConditionalBlock(window, conditional, this);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2 + conditional_panels.size();
        constraints.weightx = 1;
        constraints.weighty = 0.3;
        constraints.gridwidth = 3;
        add(block, constraints);
        rescale();
        conditional_panels.put(conditional, block);
        canvas.components.add(block);
        conditional.addListener(block);
        parent.rescale();
    }

    public void setPerson(Person person) {
        this.person = person;
        for (Conditional conditional : conditional_panels.keySet()) {
            conditional.person = person;
        }
    }

    @Override
    public void setNode(Node node) {
        this.node = (AnswerNode) node;
        text_entry.setText(this.node.getAnswerText());
        for (Conditional conditional : node.getConditionals()) {
            createConditional(conditional);
        }
    }

    /*
        Rescales the font and asks the out connector to rescale to fit the new zoom level.
        //TODO: add border resizing.
     */
    @Override
    public void rescale(float mod, Point source) {
        Font new_font = window.main_font.deriveFont((float) (18 * canvas.scale.getX()));
        text_entry.setFont(new_font);
        conditional_btn.setFont(new_font);
        out_connector.rescale();
        for (ConditionalBlock block : conditional_panels.values()) {
            block.rescale();
        }
    }

    @Override
    public void removeChild(NodePanel panel) {
        ConditionalBlock child = (ConditionalBlock) panel;
        if (child.out_connector.destination != null) {
            child.out_connector.destination.connections.remove(child.out_connector);
        }
        node.removeConditional(child.conditional);
        canvas.components.remove(panel);
        conditional_panels.remove(child.conditional);
        remove(panel);
        parent.rescale();
    }

    @Override
    public void removeAllInNodeChildren() {
        for (ConditionalBlock block : conditional_panels.values()) {
            removeChild(block);
        }
    }
}
