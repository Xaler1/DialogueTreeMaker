package Managers;

import Nodes.Node;

import java.io.Serializable;

public class Conditional implements Serializable {

    private Person person;
    private boolean is_default = false;
    public String var1_type = "int";
    public String var1 = "1";
    public String comparator = "=";
    public String var2_type = "int";
    public String var2 = "1";

    public Node child;

    public Conditional(Person person) {
        this.person = person;
    }

    public Conditional(boolean is_default) {
        this.is_default = is_default;
        person = null;
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
}
