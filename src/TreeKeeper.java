import java.util.LinkedList;
import java.util.List;

public class TreeKeeper {

    private List<Graph> graphs;

    public TreeKeeper() {
        graphs = new LinkedList<>();
        MainWindow window = new MainWindow(graphs, "Untitled project");
    }
}
