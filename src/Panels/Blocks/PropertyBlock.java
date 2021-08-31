package Panels.Blocks;

import Frames.CharacterEditWindow;
import Managers.Person;
import Managers.Property;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/*
    This is a visual representation of a property of a person. It allows the setting of the type of the property, its
    name and value.
 */
public class PropertyBlock extends JPanel {

    protected final PropertyBlock self;
    private final Property property;
    private final Person person;

    private final JTextField name_entry;
    private final JTextField value_entry;
    private final JComboBox<String> type_selector;
    private final JComboBox<Boolean> bool_selector;

    /*
        This assembles the panel using a gridbag layout.
     */
    public PropertyBlock(Property property, Person person, CharacterEditWindow parent) {
        this.self = this;
        this.property = property;
        this.person = person;
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createBevelBorder(1));
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.1;

        type_selector = new JComboBox<>();
        type_selector.addItem("string");
        type_selector.addItem("int");
        type_selector.addItem("float");
        type_selector.addItem("bool");
        type_selector.setSelectedItem(property.type);
        type_selector.setPreferredSize(new Dimension(80, 30));
        type_selector.addItemListener(e -> {
            property.type = (String)type_selector.getSelectedItem();
            setDefault(type_selector.getSelectedIndex());
        });
        add(type_selector, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.2;
        name_entry = new JTextField();
        name_entry.setPreferredSize(new Dimension(120, 30));
        name_entry.setFont(name_entry.getFont().deriveFont(14f));
        name_entry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                checkName();
            }
        });
        add(name_entry, constraints);
        name_entry.setText(property.name);

        constraints.gridx = 2;
        constraints.weightx = 0.6;
        value_entry = new JTextField();
        value_entry.setFont(value_entry.getFont().deriveFont(14f));
        value_entry.setPreferredSize(new Dimension(170, 30));
        value_entry.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                checkValue(e);
            }
        });
        value_entry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (type_selector.getSelectedIndex() != 3) {
                    property.value = value_entry.getText();
                }
            }
        });
        add(value_entry, constraints);
        value_entry.setText(property.value);

        bool_selector = new JComboBox<>();
        bool_selector.addItem(true);
        bool_selector.addItem(false);
        bool_selector.addItemListener(e -> property.value = bool_selector.getSelectedItem().toString());
        add(bool_selector, constraints);

        constraints.gridx = 3;
        constraints.weightx = 0.05;
        Image img = null;
        try {
            img = ImageIO.read(new File("imgs/remove.png"));
        } catch (IOException ignored) {}
        Image resized = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(resized);
        JLabel remove_btn = new JLabel(icon);
        remove_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                person.properties.remove(property.id);
                parent.remove_property(self);
            }
        });
        remove_btn.setPreferredSize(new Dimension(30, 30));
        add(remove_btn, constraints);

        if (property.type.equals("bool")) {
            value_entry.setVisible(false);
            bool_selector.setVisible(true);
            bool_selector.setSelectedItem(Boolean.valueOf(property.value));
        }
    }

    /*
        Checks whether the name entered is valid. If it is then update the name, if not then revert to the previous name.
     */
    private void checkName() {
        String new_name = name_entry.getText().trim();
        if (new_name.length() == 0) {
            JOptionPane.showMessageDialog(this, "A property cannot have an empty name", "Invalid name", JOptionPane.ERROR_MESSAGE);
            name_entry.setText(property.name);
        } else if (!person.isPropertyNameValid(new_name)) {
            JOptionPane.showMessageDialog(this, "Properties cannot have duplicate names", "Invalid name", JOptionPane.ERROR_MESSAGE);
            name_entry.setText(property.name);
        } else {
            property.name = new_name;
        }
    }

    /*
        This checks whether the value being entered is valid under the current property type. If it is then update the
        value. If it is not then set the value entry to not be editable so that the last pressed key does not register.
     */
    private void checkValue(KeyEvent e) {
        if (e.isActionKey() || e.getKeyCode() == 8) {
            value_entry.setEditable(true);
            return;
        }
        int loc = value_entry.getCaretPosition();
        String new_value = value_entry.getText();
        new_value = new_value.substring(0, loc) + e.getKeyChar() + new_value.substring(loc);
        switch (type_selector.getSelectedIndex()) {
            case 0 -> value_entry.setEditable(true);
            case 1 -> {
                try {
                    Integer.valueOf(new_value);
                } catch (NumberFormatException ex) {
                    value_entry.setEditable(false);
                    return;
                }
                value_entry.setEditable(true);
            }
            case 2 -> {
                try {
                    Float.valueOf(new_value);
                } catch (NumberFormatException ex) {
                    value_entry.setEditable(false);
                    return;
                }
                value_entry.setEditable(true);
            }
        }
    }

    /*
        This sets up the default value of the property according to which property type has been chosen.
     */
    private void setDefault(int type) {
        switch (type) {
            case 0 -> {
                value_entry.setText("");
                property.value = "";
                bool_selector.setVisible(false);
                value_entry.setVisible(true);
            }
            case 1 -> {
                value_entry.setText("0");
                property.value = "0";
                bool_selector.setVisible(false);
                value_entry.setVisible(true);
            }
            case 2 -> {
                value_entry.setText("0.0");
                property.value = "0.0";
                bool_selector.setVisible(false);
                value_entry.setVisible(true);
            }
            case 3 -> {
                property.value = "true";
                bool_selector.setSelectedItem(true);
                bool_selector.setVisible(true);
                value_entry.setVisible(false);
            }
        }
    }
}
