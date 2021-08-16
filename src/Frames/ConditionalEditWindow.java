package Frames;

import Managers.Conditional;
import Managers.Property;
import Managers.TreeKeeper;
import Managers.Variable;

import javax.swing.*;
import java.awt.*;

public class ConditionalEditWindow extends JDialog {

    private final Conditional conditional;
    private final TreeKeeper keeper;

    private final JComboBox<String> var1_selector;
    private final JTextField var1_entry;
    private final JComboBox<String> comparator_selector;
    private final JComboBox<String> var2_selector;
    private final JTextField var2_entry;
    private final JComboBox<String> var2_type_selector;

    private String actual_type;

    public ConditionalEditWindow(Conditional conditional, TreeKeeper keeper) {
        this.conditional = conditional;
        this.keeper = keeper;

        actual_type = conditional.var1_type;
        if (actual_type.equals("Person Property")) {
            Property property = conditional.person.getPropertyByName(conditional.var1);
            if (property == null) {
                actual_type = "";
            } else {
                actual_type = property.type;
            }
        } else if (actual_type.equals("Variable")) {
            Variable variable = keeper.getVariableByName(conditional.var1);
            if (variable == null) {
                actual_type = "";
            } else {
                actual_type = variable.type;
            }
        }

        setModal (true);
        setAlwaysOnTop (true);
        setSize(new Dimension(500, 100));
        setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;

        JComboBox<String> var1_type_selector = new JComboBox<>();
        Font font = var1_type_selector.getFont().deriveFont(20f);
        var1_type_selector.addItem("string");
        var1_type_selector.addItem("int");
        var1_type_selector.addItem("float");
        var1_type_selector.addItem("bool");
        if (conditional.person != null && conditional.person.properties.size() > 0) {
            var1_type_selector.addItem("Person Property");
        }
        if (keeper.getVariables().size() > 0) {
            var1_type_selector.addItem("Variable");
        }
        var1_type_selector.setPreferredSize(new Dimension(100, 30));
        var1_type_selector.setFont(font);
        var1_type_selector.setSelectedItem(conditional.var1_type);
        var1_type_selector.addItemListener(e -> {
            conditional.var1_type = (String) e.getItem();
            setComparatorDefaults();
            setVar1Defaults();
            setVar2SelectorDefaults();
        });
        add(var1_type_selector, constraints);

        constraints.gridy = 1;
        var1_entry = new JTextField();
        var1_entry.setPreferredSize(new Dimension(100, 30));
        var1_entry.setFont(font);
        var1_entry.setText(conditional.var1);
        add(var1_entry, constraints);

        var1_selector = new JComboBox<>();
        var1_selector.setPreferredSize(new Dimension(100, 30));
        var1_selector.setFont(font);
        var1_selector.setSelectedItem(conditional.var1);
        var1_selector.addItemListener(e -> {
            conditional.var1 = (String) e.getItem();
            setComparatorDefaults();
            setVar2SelectorDefaults();
        });
        add(var1_selector, constraints);

        constraints.gridx = 1;
        comparator_selector = new JComboBox<>();
        comparator_selector.setPreferredSize(new Dimension(40, 30));
        comparator_selector.setFont(font);
        comparator_selector.setSelectedItem(conditional.comparator);
        add(comparator_selector, constraints);

        constraints.gridx = 2;
        var2_entry = new JTextField();
        var2_entry.setPreferredSize(new Dimension(100, 30));
        var2_entry.setFont(font);
        var2_entry.setText(conditional.var1);
        add(var2_entry, constraints);

        var2_selector = new JComboBox<>();
        var2_selector.setPreferredSize(new Dimension(100, 30));
        var2_selector.setFont(font);
        add(var2_selector, constraints);

        constraints.gridy = 0;
        var2_type_selector = new JComboBox<>();
        var2_type_selector.setPreferredSize(new Dimension(100, 30));
        var2_type_selector.setFont(font);
        var2_type_selector.setSelectedItem(conditional.var2_type);
        var2_type_selector.addItemListener(e -> {
            conditional.var2_type = (String) e.getItem();
            setVar2Defaults();
        });
        add(var2_type_selector, constraints);

        setComparatorDefaults();
        setVar1Defaults();
        setVar2SelectorDefaults();
        setVar2Defaults();
    }

    private void setDefaults() {
        setVar1Defaults();
        setComparatorDefaults();
        setVar2SelectorDefaults();
        setVar2Defaults();
    }

