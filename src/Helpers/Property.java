package Helpers;

import java.io.Serializable;

/*
    Used to store the properties of a person.
 */
public class Property implements Serializable {
    public String type= "string";
    public String name;
    public String value = "";
    public final int id;

    public Property(int id) {
        this.id = id;
    };
}
