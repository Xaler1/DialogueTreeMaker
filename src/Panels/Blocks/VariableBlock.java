package Panels.Blocks;

import Managers.TreeKeeper;
import Managers.Variable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class VariableBlock extends JPanel {
    private final Variable variable;
    private final VariableBlock self;
    private String last_name;

    private final JTextField value_entry;
    private final JTextField name_entry;
    private final JComboBox<String> bool_chooser;

    public VariableBlock(Variable variable, TreeKeeper keeper) {
        this.variable = variable;
        this.self = this;
        last_name = variable.name;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createBevelBorder(1));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.weightx = 1.2;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;

        JComboBox<String> type_selector = new JComboBox<>();
        type_selector.addItem("string");
        type_selector.addItem("int");
        type_selector.addItem("float");
        type_selector.addItem("bool");
        type_selector.setSelectedItem(variable.type);
        type_selector.setPreferredSize(new Dimension(70, 30));
        type_selector.addItemListener(e -> {
            variable.setType((String) e.getItem());
            setDefaultValue();
        });
        add(type_selector, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.2;
        name_entry = new JTextField(variable.name);
        name_entry.setPreferredSize(new Dimension(70, 30));
        name_entry.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                checkName(e);
            }
        });
        name_entry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (name_entry.getText().equals(last_name)) return;
                if (!keeper.isVariableNameValid(name_entry.getText())) {
                    name_entry.setText(last_name);
                    JOptionPane.showMessageDialog(getParent(), "Cannot have duplicate variable names", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else if (name_entry.getText().length() == 0) {
                    name_entry.setText(last_name);
                    JOptionPane.showMessageDialog(getParent(), "A variable name cannot be empty", "Invalid name", JOptionPane.ERROR_MESSAGE);
                } else {
                    last_name = name_entry.getText();
                }
                variable.name = name_entry.getText();
            }
        });
        add(name_entry, constraints);

        constraints.gridx = 2;
        value_entry = new JTextField(variable.default_value);
        value_entry.setPreferredSize(new Dimension(70, 30));
        value_entry.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                checkValue(e);
            }
        });
        value_entry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                switch (variable.type) {
                    case "string":
                        break;
                    case "int":
                        if (value_entry.getText().equals("")) value_entry.setText("0");
                        break;
                    case "float":
                        if (value_entry.getText().equals("")) value_entry.setText("0.0");
                        break;
                }
                variable.default_value = value_entry.getText();
            }
        });
        add(value_entry, constraints);

        bool_chooser = new JComboBox<>();
        bool_chooser.addItem("true");
        bool_chooser.addItem("false");
        if (variable.type.equals("bool")) {
            bool_chooser.setSelectedItem(variable.default_value);
            bool_chooser.setVisible(true);
            value_entry.setVisible(false);
        } else {
            bool_chooser.setVisible(false);
        }
        bool_chooser.setPreferredSize(new Dimension(70, 30));
        bool_chooser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                variable.default_value = (String) e.getItem();
            }
        });
        add(bool_chooser, constraints);

        constraints.gridx = 3;
        Image img = null;
        try {
            img = ImageIO.read(new File("imgs/remove.png"));
        } catch (IOException ignored) {}
        Image resized = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(resized);
        JLabel remove_btn = new JLabel(icon);
        remove_btn.setPreferredSize(new Dimension(30, 30));
        remove_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Container parent = getParent();
                parent.remove(self);
                parent.revalidate();
                parent.repaint();
                keeper.removeVariable(variable.id);
            }
        });
        add(remove_btn, constraints);
    }

    private void setDefaultValue() {
        switch (variable.type) {
            case "string" -> value_entry.setText("");
            case "int" -> value_entry.setText("0");
            case "float" -> value_entry.setText("0.0");
            case "bool" -> {
                value_entry.setVisible(false);
                bool_chooser.setVisible(true);
                bool_chooser.setSelectedItem("false");
                return;
            }
        }
        value_entry.setVisible(true);
        bool_chooser.setVisible(false);
        variable.default_value = value_entry.getText();
    }

    private void checkName(KeyEvent e) {
        if (e.getKeyCode() == 8 || e.isActionKey()) {
            name_entry.setEditable(true);
            return;
        }
        int loc = name_entry.getCaretPosition();
        String new_name = name_entry.getText();
        new_name = new_name.substring(0, loc) + e.getKeyChar() + new_name.substring(loc);
        if (!new_name.matches("^[[a-zA-Z]+_+]+[[a-zA-Z]*_*[a-zA-Z]*[0-9]*]*$")) {
            name_entry.setEditable(false);
        } else {
            name_entry.setEditable(true);
        }
    }

    private void checkValue(KeyEvent e) {
        if (e.getKeyCode() == 8 || e.isActionKey()) {
            value_entry.setEditable(true);
            return;
        }
        int loc = value_entry.getCaretPosition();
        String new_value = value_entry.getText();
        new_value = new_value.substring(0, loc) + e.getKeyChar() + new_value.substring(loc);
        switch (variable.type) {
            case "string":
                value_entry.setEditable(true);
                break;
            case "int":
                try {
                    Integer.valueOf(new_value);
                } catch (NumberFormatException ex) {
                    value_entry.setEditable(false);
                    return;
                }
                value_entry.setEditable(true);
                break;
            case "float":
                try {
                    Float.valueOf(new_value);
                } catch (NumberFormatException ex) {
                    value_entry.setEditable(false);
                    return;
                }
                value_entry.setEditable(true);
                break;
        }
    }
}
