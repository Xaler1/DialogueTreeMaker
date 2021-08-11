package Frames;

import Helpers.OutConnector;
import Managers.Graph;
import Nodes.DialogueNode;
import Nodes.EndNode;
import Nodes.Node;
import Nodes.StartNode;
import Panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//TODO: clean up this mess - make all the panel creation functions more or less the same order.
/*
    This class holds and draws the main representation of a dialogue graph. It is responsible for creating and managing
    the visual representations of the nodes of the graph.
 */
public class Canvas extends JPanel implements PropertyChangeListener, Serializable {

    public final Graph graph;
    public boolean has_start_node = false;
    public final List<NodePanel> components;
    private GridBagConstraints constraints;
    private Font main_font;
    private final MainWindow window;
    public int num_workers = 5;

    public Line2D temp_line = null;
    public Point2D scale;
    private Point start = new Point(0, 0);

    /*
        This detects when there has been a change in any of the people in the project and tells all of the nodes to refresh
        to update with this new change.
        //TODO: make the call more specific so that only person-related things update.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (name.equals("name_change") || name.equals("person_create") || name.equals("person_delete")) {
            refreshAll();
        }
    }

    /*
        This constructs the canvas
     */
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

        /*
            This creates a mouse wheel listener that detects when the user wants to zoom in/out and moves all the nodes
            as well as telling them to rescale. The grid reference point is also moved.
         */
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                float mod = -.05f;
                if (e.getUnitsToScroll() < 0) {
                    mod = .05f;
                }
                double old = scale.getX();
                scale.setLocation(Math.min(Math.max(0.3, scale.getX() + mod), 3), Math.min(Math.max(0.3, scale.getY() + mod), 3));
                float diff = (float) (scale.getX() / old);
                translateStart(diff, getMousePosition());
                updateLines();
                List<NodePanel> groups = new LinkedList<>(components);
                for (int x = 0; x < num_workers; x++) {
                    SwingWorker worker = new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            while(groups.size() > 0) {
                                NodePanel panel = groups.remove(0);
                                panel.rescale(diff, getMousePosition());
                            }
                            return null;
                        }
                    };
                    worker.execute();
                }
            }
        });
    }

    public void loadGraph() {
        scale = graph.zoom;
        for (Node node : graph.getNodes()) {
            if (node instanceof StartNode) {
                addStartNode((StartNode) node);
            } else if (node instanceof EndNode) {
                addEndNode((EndNode) node);
            } else if (node instanceof DialogueNode temp_node) {
                if (temp_node.isChoice) {
                    addChoiceNode(temp_node);
                } else {
                    addDialogueNode(temp_node);
                }
            }
        }
        for (Node node : graph.getNodes()) {
            NodePanel parent = graph.getPanel(node);
            if (parent instanceof ChoicePanel) {
                continue;
            }
            for (Node child : node.getChildren()) {
                NodePanel child_panel = graph.getPanel(child);
                parent.getOutConnector().destination = child_panel.getInConnector();
                child_panel.getInConnector().addConnection(parent);
            }
        }
    }

    /*
        This creates a new start node visually as well as a corresponding start node in the graph.
        Before that it also checks that there isn't already a start node on this graph.
     */
    public void addStartNode(StartNode node) {
        if (has_start_node) {
            JOptionPane.showMessageDialog(this, "A single graph cannot have more than one start node.", "Start node already in graph", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        StartPanel start_node = null;
        if (node == null) {
            start_node = new StartPanel(window, this.getMousePosition());
            graph.addStartNode(start_node);
        } else {
            start_node = new StartPanel(window, node.location);
            graph.assignNodePanel(start_node, node.getId());
        }
        start_node.setNode(graph.getNode(start_node));
        add(start_node);
        components.add(start_node);
        repaint();
        has_start_node = true;
    }

    /*
        This creates a new end node visually (a panel) as well as a corresponding end node in the graph.
     */
    public void addEndNode(EndNode node) {

        EndPanel end_node = null;
        if (node == null) {
            end_node = new EndPanel(window, getMousePosition());
            graph.addEndNode(end_node);
        } else {
            end_node = new EndPanel(window, node.location);
            graph.assignNodePanel(end_node, node.getId());
        }
        end_node.setNode(graph.getNode(end_node));
        this.add(end_node);
        components.add(end_node);
        repaint();
    }

    /*
        This creates a new dialogue node visually (a panel) as well as a corresponding end node in the graph.
     */
    public void addDialogueNode(DialogueNode node) {

        DialoguePanel dialogue_node = null;
        if (node == null) {
            dialogue_node = new DialoguePanel(window, getMousePosition());
            graph.addDialogueNode(dialogue_node, "Hello world");
        } else {
            dialogue_node = new DialoguePanel(window, node.location);
            graph.assignNodePanel(dialogue_node, node.getId());
        }

        dialogue_node.setNode(graph.getNode(dialogue_node));
        components.add(dialogue_node);
        add(dialogue_node);
        repaint();
    }

    /*
        This creates a new choice node visually (a panel) as well as a corresponding end node in the graph.
     */
    public void addChoiceNode(DialogueNode node) {

        ChoicePanel dialogue_node = null;
        if (node == null) {
            dialogue_node = new ChoicePanel(window, graph, getMousePosition());
            graph.addChoiceNode(dialogue_node, "Hello world");
        } else {
            dialogue_node = new ChoicePanel(window, graph, node.location);
            graph.assignNodePanel(dialogue_node, node.getId());
        }
        dialogue_node.setNode(graph.getNode(dialogue_node));
        add(dialogue_node);
        components.add(dialogue_node);
        repaint();
    }

    /*
        This moves all of the node panels on the graph together as well as the grid to create the effect of dragging along the
        surface.
     */
    public void translateAll(Point offset) {
        for (JComponent component : components) {
            if (!(component instanceof AnswerPanel)) {
                Point old_loc = component.getLocation();
                old_loc.translate(offset.x, offset.y);
                component.setLocation(old_loc);
            }
        }
        start.translate(offset.x, offset.y);
        updateLines();
    }

    /*
        This moves a specific node panel around with the user's mouse.
     */
    public void relocatedNode(Component component, Point loc) {
        Point old = component.getLocation();
        Point holder = loc.getLocation();
        holder.translate(-old.x, -old.y);
        component.setLocation(loc);
        updateLines();
    }

    /*
        The creates a visual link between an out connector and the in connector of a another node panel. Since each
        node panel has only one in connector - specifying the panel is enough.
     */
    public void createLink(OutConnector out_connector, NodePanel component_end) {
        out_connector.setDestination(component_end.getInConnector());
        component_end.getInConnector().addConnection(out_connector.getParent());
        updateLines();
        temp_line = null;
        graph.createRelation(out_connector.getParent(), component_end);
    }

    /*
        //TODO: probably remove since it doesn't look like anything extra needs to be done.
     */
    public void updateLines() {
        //removeAll();
        repaint();
    }

    /*
        Moves around the reference point for the grid to create the illusion of the grid being moved around.
     */
    private void translateStart(float mod, Point source) {
        start.translate(-source.x, -source.y);
        start.setLocation(start.x * mod, start.y * mod);
        start.translate(source.x, source.y);
    }

    /*
        Removes all the out connections of the given node on the graph.
        All of the visual connections are removed inside the panel itself, which is what calls this.
        //TODO: maybe it makes more sense if this is inside the graph or even the treekeeper?
     */
    public void removeOutConnections(NodePanel panel) {
        graph.getNode(panel).removeChildren();
    }

    /*
        This attempts to delete a node both visually and from the graph. First it checks which (if any) node contains
        the user's cursor. Then it removes all of its connections both in an out and then removes the node and panel themselves.
     */
    public void deleteNode() {
        NodePanel to_remove = null;
        for (NodePanel panel : components) {
            Point point = getMousePosition();
            Point loc = panel.getLocation();
            point.translate(-loc.x, -loc.y);
            if (panel.contains(point)) {
                panel.removeAllInConnections();
                panel.removeAllOutConnections();
                graph.removeNode(panel);
                to_remove = panel;
                break;
            }
        }
        if (to_remove != null) {
            remove(to_remove);
            components.remove(to_remove);
            if (to_remove instanceof StartPanel) {
                has_start_node = false;
            }
        }
        repaint();
    }

    /*
        This tells all the visual node panels to refresh.
        //TODO: create more specific arguments to differentiate between things that need to be refreshed.
     */
    public void refreshAll() {
        for (NodePanel panel : components) {
            panel.refresh();
        }
    }

    public void writeLayout() {
        graph.zoom = scale;
        for (NodePanel node : components) {
            graph.getNode(node).location = node.getLocation(false);
        }
    }


    /*
        First - if the user is creating a new connection between two nodes then it draws a temporary line from the out
        connector to the user's cursor. Next if draws all of the existing connections by looking at all the destinations
        of all the out connectors of all the panels. Finally (if it is enabled) it draws the grid offset by what
        is effectively a 2D mod function of a reference point - start.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D painter = (Graphics2D) g;
        if (temp_line != null) {
            painter.draw(temp_line);
        }
        for (NodePanel panel : components) {
            painter.setStroke(new BasicStroke(4f));
            painter.setPaint(Color.GREEN);
            if (panel.getOutConnector() != null && panel.getOutConnector().destination != null) {
                painter.draw(new Line2D.Float(panel.getOutConnector().getCenter(), panel.getOutConnector().destination.getCenter()));
            }
            painter.setPaint(Color.BLACK);
        }
        if (window.show_grid) {
            painter.setStroke(new BasicStroke((float) scale.getX()));
            float spacing = (float) (scale.getX() * 50);
            Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
            start.x = Math.floorMod(start.x, (int) spacing);
            start.y = Math.floorMod(start.y, (int) spacing);
            for (float x = start.x; x < screen_size.width; x += spacing) {
                painter.draw(new Line2D.Float(x, 0, x, screen_size.height));
            }

            for (float y = start.y; y < screen_size.height; y += spacing) {
                painter.draw(new Line2D.Float(0, y, screen_size.width, y));
            }
        }
    }
}
