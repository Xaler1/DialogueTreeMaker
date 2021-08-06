package Helpers;

/*
    Used to store the properties of a person.
 */
public class Property {
    public String type= "string";
    public String name;
    public String value = "";
    public final int id;

    public Property(int id) {
        this.id = id;
    };
}
