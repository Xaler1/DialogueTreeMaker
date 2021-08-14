package Panels;

import Frames.MainWindow;
import Helpers.OutConnector;
import Managers.Conditional;
import Nodes.Node;

import javax.swing.*;
import java.awt.*;

public class ConditionalBlock extends NodePanel{

    private final Conditional conditional;
    private final JLabel var1;
    private final JLabel var2;
    private final JLabel comparator;

    public ConditionalBlock(MainWindow window, Conditional conditional, NodePanel parent) {
        super(window, parent);
        this.conditional = conditional;

        setBorder(BorderFactory.createBevelBorder(1));
        setPreferredSize(new Dimension(100, 30));
        setBackground(Color.GREEN);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 10;
        constraints.ipady = 10;

        var1 = new JLabel(conditional.var1);
        var1.setPreferredSize(new Dimension(100, 30));
        var1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        var1.setHorizontalAlignment(SwingConstants.TRAILING);
        add(var1, constraints);

        constraints.gridx = 1;
        comparator = new JLabel(conditional.comparator);
        comparator.setPreferredSize(new Dimension(30, 30));
        comparator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        comparator.setHorizontalAlignment(SwingConstants.CENTER);
        add(comparator, constraints);

        constraints.gridx = 2;
        var2 = new JLabel(conditional.var2);
        var2.setPreferredSize(new Dimension(100, 30));
        var2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        var2.setHorizontalAlignment(SwingConstants.LEADING);
        add(var2, constraints);

        constraints.gridx = 3;
        out_connector = new OutConnector(this, window);
        out_connector.setConditional(conditional);
        out_connector.setHorizontalAlignment(SwingConstants.TRAILING);
        add(out_connector, constraints);
        rescale();
    }

    @Override
    public void setNode(Node node) {}

    @Override
    public void rescale() {
        Font new_font = window.main_font.deriveFont((float)(20 * canvas.scale.getX()));
        var1.setFont(new_font);
        var2.setFont(new_font);
        comparator.setFont(new_font);
        out_connector.rescale();
    }
}
