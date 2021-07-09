package Frames;

import Helpers.ComponentListener;
import Managers.Graph;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel implements PropertyChangeListener {

    private final Graph graph;
    public boolean has_start_node = false;
    public final List<JComponent> components;
    private GridBagConstraints constraints;
    private Font main_font;
    private final MainWindow window;

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
    }

    public void addStartNode() {
        if (has_start_node) {
            JOptionPane.showMessageDialog(this, "A single graph cannot have more than one node.", "Start node already in graph", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 10;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        JPanel start_node = new JPanel(new GridBagLayout());
        start_node.addMouseListener(new ComponentListener(window, start_node));
        start_node.setBorder(BorderFactory.createMatteBorder(2, 5, 2, 2, Color.green));
        start_node.setBackground(Color.lightGray);
        JLabel start_label = new JLabel("Start");
        start_label.setFont(main_font);
        start_node.add(start_label, constraints);

        constraints.gridwidth = 1;
        constraints.gridx = 1;
        ImageIcon icon = new ImageIcon("imgs/out_connector.png");
        JLabel out_connector = new JLabel(icon);
        start_node.add(out_connector, constraints);
        Point mouse_loc = this.getMousePosition();
        start_node.setBounds(mouse_loc.x, mouse_loc.y, 120, 50);
        this.add(start_node);
        components.add(start_node);
        this.update(this.getGraphics());
        has_start_node = true;
    }

    public void addEndNode() {
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.ipadx = 10;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        JPanel end_node = new JPanel(new GridBagLayout());
        end_node.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 5, Color.red));
        end_node.setBackground(Color.lightGray);
        end_node.addMouseListener(new ComponentListener(window, end_node));
        JLabel end_label = new JLabel("End");
        end_label.setFont(main_font);
        end_node.add(end_label, constraints);

        constraints.gridwidth = 1;
        constraints.gridx = 0;
        ImageIcon icon = new ImageIcon("imgs/in_connector.png");
        JLabel out_connector = new JLabel(icon);
        end_node.add(out_connector, constraints);
        Point mouse_loc = this.getMousePosition();
        end_node.setBounds(mouse_loc.x, mouse_loc.y, 120, 50);
        this.add(end_node);
        components.add(end_node);
        this.update(this.getGraphics());
    }

    public JComponent attemptGrab() {
        Point point = this.getMousePosition();
        for (Component component : this.getComponents()) {
            Point p = component.getMousePosition();
            if (component.contains(p)) {
                for (Component sub_component : ((JComponent)component).getComponents()) {
                    if (!(sub_component instanceof JLabel) && sub_component.contains(point)) {
                        return null;
                    }
                }
                return (JComponent) component;
            }
        }
        return null;
    }

    public void translateAll(Point offset) {
        for (JComponent component : components) {
            Point old_loc = component.getLocation();
            old_loc.translate(offset.x, offset.y);
            component.setLocation(old_loc);
        }
    }
}
