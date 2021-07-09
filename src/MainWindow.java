import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame implements MouseListener {

    private List<Graph> graphs;

    private boolean mouse_down = false;

    JMenuBar menu_bar = new JMenuBar();
    JMenu menu, submenu;
    JMenuItem menu_item;
    JButton test;
    MainWindow self;

    private JTabbedPane tabs = new JTabbedPane();
    List<Canvas> canvases;

    public MainWindow(List<Graph> graphs, String name) {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        self = this;

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
        test= new JButton("test");

        menu = new JMenu("Add");
        menu_bar.add(menu);
        menu_item = new JMenuItem("Graph");
        menu_item.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Graph new_graph = new Graph("Untitled");
                graphs.add(new_graph);
                Canvas new_canvas = new Canvas(new_graph);
                new_canvas.addMouseListener(self);
                test = new JButton("test");
                test.setBounds(10, 10, 100, 100);
                new_canvas.add(test);
                canvases.add(new_canvas);
                tabs.add("Untitiled", new_canvas);
            }
        });
        menu_item.setText("Graph");
        menu.add(menu_item);
        menu_item = new JMenuItem("Data folder");
        menu.add(menu_item);

        canvases = new ArrayList<>();
        for (Graph graph : graphs) {
            Canvas canvas = new Canvas(graph);
            canvas.addMouseListener(this);
            canvas.setBackground(Color.WHITE);
            canvas.setBorder(BorderFactory.createBevelBorder(1));
            tabs.add(graph.name, canvas);
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
    }

    public void trackDrag() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Point2D old = new Point2D.Double(10, 10);
                while (mouse_down) {
                    Point2D mouse_loc = MouseInfo.getPointerInfo().getLocation();
                    if (mouse_loc.distance(old) > 1) {
                        old = mouse_loc;
                        test.setBounds((int)mouse_loc.getX(), (int)mouse_loc.getY(), 100, 100);
                        //self.update(self.getGraphics());
                    }
                    Thread.sleep(10);
                }
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouse_down = true;
        trackDrag();
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
}
