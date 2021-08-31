package Panels.Blocks;

import Frames.ConditionalEditWindow;
import Frames.MainWindow;
import Helpers.OutConnector;
import Managers.Conditional;
import Nodes.Node;
import Panels.NodePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ConditionalBlock extends NodePanel implements PropertyChangeListener {

    public final Conditional conditional;

    private final JLabel remove_btn;
    private final JLabel modify_btn;
    private final JLabel var1;
    private final JLabel var2;
    private final JLabel comparator;

    private final ConditionalBlock self;

    public ConditionalBlock(MainWindow window, Conditional conditional, NodePanel parent) {
        super(window, parent);
        this.self = this;
        this.conditional = conditional;


        setPreferredSize(new Dimension(100, 20));
        loadBackground("lightgreen");
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 0.4;
        constraints.gridx = 0;
        constraints.gridy = 0;

        remove_btn = new JLabel("X");
        remove_btn.setPreferredSize(new Dimension(40, 30));
        remove_btn.setHorizontalAlignment(SwingConstants.CENTER);
        remove_btn.setBorder(BorderFactory.createBevelBorder(0));
        remove_btn.setBackground(Color.WHITE);
        remove_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.removeChild(self);
            }
        });
        add(remove_btn, constraints);

        constraints.gridx = 1;
        modify_btn = new JLabel("M");
        modify_btn.setPreferredSize(new Dimension(40, 30));
        modify_btn.setHorizontalAlignment(SwingConstants.CENTER);
        modify_btn.setBorder(BorderFactory.createBevelBorder(0));
        modify_btn.setBackground(Color.WHITE);
        modify_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog dialog = new ConditionalEditWindow(conditional, keeper);
                dialog.setVisible(true);
            }
        });
        add(modify_btn, constraints);

        constraints.gridx = 2;
        constraints.weightx = 1;
        var1 = new JLabel(conditional.var1);
        var1.setPreferredSize(new Dimension(100, 30));
        var1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        var1.setHorizontalAlignment(SwingConstants.TRAILING);
        add(var1, constraints);

        constraints.gridx = 3;
        comparator = new JLabel(conditional.comparator);
        comparator.setPreferredSize(new Dimension(30, 30));
        comparator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        comparator.setHorizontalAlignment(SwingConstants.CENTER);
        add(comparator, constraints);

        constraints.gridx = 4;
        var2 = new JLabel(conditional.var2);
        var2.setPreferredSize(new Dimension(100, 30));
        var2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        var2.setHorizontalAlignment(SwingConstants.LEADING);
        add(var2, constraints);

        constraints.gridx = 5;
        constraints.weightx = 0.3;
        out_connector = new OutConnector(this, window);
        out_connector.setConditional(conditional);
        add(out_connector, constraints);
        rescale();
    }

    @Override
    public void setNode(Node node) {}

    @Override
    public void rescale() {
        Font new_font = window.main_font.deriveFont((float)(20 * canvas.scale.getX()));
        remove_btn.setFont(new_font);
        modify_btn.setFont(new_font);
        var1.setFont(new_font);
        var2.setFont(new_font);
        comparator.setFont(new_font);
        out_connector.rescale();
    }

    public NodePanel getConditionalParent() {
        return parent;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var1.setText(conditional.var1);
        var2.setText(conditional.var2);
        comparator.setText(conditional.comparator);
    }
}
