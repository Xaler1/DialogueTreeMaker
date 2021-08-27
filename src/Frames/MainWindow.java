package Frames;

import Helpers.OutConnector;
import Managers.Graph;
import Managers.TreeKeeper;
import Panels.NodePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;


/*
    This is the class that assembles the main window and is responsible for handing most project management operations.
 */
public class MainWindow extends JFrame implements MouseListener {

    private final List<Graph> graphs;
    public final TreeKeeper keeper;

    private boolean mouse_down = false;

    private JMenuBar menu_bar = new JMenuBar();
    private JMenu menu, submenu;
    private JMenuItem menu_item;

    private JPopupMenu right_click_menu;

    private JTabbedPane tabs = new JTabbedPane();

    public final List<Canvas> canvases;
    public Canvas current_canvas = null;
    private PersonPanel character_panel;
    private VariablePanel variables_panel;
    MainWindow self;
    private Point2D scale;

    public final Font main_font = new Font("Serif", Font.PLAIN, 28);

    public NodePanel potential_end_component = null;

    public boolean show_grid = true;

    private Properties config;

    /*
        This assembles the main window.
     */
    public MainWindow(List<Graph> graphs, String name, TreeKeeper keeper, Properties config) {
        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.config = config;

        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        scale = new Point2D.Float(screen_size.width / 2560.0f, screen_size.height / 1440.0f);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        self = this;
        this.graphs = graphs;
        this.keeper = keeper;
        canvases = new ArrayList<>();

        //This right click menu.
        right_click_menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Start node");
        item.addActionListener(e -> current_canvas.addStartNode(null));
        right_click_menu.add(item);
        item = new JMenuItem("Dialogue node");
        item.addActionListener(e -> current_canvas.addDialogueNode(null));
        right_click_menu.add(item);
        item = new JMenuItem("Choice node");
        item.addActionListener(e -> current_canvas.addChoiceNode(null));
        right_click_menu.add(item);
        item = new JMenuItem("End node");
        item.addActionListener(e -> current_canvas.addEndNode(null));
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
        menu_item.addActionListener(e -> keeper.newProject());
        menu.add(menu_item);
        menu_item = new JMenuItem("Open");
        menu_item.addActionListener(e -> loadProject());
        menu.add(menu_item);
        menu_item = new JMenuItem("Save");
        menu_item.addActionListener(e -> saveProject(false));
        menu.add(menu_item);
        menu_item = new JMenuItem("Save a copy");
        menu_item.addActionListener(e -> saveProject(true));
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
                createCanvas(null);
            }
        });
        menu_item.setText("Graph");
        menu.add(menu_item);
        menu_item = new JMenuItem("Data folder");
        menu.add(menu_item);

        menu = new JMenu("Settings");
        menu_bar.add(menu);
        final JCheckBoxMenuItem check_item = new JCheckBoxMenuItem("Show grid");
        check_item.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                show_grid = check_item.getState();
                for (Canvas canvas : canvases) {
                    canvas.updateLines();
                }
                config.setProperty("show_grid", String.valueOf(show_grid));
                saveConfig();
            }
        });
        check_item.setSelected(Boolean.parseBoolean(config.getProperty("show_grid", "True")));
        menu.add(check_item);
        final JCheckBoxMenuItem load_last_check = new JCheckBoxMenuItem("Load last project");
        load_last_check.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                config.setProperty("load_last", String.valueOf(load_last_check.getState()));
                saveConfig();
            }
        });
        load_last_check.setSelected(Boolean.parseBoolean(config.getProperty("load_last", "true")));
        menu.add(load_last_check);
        JLabel label = new JLabel("Performance");
        menu.add(label);
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 11, 1);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(1);
        Dictionary<Integer, JComponent> labels = new Hashtable<>();
        labels.put(1, new JLabel("Low"));
        labels.put(6, new JLabel("Med"));
        labels.put(11, new JLabel("High"));
        slider.setLabelTable(labels);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for (Canvas canvas : canvases) {
                    canvas.num_workers = slider.getValue();
                }
                config.setProperty("performance", String.valueOf(slider.getValue()));
                saveConfig();
            }
        });
        slider.setValue(Integer.parseInt(config.getProperty("performance", "1")));
        menu.add(slider);

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
        tabs.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (e.getOppositeComponent() != null && e.getOppositeComponent() instanceof JButton) {
                    //tabs.requestFocusInWindow();
                }
            }
        });
        setJMenuBar(menu_bar);
        add(tabs, constraints);
        character_panel = new PersonPanel(keeper);
        character_panel.setPreferredSize(new Dimension(100, 300));
        variables_panel = new VariablePanel(keeper);
        variables_panel.setPreferredSize(new Dimension(100, 300));
        constraints.gridx = 1;
        constraints.gridheight = 1;
        //constraints.ipadx = (int) (screen_size.width * 0.14);
        //constraints.ipady = (int) (screen_size.height * 0.5);
        add(character_panel, constraints);
        constraints.gridy = 1;
        add(variables_panel, constraints);

        for (Graph graph : graphs) {
            createCanvas(graph);
        }

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
        setupKeybinds();
    }

    public void saveConfig() {
        try {
            FileOutputStream writer = new FileOutputStream("config.properties");
            config.store(writer, "");
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Whatever Zinks2 is doing.
    public void saveAsJson() {
        System.out.println("RAN!");
    }

    private void saveProject(boolean new_file) {
        if (!new_file && keeper.saved_before) {
            keeper.saveProject(null, false);
            return;
        }
        final JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Dialogue trees", "tree");
        chooser.setFileFilter(filter);
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            keeper.saveProject(chooser.getSelectedFile(), true);
        }
    }

    private void loadProject() {
        final JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Dialogue trees", "tree");
        chooser.setFileFilter(filter);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            keeper.loadProject(chooser.getSelectedFile());
        }
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
                if (potential_end_component != null) {
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
    private void createCanvas(Graph graph) {
        if (graph == null) {
            while (true) {
                String name = "";
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
                    for (int x = 0; x < graphs.size(); x++) {
                        if (name.equals(graphs.get(x).name)) {
                            JOptionPane.showMessageDialog(this, "There is already a graph with this name", "Invalid name", JOptionPane.ERROR_MESSAGE);
                            duplicate = true;
                            break;
                        }
                    }
                    if (duplicate) continue;
                    graph = new Graph(name);
                    graphs.add(graph);
                    break;
                }
            }
        }
        Canvas new_canvas = new Canvas(graph, self, graph.zoom);
        character_panel.addListener(new_canvas);
        new_canvas.addMouseListener(self);
        new_canvas.setBackground(Color.WHITE);
        new_canvas.setComponentPopupMenu(right_click_menu);
        new_canvas.setBorder(BorderFactory.createBevelBorder(1));
        current_canvas = new_canvas;
        current_canvas.loadGraph();
        canvases.add(new_canvas);
        setDefaultLookAndFeelDecorated(true);
        tabs.add(graph.name, new_canvas);
        tabs.setSelectedIndex(tabs.getTabCount() - 1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
            mouse_down = true;
            trackGrab();
        }
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

    /*
        This section setups various keybinds for performing things such as saving and opening and creating a new project.
        As well as copying/pasting and undoing (whenever that is implemented)
     */

    private final String OPEN_PROJECT = "openProject";
    private final String SAVE_PROJECT = "saveProject";
    private final String SAVE_PROJECT_COPY = "saveCopyProject";
    private final String NEW_PROJECT = "newProject";

    Action openAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadProject();
        }
    };

    Action saveAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveProject(false);
        }
    };

    Action saveCopyAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveProject(true);
        }
    };

    Action newProjectAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            keeper.newProject();
        }
    };

    private void setupKeybinds() {
        InputMap imap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap amap = this.getRootPane().getActionMap();

        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), SAVE_PROJECT);
        amap.put(SAVE_PROJECT, saveAction);

        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), OPEN_PROJECT);
        amap.put(OPEN_PROJECT, openAction);

        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), SAVE_PROJECT_COPY);
        amap.put(SAVE_PROJECT_COPY, saveCopyAction);

        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), NEW_PROJECT);
        amap.put(NEW_PROJECT, newProjectAction);
    }
}
