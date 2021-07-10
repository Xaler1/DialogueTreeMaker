package Frames;

import Helpers.*;
import Managers.Graph;
import Panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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
    public Point2D scale;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public Canvas(Graph graph, MainWindow window, Point2D default_scale) {
        this.graph = graph;
        components = new ArrayList<>();
        constraints = new GridBagConstraints();
        main_font = new Font("Serif", Font.PLAIN, 28);
        this.window = window;
        this.scale = new Point2D.Double(default_scale.getX(), default_scale.getY());
        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        float mod = -.05f;
                        if (e.getUnitsToScroll() < 0) {
                            mod = .05f;
                        }
                        double old = scale.getX();
                        scale.setLocation(Math.min(Math.max(0.3, scale.getX() + mod), 3), Math.min(Math.max(0.3, scale.getY() + mod), 3));
                        float diff = (float) (scale.getX() / old);
                        for (NodePanel panel : components) {
                            panel.rescale(diff, getMousePosition());
                        }
                        updateLines();
                        return null;
                    }
                };
                worker.execute();
            }
        });
    }

    public void addStartNode() {
        if (has_start_node) {
            JOptionPane.showMessageDialog(this, "A single graph cannot have more than one start node.", "Start node already in graph", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        StartPanel start_node = new StartPanel(window, this.getMousePosition());
        this.add(start_node);
        components.add(start_node);
        this.update(this.getGraphics());
        has_start_node = true;

        graph.addStartNode(start_node);
    }

    public void addEndNode() {
        EndPanel end_node = new EndPanel(window, getMousePosition());
        this.add(end_node);
        components.add(end_node);
        this.update(this.getGraphics());

        graph.addEndNode(end_node);
    }

    public void addDialogueNode() {
        DialoguePanel dialogue_node = new DialoguePanel(window, getMousePosition());

        add(dialogue_node);
        components.add(dialogue_node);
        this.update(this.getGraphics());
        graph.addDialogueNode(dialogue_node, "Hello world");
    }

    public void addChoiceNode() {
        ChoicePanel dialogue_node = new ChoicePanel(window, graph, getMousePosition());
        graph.addDialogueNode(dialogue_node, "Hello world");
        add(dialogue_node);
        components.add(dialogue_node);
        this.update(this.getGraphics());
    }

    public void translateAll(Point offset) {
        for (JComponent component : components) {
            if (!(component instanceof AnswerPanel)) {
                Point old_loc = component.getLocation();
                old_loc.translate(offset.x, offset.y);
                component.setLocation(old_loc);
            }
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
            if (panel.getOutConnector() != null && panel.getOutConnector().destination != null) {
                painter.draw(new Line2D.Float(panel.getOutConnector().getCenter(), panel.getOutConnector().destination.getCenter()));
            }
        }
    }
}
