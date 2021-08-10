package Nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    private int id;
    private List<Node> children = new ArrayList<>();

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

    public void removeChild(Node node){
        this.children.remove(node);
    }

    public void removeChildren() {
        this.children.clear();
    }
}
