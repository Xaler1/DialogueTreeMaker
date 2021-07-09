package Frames;

import Panels.NodePanel;
import Helpers.OutConnector;
import Managers.Graph;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame implements MouseListener {

    private List<Graph> graphs;

    private boolean mouse_down = false;

    private JMenuBar menu_bar = new JMenuBar();
    private JMenu menu, submenu;
    private JMenuItem menu_item;

    private JPopupMenu right_click_menu;

    private JTabbedPane tabs = new JTabbedPane();

    private final List<Canvas> canvases;
    Canvas current_canvas = null;
    MainWindow self;

    public NodePanel potential_end_component = null;

    public MainWindow(List<Graph> graphs, String name) {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        self = this;
        this.graphs = graphs;

        right_click_menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Start node");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_canvas.addStartNode();
            }
        });
        right_click_menu.add(item);
        item = new JMenuItem("Dialogue node");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_canvas.addDialogueNode();
            }
        });
        right_click_menu.add(item);
        item = new JMenuItem("Choice node");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_canvas.addChoiceNode();
            }
        });
        right_click_menu.add(item);
        item = new JMenuItem("End node");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_canvas.addEndNode();
            }
        });
        right_click_menu.add(item);

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                current_canvas = canvases.get(tabs.getSelectedIndex());
            }
        });

        menu = new JMenu("File");
        menu_bar.add(menu);

        menu_item = new JMenuItem("New");
        menu.add(menu_item);
        menu_item = new JMenuItem("Open");
        menu.add(menu_item);
        menu_item = new JMenuItem("Save");
        menu.add(menu_item);
        submenu = new JMenu("Export as");
        menu.add(submenu);
        menu_item = new JMenuItem("XML");
        submenu.add(menu_item);
        menu_item = new JMenuItem("JSON");
        submenu.add(menu_item);

        menu = new JMenu("Add");
        menu_bar.add(menu);
        menu_item = new JMenuItem("Graph");
        menu_item.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createCanvas("");
            }
        });
        menu_item.setText("Graph");
        menu.add(menu_item);
        menu_item = new JMenuItem("Data folder");
        menu.add(menu_item);

        canvases = new ArrayList<>();
        for (Graph graph : graphs) {
            createCanvas(graph.name);
        }

        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.ipadx = (int) (screen_size.width);
        constraints.ipady = (int) (screen_size.height);
        setJMenuBar(menu_bar);
        add(tabs, constraints);
        setBackground(Color.GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screen_size.width, screen_size.height);
        setVisible(true);
        setTitle(name);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_LEFT && tabs.getTabCount() > 0) {
                    tabs.setSelectedIndex(Math.max(0, tabs.getSelectedIndex() - 1));
                } else if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_RIGHT && tabs.getTabCount() > 0) {
                    tabs.setSelectedIndex(Math.max(tabs.getTabCount() - 1, tabs.getSelectedIndex() + 1));
                }
            }
        });
    }

    public void trackRelocate(JComponent component) {
        Point offset = component.getMousePosition();
        mouse_down = true;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Point old = current_canvas.getMousePosition();
                while (mouse_down) {
                    Point mouse_loc = current_canvas.getMousePosition();
                    if (mouse_loc.distance(old) > 1) {
                        old = mouse_loc;
                        mouse_loc.translate(-offset.x, -offset.y);
                        current_canvas.relocatedNode(component, mouse_loc);
                        Thread.sleep(10);
                    }
                }
                return null;
            }
        };
        worker.execute();
    }

    private void trackGrab() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Point old = MouseInfo.getPointerInfo().getLocation();
                while (mouse_down) {
                    Point mouse_loc = MouseInfo.getPointerInfo().getLocation();
                    if (mouse_loc.distance(old) > 1) {
                        Point holder = mouse_loc.getLocation();
                        mouse_loc.translate(-old.x, -old.y);
                        old = holder;
                        current_canvas.translateAll(mouse_loc);
                        Thread.sleep(10);
                    }
                }
                return null;
            }
        };
        worker.execute();
    }

    public void trackConnect(OutConnector component, Point start) {
        mouse_down = true;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                while (mouse_down) {
                    Point mouse_loc = current_canvas.getMousePosition();
                    current_canvas.temp_line = new Line2D.Float(start, mouse_loc);
                    current_canvas.updateLines();
                    Thread.sleep(10);
                }
                if (potential_end_component != null) { ;
                    current_canvas.createLink(component, potential_end_component);
                }
                current_canvas.temp_line = null;
                current_canvas.updateLines();
                return null;
            }
        };
        worker.execute();
    }

    private void createCanvas(String name) {
        if (name.equals("")) {
            while (true) {
                name = (String) JOptionPane.showInputDialog(
                        this,
                        "Enter the name of the graph.",
                        "Graph name",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        name
                );
                if (name == null) {
                    return;
                }
                if (name.trim().length() == 0) {
                    JOptionPane.showMessageDialog(this, "You must enter a name", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else if (name.contains(" ")) {
                    JOptionPane.showMessageDialog(this, "The name cannot contain any spaces", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else if (name.contains(".")) {
                    JOptionPane.showMessageDialog(this, "The name cannot contain dots", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else if (name.length() > 30) {
                    JOptionPane.showMessageDialog(this, "The name must be less than 30 characters", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else {
                    boolean duplicate = false;
                    for (Graph graph : graphs) {
                        if (name.equals(graph.name)) {
                            JOptionPane.showMessageDialog(this, "There is already a graph with this name", "Invalid name", JOptionPane.ERROR_MESSAGE);
                            duplicate = true;
                            break;
                        }
                    }
                    if (duplicate) continue;
                    break;
                }
            }
        }
        Graph new_graph = new Graph(name);
        graphs.add(new_graph);
        Canvas new_canvas = new Canvas(new_graph, self);
        new_canvas.addMouseListener(self);
        new_canvas.setBackground(Color.WHITE);
        new_canvas.setComponentPopupMenu(right_click_menu);
        new_canvas.setBorder(BorderFactory.createBevelBorder(1));
        current_canvas = new_canvas;
        canvases.add(new_canvas);
        tabs.add(name, new_canvas);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
            mouse_down = true;
            trackGrab();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouse_down = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void endMouse() {
        mouse_down = false;
    }
}
