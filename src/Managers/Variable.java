package Managers;

import java.io.Serializable;

/*
    This class is for storing information about variables that can be used in conditional statements.
 */
public class Variable implements Serializable {
    public String type = "string";
    public String name = "";
    public String default_value = "";
    public final int id;

    public Variable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
