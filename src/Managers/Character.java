package Managers;

import Helpers.Property;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Character {

    public final int id;
    private int property_id = 0;
    public String name;
    public Image image;
    public String img_name;
    public Map<Integer, Property> properties;

    public Character(int id, String name) {
        this.id = id;
        this.name = name;
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
}
