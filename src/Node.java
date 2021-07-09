import java.util.List;

public class Node {
    private int id;
    private List<Node> children;

    public Node(int id){
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

    public void removeChild(int childId){
        for(Node item : this.children){
            if(item.getId() == childId){
                this.children.remove(item);
            }
        }
    }
}
