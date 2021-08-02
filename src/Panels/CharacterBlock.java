package Panels;

import Frames.CharacterEditWindow;
import Frames.CharacterPanel;
import Managers.TreeKeeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterBlock extends JPanel {

    private final TreeKeeper keeper;

    private JLabel name_label;

    public final CharacterBlock self;
    private final CharacterPanel parent;
    public final int id;

    public CharacterBlock(int id, TreeKeeper keeper, CharacterPanel parent) {
        this.keeper = keeper;
        this.self = this;
        this.parent = parent;
        this.id = id;
        String name = keeper.getCharacterName(id);
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        setBorder(BorderFactory.createBevelBorder(1));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 1;
        String actual = name.substring(0);
        if (name.length() > 20) {
            actual = name.substring(0, 17) +  "...  ";
        }
        if (name.length() < 20) {
            actual = name + "  ".repeat(20 - name.length());
        }
        name_label = new JLabel(actual);
        add(name_label, constraints);

        JButton button = new JButton("M");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new CharacterEditWindow(id, keeper);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        add(button, constraints);

        constraints.gridx = 2;
        button = new JButton("X");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove_character(self);
            }
        });
        add(button, constraints);
    }

    @Override
    public void repaint() {
        try {
            String name = keeper.getCharacterName(id);
            String actual = name.substring(0);
            if (name.length() > 20) {
                actual = name.substring(0, 17) +  "...  ";
            }
            if (name.length() < 20) {
                actual = name + "  ".repeat(20 - name.length());
            }
            name_label.setText(actual);
        } catch (NullPointerException ex) {}
        super.repaint();
    }
}