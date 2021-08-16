package Managers;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
    This represents a person in the project. Storing the name, id, as well as an image and a list of properties.
 */
public class Person implements Serializable {

    public final int id;
    private int property_id = 0;
    private transient PropertyChangeSupport notifier;
    public String name;
    public transient Image image;
    public String img_name;
    public Map<Integer, Property> properties;

    /*
        Adds an update listener.
     */
    public void addListener(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener(listener);
    }

    /*
        Instantiates a person
     */
    public Person(int id, String name) {
        this.id = id;
        this.name = name;
        this.notifier = new PropertyChangeSupport(this);
        properties = new HashMap<>();
    }

    public void reInitNotifier() {
        notifier = new PropertyChangeSupport(this);
    }

    /*
        Attempts to add a new property to the list of properties. If there is already a property with such a name
        then it returns null, otherwise the property is added and returned.
     */
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

    /*
        Sets the name of the person and fires a property change notification so that all the lists are updated with the
        new name.
     */
    public void setName(String name) {
        this.name = name;
        notifier.firePropertyChange("name_change", "old", name);
    }

    public boolean isPropertyNameValid(String name) {
        for (Property property : properties.values()) {
            if (property.name.equals(name)) return false;
        }
        return true;
    }

    public Property getPropertyByName(String name) {
        for (Property property : properties.values()) {
            if (property.name.equals(name)) return property;
        }
        return null;
    }
}
