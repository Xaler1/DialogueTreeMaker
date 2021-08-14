package Nodes;

import Managers.Conditional;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Node implements Serializable {
    private int id;
    private List<Node> children = new ArrayList<>();
    private LinkedHashMap<Conditional, Node> conditional_children = new LinkedHashMap<>();
    public Point location;

    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Node> getChildren(){
        return this.children;
    }

    public void addChild(Node newChild){
        this.children.add(newChild);
    }

    public Conditional addConditional() {
        Conditional conditional = new Conditional(false);
        conditional_children.put(conditional, null);
        return conditional;
    }

    public void setConditionalChild(Conditional conditional, Node newChild) {
        conditional_children.replace(conditional, newChild);
    }

    public void removeChild(Node node){
        this.children.remove(node);
    }

    public void removeChildren() {
        this.children.clear();
    }
}
