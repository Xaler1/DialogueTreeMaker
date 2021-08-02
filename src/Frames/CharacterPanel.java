package Frames;

import Managers.Graph;
import Managers.TreeKeeper;
import Panels.CharacterBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterPanel extends JPanel {
    private final TreeKeeper keeper;

    JButton button;
    JScrollPane scroll_pane;
    JPanel person_holder;

    public CharacterPanel(TreeKeeper keeper) {
        this.keeper = keeper;
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
                        constraints.gridx = 0;
                        constraints.gridy = keeper.getNumCharacters() - 1;
                        constraints.anchor = GridBagConstraints.PAGE_START;
                        constraints.fill = GridBagConstraints.HORIZONTAL;
                        constraints.weightx = 1;
                        person_holder.add(new CharacterBlock(new_id, keeper, this), constraints);
                        revalidate();
                        repaint();
                        break;
                    }
                }
            }
        }
    }

    public void remove_character(CharacterBlock block) {
        keeper.removeCharacter(block.id);
        person_holder.remove(block);
        scroll_pane.revalidate();
        scroll_pane.repaint();
    }
}
