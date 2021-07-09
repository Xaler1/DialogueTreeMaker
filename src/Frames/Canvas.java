package Frames;

import Helpers.*;
import Managers.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class Canvas extends JPanel implements PropertyChangeListener {

    private final Graph graph;
    public boolean has_start_node = false;
    public final List<NodePanel> components;
    private GridBagConstraints constraints;
    private Font main_font;
    private final MainWindow window;
    private Graphics2D painter;

    public Line2D temp_line = null;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public Canvas(Graph graph, MainWindow window) {
        this.graph = graph;
        components = new ArrayList<>();
        constraints = new GridBagConstraints();
        main_font = new Font("Serif", Font.PLAIN, 28);
        this.window = window;
        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();
    }

    public void addStartNode() {
        if (has_start_node) {
            JOptionPane.showMessageDialog(this, "A single graph cannot have more than one start node.", "Start node already in graph", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 10;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        NodePanel start_node = new NodePanel();
        start_node.setLayout(new GridBagLayout());
        start_node.addMouseListener(new ComponentListener(window, start_node));
        start_node.setBorder(BorderFactory.createMatteBorder(2, 5, 2, 2, Color.green));
        start_node.setBackground(Color.lightGray);
        JLabel start_label = new JLabel("Start");
        start_label.setFont(main_font);
        start_node.add(start_label, constraints);

        constraints.gridwidth = 1;
        constraints.gridx = 1;
        OutConnector out_connector = new OutConnector(start_node, window);
        start_node.addOutConnector(out_connector);
        start_node.add(out_connector, constraints);
        Point mouse_loc = this.getMousePosition();
        start_node.setBounds(mouse_loc.x, mouse_loc.y, 120, 50);
        this.add(start_node);
        components.add(start_node);
        this.update(this.getGraphics());
        has_start_node = true;

        graph.addStartNode(start_node);
    }

    public void addEndNode() {
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.ipadx = 10;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        NodePanel end_node = new NodePanel();
        end_node.setLayout(new GridBagLayout());
        end_node.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 5, Color.red));
        end_node.setBackground(Color.lightGray);
        end_node.addMouseListener(new ComponentListener(window, end_node));
        JLabel end_label = new JLabel("End");
        end_label.setFont(main_font);
        end_node.add(end_label, constraints);

        constraints.gridwidth = 1;
        constraints.gridx = 0;
        InConnector in_connector = new InConnector(end_node, window);
        end_node.setInConnector(in_connector);
        end_node.add(in_connector, constraints);
        Point mouse_loc = this.getMousePosition();
        end_node.setBounds(mouse_loc.x, mouse_loc.y, 120, 50);
        this.add(end_node);
        components.add(end_node);
        this.update(this.getGraphics());

        graph.addEndNode(end_node);
    }

    public void addDialogueNode() {
        NodePanel dialogue_node = new NodePanel();
        dialogue_node.setLayout(null);
        dialogue_node.setBorder(BorderFactory.createMatteBorder(5, 2, 2, 2, Color.yellow));
        dialogue_node.addMouseListener(new ComponentListener(window, dialogue_node));

        JTextArea text_entry = new JTextArea("Hello world!");
        text_entry.setLineWrap(true);
        JScrollPane pane = new JScrollPane(text_entry);
        pane.setBounds(40, 10, 150, 60);
        dialogue_node.add(pane);

        JButton button = new JButton("Add answer");
        button.setBounds(10, 90, 120, 20);
        dialogue_node.add(button);

        InConnector in_connector = new InConnector(dialogue_node, window);
        in_connector.setBounds(5, 10, 30, 30);
        dialogue_node.add(in_connector);
        dialogue_node.setInConnector(in_connector);

        Point mouse_loc = this.getMousePosition();
        dialogue_node.setBounds(mouse_loc.x, mouse_loc.y, 200, 200);

        add(dialogue_node);
        components.add(dialogue_node);
        this.update(this.getGraphics());
        graph.addDialogueNode(dialogue_node, "Hello world");
    }

    public void translateAll(Point offset) {
        for (JComponent component : components) {
            Point old_loc = component.getLocation();
            old_loc.translate(offset.x, offset.y);
            component.setLocation(old_loc);
        }
    }

    public void relocatedNode(Component component, Point loc) {
        Point old = component.getLocation();
        Point holder = loc.getLocation();
        holder.translate(-old.x, -old.y);
        component.setLocation(loc);
        updateLines();
    }

    public void createLink(OutConnector out_connector, NodePanel component_end) {
        out_connector.setDestination(component_end.getInConnector());
        updateLines();
        temp_line = null;
        graph.createRelation(out_connector.getParent(), component_end);
    }

    public void updateLines() {
        //removeAll();
        repaint();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        painter = (Graphics2D) g;
        if (temp_line != null) {
            painter.draw(temp_line);
        }
        for (NodePanel panel : components) {
            for (OutConnector connector : panel.getOutConnectors()) {
                if (connector.destination != null) {
                    painter.draw(new Line2D.Float(connector.getCenter(), connector.destination.getCenter()));
                }
            }
        }
    }
}
