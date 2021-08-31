package StateMachines;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DialogueMachine {

    private String project_name;
    private int project_version;
    private HashMap<Integer, Person> people;

    private class Variable {
        public String name;
        public String type;
        public String default_value;
        public Field source = null;

        public Variable(String name, String type, String default_value) {
            this.name = name;
        }

        public void setSource(Field field) {
            field.setAccessible(true);
        }
    }

    private class Person {
        public String name;
        public int id;
        public String img_name;
        public Image img;
        public Map<String, Property> properties;

        public Person(int id, String name, String img_name) {
            this.id = id;
            this.name = name;
            this.img_name = img_name;
            try {
                img = ImageIO.read(new File(img_name));
            } catch (IOException ignored) {}
            properties = new HashMap<>();
        }

        public void addProperty(String name, String type, String value) {
            Comparable default_value = null;
            switch (type) {
                case "string" -> default_value = value;
                case "int" -> default_value = Integer.valueOf(value);
                case "float" -> default_value = Float.valueOf(value);
                case "bool" -> default_value = Boolean.valueOf(value);
            }
            properties.put(name, new Property(default_value));
        }
    }

    private class Property {
        public Class type;
        public Comparable value;

        public Property(Comparable value) {
            this.value = value;
            this.type = value.getClass();
        }
    }

    private class Conditional {
        public String var1_type;
        public String var2_type;
        public String var1;
        public String var2;
        public Comparator comparator;

        public Conditional (String var1_type, String var1, String comparator, String var2_type, String var2) {
            this.var1_type = var1_type;
            this.var1 = var1;
            this.var2_type = var2_type;
            this.var2 = var2;

        }
    }

    public DialogueMachine(File source) throws IOException {
        people = new HashMap<>();
        ObjectInputStream reader = new ObjectInputStream(new FileInputStream(source));
        project_name = reader.readUTF();
        project_version = reader.readInt();
        for (int i = 0; i < reader.readInt(); i++) {
            Person new_person = readPerson(reader);
            people.put(new_person.id, new_person);
        }
    }

    private Person readPerson(ObjectInputStream reader) throws IOException {
        int id = reader.readInt();
        String name = reader.readUTF();
        String img_name = reader.readUTF();
        return new Person(id, name, img_name);
    }

}
