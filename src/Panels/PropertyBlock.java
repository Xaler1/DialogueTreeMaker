package Panels;

import Frames.CharacterEditWindow;
import Helpers.Property;
import Managers.Character;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class PropertyBlock extends JPanel {

    protected final PropertyBlock self;
    private final CharacterEditWindow parent;
    private final Property property;

    private JTextField name_field;
    private String current_name;
    private JTextField value_field;
    private String current_value;
    private JComboBox<String> type_selector;
    private JComboBox<Boolean> bool_selector;

    public PropertyBlock(Property property, Character character, CharacterEditWindow parent) {
        this.self = this;
        this.parent = parent;
        this.property = property;
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
        type_selector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                property.type = (String)type_selector.getSelectedItem();
                setDefault(type_selector.getSelectedIndex());
            }
        });
        add(type_selector, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.2;
        name_field = new JTextField();
        name_field.setPreferredSize(new Dimension(120, 30));
        name_field.setFont(name_field.getFont().deriveFont(14f));
        name_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                checkName();
            }
        });
        add(name_field, constraints);
        name_field.setText(property.name);
        current_name = property.name;

        constraints.gridx = 2;
        constraints.weightx = 0.6;
        value_field = new JTextField();
        value_field.setFont(value_field.getFont().deriveFont(14f));
        value_field.setPreferredSize(new Dimension(170, 30));
        value_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                checkValue();
            }
        });
        add(value_field, constraints);
        value_field.setText(property.value);
        current_value = property.value;

        bool_selector = new JComboBox<>();
        bool_selector.addItem(true);
        bool_selector.addItem(false);
        bool_selector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                property.value = bool_selector.getSelectedItem().toString();
            }
        });
        add(bool_selector, constraints);

        constraints.gridx = 3;
        constraints.weightx = 0.05;
        Image img = null;
        try {
            img = ImageIO.read(new File("imgs/remove.png"));
        } catch (IOException ex) {}
        Image resized = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(resized);
        JLabel remove_btn = new JLabel(icon);
        remove_btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                character.properties.remove(property.id);
                parent.remove_property(self);
            }
        });
        remove_btn.setPreferredSize(new Dimension(30, 30));
        add(remove_btn, constraints);

        if (property.type.equals("bool")) {
            value_field.setVisible(false);
            bool_selector.setVisible(true);
            bool_selector.setSelectedItem(Boolean.valueOf(property.value));
        }
    }

    private void checkName() {
        String new_name = name_field.getText().replace(" ", "");
        if (new_name.length() == 0) {
            new_name = current_name;
        }
        current_name = new_name;
        name_field.setText(current_name);
        property.name = current_name;
        name_field.revalidate();
        name_field.repaint();
    }

    //TODO: figure out why the last typed character is shown even though it is replaced
    private void checkValue() {
        String new_value = value_field.getText();
        switch (type_selector.getSelectedIndex()) {
            case 0:
                current_value = new_value;
                break;
            case 1:
                try {
                    Integer.valueOf(new_value);
                } catch (NumberFormatException ex) {
                    break;
                }
                current_value = new_value;
                break;
            case 2:
                try {
                    Float.valueOf(new_value);
                } catch (NumberFormatException ex) {
                    break;
                }
                current_value = new_value;
                break;
        }
        value_field.setText(current_value);
        property.value = current_value;
        value_field.revalidate();
        value_field.repaint();
    }

    private void setDefault(int type) {
        switch (type) {
            case 0:
                value_field.setText("");
                property.value = "";
                bool_selector.setVisible(false);
                value_field.setVisible(true);
                break;
            case 1:
                value_field.setText("0");
                property.value = "0";
                bool_selector.setVisible(false);
                value_field.setVisible(true);
                break;
            case 2:
                value_field.setText("0.0");
                property.value = "0.0";
                bool_selector.setVisible(false);
                value_field.setVisible(true);
                break;
            case 3:
                property.value = "true";
                bool_selector.setSelectedItem(true);
                bool_selector.setVisible(true);
                value_field.setVisible(false);
        }
    }
}
