package Frames;

import Managers.TreeKeeper;
import Panels.PersonBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PersonPanel extends JPanel implements PropertyChangeListener {
    private final TreeKeeper keeper;
    private final PropertyChangeSupport notifier;

    JButton button;
    JScrollPane scroll_pane;
    JPanel person_holder;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        notifier.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    public void addListener(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        notifier.removePropertyChangeListener(listener);
    }

    public PersonPanel(TreeKeeper keeper) {
        this.keeper = keeper;
        this.notifier = new PropertyChangeSupport(this);
        setLayout(new GridBagLayout());
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        GridBagConstraints constraints = new GridBagConstraints();

        setBackground(Color.white);
        setBorder(BorderFactory.createTitledBorder("Characters"));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 0.05;
        constraints.anchor = GridBagConstraints.WEST;
        button = new JButton("Add");
        button.setFont(button.getFont().deriveFont(20f));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createCharacter("");
            }
        });
        add(button, constraints);

        constraints.gridy = 1;
        constraints.gridheight = 20;
        constraints.weighty = 1;
        person_holder = new JPanel();
        person_holder.setLayout(new GridBagLayout());
        scroll_pane  = new JScrollPane(person_holder);
        add(scroll_pane, constraints);
    }

    private void createCharacter(String name) {
        if (name.equals("")) {
            while (true) {
                name = (String) JOptionPane.showInputDialog(
                        this,
                        "Enter the name of the character.",
                        "Character name",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        name
                );
                if (name == null) {
                    break;
                }
                if (name.trim().length() < 1) {
                    JOptionPane.showMessageDialog(this, "You must enter a name", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else {
                    int new_id = keeper.addCharacter(name);
                    if (new_id == -1) {
                        JOptionPane.showMessageDialog(this, "You need a unique character name", "Invalid name", JOptionPane.ERROR_MESSAGE);
                    } else {
                        GridBagConstraints constraints = new GridBagConstraints();
                        keeper.getPersonByName(name).addListener(this);
                        constraints.gridx = 0;
                        constraints.gridy = keeper.getNumCharacters() - 1;
                        constraints.anchor = GridBagConstraints.PAGE_START;
                        constraints.fill = GridBagConstraints.HORIZONTAL;
                        constraints.weightx = 1;
                        PersonBlock new_block = new PersonBlock(new_id, keeper, this);
                        notifier.firePropertyChange("person_create", "old", "new");
                        person_holder.add(new_block, constraints);
                        revalidate();
                        repaint();
                        break;
                    }
                }
            }
        }
    }

    public void remove_character(PersonBlock block) {
        keeper.removeCharacter(block.id);
        person_holder.remove(block);
        scroll_pane.revalidate();
        scroll_pane.repaint();
        notifier.firePropertyChange("person_delete", "old", "new");
    }
}
