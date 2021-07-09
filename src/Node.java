import java.util.List;

public class Node {
    private List<Node> children;

    public List<Node> getChildren(){
        return this.children;
    }

    public void addChild(Node newChild){
        this.children.add(newChild);
    }
}
