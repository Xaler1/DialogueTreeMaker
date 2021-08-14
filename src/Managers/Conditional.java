package Managers;

public class Conditional {

    private Person person;
    private boolean is_default = false;

    public Conditional(Person person) {
        this.person = person;
    }

    public Conditional(boolean is_default) {
        this.is_default = is_default;
        person = null;
    }

    public boolean is_satisfied() {
        if (is_default) return true;
        return false;
    }
}
