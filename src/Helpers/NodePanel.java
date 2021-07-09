package Helpers;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodePanel extends JPanel {
    private final List<OutConnector> out_connectors;
    private InConnector in_connector;

    public NodePanel() {
        out_connectors = new ArrayList<>();

        setLayout(null);
    }

    public void addOutConnector(OutConnector connector) {
        out_connectors.add(connector);
    }

    public void setInConnector(InConnector connector) {
        add(connector);
        in_connector = connector;
    }

    public InConnector getInConnector() {
        return in_connector;
    }

    public Iterable<OutConnector> getOutConnectors() {
        return out_connectors;
    }
}
