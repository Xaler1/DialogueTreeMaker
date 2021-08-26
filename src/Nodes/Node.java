package Nodes;

import Managers.Conditional;
import Managers.Person;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Node implements Serializable {
    private int id;
    private List<Node> children = new ArrayList<>();
    protected Person person = null;
    protected List<Conditional> conditionals = new LinkedList<>();
    public Point location;

    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPerson(Person person) {
        this.person = person;
        for (Conditional conditional : conditionals) {
            conditional.person = person;
        }
    }

    public Person getPerson() {
        return person;
    }

    public List<Node> getChildren(){
        return this.children;
    }

    public void addChild(Node newChild){
        this.children.add(newChild);
    }

    public Conditional addConditional() {
        Conditional conditional = new Conditional(person);
        conditionals.add(conditional);
        return conditional;
    }

    public void addConditional(Conditional conditional) {
        conditionals.add(conditional);
    }

    public void removeConditional(Conditional conditional) {
        conditionals.remove(conditional);
    }

    public List<Conditional> getConditionals() {
        return conditionals;
    }

    public void removeChild(Node node) {
        this.children.remove(node);
    }

    public void removeChildren() {
        this.children.clear();
    }
}
