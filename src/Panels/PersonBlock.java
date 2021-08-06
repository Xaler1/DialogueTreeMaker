package Panels;

import Frames.CharacterEditWindow;
import Frames.PersonPanel;
import Managers.TreeKeeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/*
    A visual representation of a person. For simplicity only shows the name and then gives a button to open the
    person editing window. Also has a delete button for removing a person from the project.
 */
public class PersonBlock extends JPanel implements PropertyChangeListener {

    private final TreeKeeper keeper;

    private JLabel name_label;

    public final PersonBlock self;
    public final int id;

    /*
        This assembles the panel using a gridbag layout.
     */
    public PersonBlock(int id, TreeKeeper keeper, PersonPanel parent) {
        this.keeper = keeper;
        this.self = this;
        this.id = id;
        String name = keeper.getCharacterName(id);
        keeper.getPerson(id).addListener(this);
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
        name_label.setPreferredSize(new Dimension(100, 30));
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
        button.setPreferredSize(new Dimension(30, 30));
        add(button, constraints);

        constraints.gridx = 2;
        button = new JButton("X");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove_character(self);
            }
        });
        button.setPreferredSize(new Dimension(30, 30));
        add(button, constraints);
    }

    /*
        Detects a property change when a person has been renamed.
        //TODO: lol this is dumb, doesn't even check which person's name has been changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("name_change")) {
            name_label.setText((String) evt.getNewValue());
        }
    }
}