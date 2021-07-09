import java.util.HashMap;

public class Graph implements DialogueGraph {
    public String name;
    private HashMap<Integer, Node> keys = new HashMap<Integer, Node>();
    private int numNodes = 0;


    public Graph(String name) {
        this.name = name;
    }

    public Graph(String name, String filePath){
        this.name = name;
        //For creating a graph loaded from a file
    }

    @Override
    public void addStartNode(StartNode newStart) {

    }

    @Override
    public void addDialogueNode(DialogueNode newDialogue) {

    }

    @Override
    public void addAnswerNode(AnswerNode newAnswer) {

    }

    @Override
    public void addEndNode(EndNode newEnd) {

    }

    @Override
    public void removeNode(int id) {

    }

    @Override
    public void writeToFile() {

    }
}
