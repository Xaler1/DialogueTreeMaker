package Managers;

import Helpers.Property;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class Person {

    public final int id;
    private int property_id = 0;
    private final PropertyChangeSupport notifier;
    public String name;
    public Image image;
    public String img_name;
    public Map<Integer, Property> properties;

    public void addListener(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
        this.notifier = new PropertyChangeSupport(this);
        properties = new HashMap<>();
    }

    public Property addProperty(String name) {
        for (Property property : properties.values()) {
            if (property.name.equals(name)) {
                return null;
            }
        }
        Property property = new Property(property_id);
        property.name = name;
        properties.put(property_id, property);
        property_id++;
        return property;
    }

    public void setName(String name) {
        this.name = name;
        notifier.firePropertyChange("name_change", "old", name);
    }
}
