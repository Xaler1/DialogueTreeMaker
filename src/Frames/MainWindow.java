package Frames;

import Managers.TreeKeeper;
import Panels.NodePanel;
import Helpers.OutConnector;
import Managers.Graph;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


/*
    This is the class that assembles the main window and is responsible for handing most project management operations.
 */
public class MainWindow extends JFrame implements MouseListener {

    private List<Graph> graphs;
    public final TreeKeeper keeper;

    private boolean mouse_down = false;

    private JMenuBar menu_bar = new JMenuBar();
    private JMenu menu, submenu;
    private JMenuItem menu_item;

    private JButton button;

    private JPopupMenu right_click_menu;

    private JTabbedPane tabs = new JTabbedPane();

    private final List<Canvas> canvases;
    public Canvas current_canvas = null;
    private PersonPanel character_panel;
    private Variables variables_panel;
    MainWindow self;
    private Point2D scale;

    public final Font main_font = new Font("Serif", Font.PLAIN, 28);

    public NodePanel potential_end_component = null;
    public String last_character = "";

    public boolean show_grid = true;

    /*
        This assembles the main window.
     */
    public MainWindow(List<Graph> graphs, String name, TreeKeeper keeper) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        scale = new Point2D.Float(screen_size.width / 2560.0f, screen_size.height / 1440.0f);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        self = this;
        this.graphs = graphs;
        this.keeper = keeper;

        //This right click menu.
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
                current_canvas.updateLines();
            }
        });

        //This detects when the user presses the delete key and tells the current canvas to attempt to delete a node.
        tabs.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\u007F') {
                    current_canvas.deleteNode();
                }
            }
        });

        //This section assembles the top menu bar.
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
        menu_item.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsJson();
            }
        });
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

        menu = new JMenu("Settings");
        menu_bar.add(menu);
        JCheckBoxMenuItem check_item = new JCheckBoxMenuItem("Show grid");
        check_item.setSelected(true);
        check_item.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                show_grid = check_item.getState();
                current_canvas.updateLines();
            }
        });
        menu.add(check_item);

        canvases = new ArrayList<>();
        for (Graph graph : graphs) {
            createCanvas(graph.name);
        }

        //This section assembles the main portion of the window.
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        //constraints.ipadx = (int) (screen_size.width * 0.8);
        //constraints.ipady = (int) (screen_size.height);
        tabs.setPreferredSize(new Dimension(1000, 600));
        setJMenuBar(menu_bar);
        add(tabs, constraints);
        character_panel = new PersonPanel(keeper);
        character_panel.setPreferredSize(new Dimension(100, 300));
        variables_panel = new Variables();
        variables_panel.setPreferredSize(new Dimension(100, 300));
        constraints.gridx = 1;
        constraints.gridheight = 1;
        //constraints.ipadx = (int) (screen_size.width * 0.14);
        //constraints.ipady = (int) (screen_size.height * 0.5);
        add(character_panel, constraints);
        constraints.gridy = 1;
        add(variables_panel, constraints);

        setBackground(Color.GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screen_size.width, screen_size.height);
        setVisible(true);
        setTitle(name);

        //TODO: doesn't really work as the tabs for some reason always have to focus and so can be changed by pressing
        //the keys anyway.
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

    //What ever Zinks2 is doing.
    public void saveAsJson() {
        System.out.println("RAN!");
    }

    /*
        This tracks the relocation of a component on the canvas by tracking the user's mouse until it is released.
        The movement only happens at most - every 10 milliseconds and also only if the user has moved the mouse to avoid
        flickering.
     */
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

    /*
        This tracks the relocation of the entire canvas (i.e. all the components on the canvas together) by tracking
        the user's mouse until the left mouse button is released. The movement only happens at most - every 10
        milliseconds and also only if the user has moved the mouse to avoid flickering.
     */
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

    /*
        This tracks the connection of two nodes - triggered if the user has pressed the left mouse button over an
        out connector. It follows the user's mouse and tells the canvas to draw a temporary line showing where the
        connection is currently going. Once the user releases the left mouse button it checks whether the mouse has entered
        any in connectors and if it has then it tells the canvas to create a connection. Otherwise, the temporary line
        is discarded.
     */
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

    /*
        This creates a new canvas - first asking the user for its name. Only non-empty, non-duplicate names with
        no spaces or dots are allowed.
     */
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
        Canvas new_canvas = new Canvas(new_graph, self, scale);
        character_panel.addListener(new_canvas);
        new_canvas.addMouseListener(self);
        new_canvas.setBackground(Color.WHITE);
        new_canvas.setComponentPopupMenu(right_click_menu);
        new_canvas.setBorder(BorderFactory.createBevelBorder(1));
        current_canvas = new_canvas;
        canvases.add(new_canvas);
        setDefaultLookAndFeelDecorated(true);
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

    /*
        Sets the mouse down flag to false, to stop any movement tracking loops.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        mouse_down = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /*
        Also sets the mouse down flag to false, to stop any movement tracking loops in case the mouse leaves the canvas.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        Point loc = MouseInfo.getPointerInfo().getLocation();
        loc.translate(-current_canvas.getLocationOnScreen().x, -current_canvas.getLocationOnScreen().y);
        if (!current_canvas.contains(loc)){
            mouse_down = false;
        }
    }

    /*
        Provides a public method for setting the mouse down flag to false in case the mouse was released over some other
        element.
     */
    public void endMouse() {
        mouse_down = false;
    }
}
