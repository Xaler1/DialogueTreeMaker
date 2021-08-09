package Managers;

/*
    This class is for storing information about variables that can be used in conditional statements.
 */
public class Variable {
    public Class type = String.class;
    public String name = "";
    public String default_value = "";
    public final int id;

    public Variable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void setType(String type) {
        switch (type) {
            case "string" -> this.type = String.class;
            case "int" -> this.type = Integer.class;
            case "float" -> this.type = Float.class;
            case "bool" -> this.type = Boolean.class;
        }
    }
}
