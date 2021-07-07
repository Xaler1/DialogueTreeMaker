import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Canvas extends JPanel implements PropertyChangeListener {

    private final Graph graph;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public Canvas(Graph graph) {
        this.graph = graph;
    }
}
