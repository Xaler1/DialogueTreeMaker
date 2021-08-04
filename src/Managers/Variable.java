package Managers;

public class Variable {
    public Class type = String.class;
    public String name = "";
    public String default_value = "";

    public Variable(String name) {
        this.name = name;
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
