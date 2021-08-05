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

/*
    This assembles and handles the management of a list of people in the current project. Since they are the same for
    all the graphs, there is only one in the window
 */
public class PersonPanel extends JPanel implements PropertyChangeListener {
    private final TreeKeeper keeper;
    private final PropertyChangeSupport notifier;

    JButton button;
    JScrollPane scroll_pane;
    JPanel person_holder;

    /*
        This mostly acts as a property change collector - funneling all the property changes of people further up to
        the canvases. This was done mostly to avoid having to link each canvas to each new person.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        notifier.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    /*
        This adds a new property change listener to the notifier.
     */
    public void addListener(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    /*
        This removes a property change listener from the notifier. Needed for when canvases are removed, so that the
        garbage collector can remove them.
     */
    public void removeListener(PropertyChangeListener listener) {
        notifier.removePropertyChangeListener(listener);
    }

    /*
        This assembles the person panel.
     */
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

    /*
        This handles character creation - adding a new character to the project and also a character block to the list.
        First the user is prompted for a name until they enter a name that is not empty and unique.
     */
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
                        //TODO: figure out how to added the blocks from the top while preserving all the filling properties
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

    /*
        This removes a character from the project and the corresponding block from the list.
     */
    public void remove_character(PersonBlock block) {
        keeper.removeCharacter(block.id);
        person_holder.remove(block);
        scroll_pane.revalidate();
        scroll_pane.repaint();
        notifier.firePropertyChange("person_delete", "old", "new");
    }
}
