package Managers;

import Nodes.Node;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class Conditional implements Serializable {

    public Person person;
    private boolean is_default = false;
    public String var1_type = "int";
    public String var1 = "1";
    public String comparator = "=";
    public String var2_type = "int";
    public String var2 = "1";
    private transient PropertyChangeSupport notifier = new PropertyChangeSupport(this);

    public Node child;

    public void addListener(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    public Conditional(Person person) {
        this.person = person;
    }

    public Conditional(boolean is_default) {
        this.is_default = is_default;
        person = null;
    }

    public void reInit() {
        notifier = new PropertyChangeSupport(this);
    }

    public boolean is_satisfied() {
        if (is_default) return true;
        switch (var1_type) {
            case "string":
                if (comparator.equals("=")) {
                    return var1.equals(var2);
                } else {
                    return !var1.equals(var2);
                }
            case "float":
            case "int":
                float var1_float = Float.valueOf(var1);
                float var2_float = Float.valueOf(var2);
                switch (comparator) {
                    case "=":
                        return var1_float == var2_float;
                    case ">":
                        return var1_float > var2_float;
                    case "<":
                        return var1_float < var2_float;
                    case ">=":
                        return var1_float >= var2_float;
                    case "<=":
                        return var1_float <= var2_float;
                    case "!=":
                        return var1_float != var2_float;
                }
            case "bool":
                boolean var1_bool = Boolean.valueOf(var1);
                boolean var2_bool = Boolean.valueOf(var2);
                if (comparator.equals("=")) {
                    return  var1_bool == var2_bool;
                } else {
                    return var1_bool != var2_bool;
                }
        }
        return false;
    }

    public void fireUpdate() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Thread.sleep(100);
                notifier.firePropertyChange("conditional_change", "0", "1");
                return null;
            }
        };
        worker.execute();
    }
}