    private void setComparatorDefaults() {
        String[] comparators = new String[]{};
        actual_type = conditional.var1_type;
        if (actual_type.equals("Person Property")) {
            Property property = conditional.person.getPropertyByName(conditional.var1);
            if (property == null) {
                actual_type = "";
            } else {
                actual_type = property.type;
            }
        } else if (actual_type.equals("Variable")) {
            Variable variable = keeper.getVariableByName(conditional.var1);
            if (variable == null) {
                actual_type = "";
            } else {
                actual_type = variable.type;
            }
        }
        switch (actual_type) {
            case "bool", "string" -> comparators = new String[]{"=", "!="};
            case "int", "float" -> comparators = new String[]{"=", ">", "<", ">=", "<=", "!="};
        }
        String last_comparator = conditional.comparator;
        comparator_selector.removeAllItems();
        for (String comparator : comparators) {
            comparator_selector.addItem(comparator);
        }
        comparator_selector.setSelectedItem(last_comparator);
    }

    private void setVar1Defaults() {
        var1_selector.removeAllItems();
        if (conditional.var1_type.equals("float") || conditional.var1_type.equals("int") || conditional.var1_type.equals("string")) {
            var1_entry.setVisible(true);
            var1_selector.setVisible(false);
        } else {
            var1_entry.setVisible(false);
            var1_selector.setVisible(true);
        }
        if (conditional.var1_type.equals("bool")) {
            var1_selector.addItem("true");
            var1_selector.addItem("false");
        } else if (conditional.var1_type.equals("Person Property")) {
            for (Property property : conditional.person.properties.values()) {
                var1_selector.addItem(property.name);
            }
        } else if (conditional.var1_type.equals("Variable")) {
            for (Variable variable : keeper.getVariables()) {
                var1_selector.addItem(variable.name);
            }
        }
    }

    private void setVar2SelectorDefaults() {
        String last_type2 = conditional.var2_type;
        var2_type_selector.removeAllItems();
        if (actual_type.equals("float") || actual_type.equals("int")) {
            var2_type_selector.addItem("int");
            var2_type_selector.addItem("float");
        } else {
            var2_type_selector.addItem(actual_type);
        }
        if (conditional.person != null && conditional.person.properties.size() > 0) {
            var2_type_selector.addItem("Person Property");
        }
        if (keeper.getVariables().size() > 0) {
            var2_type_selector.addItem("Variable");
        }
        var2_type_selector.setSelectedItem(last_type2);
    }

    private void setVar2Defaults() {
        String last_var2 = conditional.var2;
        var2_selector.removeAllItems();
        if (conditional.var2_type.equals("string") || conditional.var2_type.equals("int") || conditional.var2_type.equals("float")) {
            var2_selector.setVisible(false);
            var2_entry.setVisible(true);
        } else {
            var2_selector.setVisible(true);
            var2_entry.setVisible(false);
        }
        switch (conditional.var2_type) {
            case "bool":
                var2_selector.addItem("true");
                var2_selector.addItem("false");
                break;
            case "int":
                var2_entry.setText("0");
                try {
                    var2_entry.setText(String.valueOf(Integer.parseInt(last_var2)));
                } catch (NumberFormatException ignored) {}
                break;
            case "float":
                var2_entry.setText("0.0");
                try {
                    var2_entry.setText(String.valueOf(Float.parseFloat(last_var2)));
                } catch (NumberFormatException ignored) {}
                break;
            case "string":
                var2_entry.setText("");
                break;
            case "Person Property":
                for (Property property : conditional.person.properties.values()) {
                    if (actual_type.equals("float") || actual_type.equals("int")) {
                        if (property.type.equals("float") || property.type.equals("int")) {
                            var2_selector.addItem(property.name);
                        }
                    } else {
                        if (property.type.equals(actual_type)) {
                            var2_selector.addItem(property.name);
                        }
                    }
                }
                break;
            case "Variable":
                for (Variable variable : keeper.getVariables()) {
                    if (actual_type.equals("float") || actual_type.equals("int")) {
                        if (variable.type.equals("float") || variable.type.equals("int")) {
                            var2_selector.addItem(variable.name);
                        }
                    } else {
                        if (variable.type.equals(actual_type)) {
                            var2_selector.addItem(variable.name);
                        }
                    }
                }
                break;
        }
        var2_selector.setSelectedItem(last_var2);
    }
}
