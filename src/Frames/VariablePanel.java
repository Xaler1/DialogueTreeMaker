package Frames;

import Managers.TreeKeeper;
import Managers.Variable;
import Panels.Blocks.VariableBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
    This assembles and handles the creation of variables for the project that can be used for conditional statements.
 */
public class VariablePanel extends JPanel {

    private final JPanel holder;
    private final TreeKeeper keeper;
    private int variable_counter = 0;

    public VariablePanel(TreeKeeper keeper) {
        this.keeper = keeper;

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Variables"));
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        JButton add_btn = new JButton("Add");
        add_btn.setFont(add_btn.getFont().deriveFont(20f));
        add_btn.setPreferredSize(new Dimension(100, 30));
        add_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createVariable(null);
            }
        });
        add(add_btn, constraints);

        constraints.gridy = 1;
        constraints.weighty = 1;
        holder = new JPanel();
        holder.setLayout(new GridBagLayout());
        JScrollPane pane = new JScrollPane(holder);
        add(pane, constraints);

        for (Variable variable : keeper.getVariables()) {
            createVariable(variable);
        }
    }

    private void createVariable(Variable variable) {
        if (variable == null) {
            while (true) {
                String name = "";
                name = (String) JOptionPane.showInputDialog(
                        this,
                        "Enter the name of the variable.",
                        "Variable name",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        name
                );
                if (name == null) {
                    return;
                }
                name = name.strip();
                if (name.length() == 0) {
                    JOptionPane.showMessageDialog(this, "You must enter a name", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else if (name.contains(" ")) {
                    JOptionPane.showMessageDialog(this, "The name cannot contain spaces", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else if (!name.matches("^[[a-zA-Z]+_+]+[[a-zA-Z]*_*\\-*[a-zA-Z]*[0-9]*]*$")) {
                    JOptionPane.showMessageDialog(this, "The name does not conform to the java variable naming standards", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else {
                    variable = keeper.addVariable(name);
                    if (variable == null) {
                        JOptionPane.showMessageDialog(this, "Variables must have unique names", "Invalid name", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    break;
                }
            }
        }
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = variable_counter;
        variable_counter++;
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        holder.add(new VariableBlock(variable, keeper), constraints);
        revalidate();
        repaint();
    }
}
