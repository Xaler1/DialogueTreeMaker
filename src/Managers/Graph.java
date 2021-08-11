package Managers;

import Nodes.*;
import Panels.NodePanel;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;


public class Graph implements Serializable {

    public String name;
    private transient HashMap<NodePanel, Integer> componentKeys = new HashMap<>();
    private HashMap<Integer, Node> idKeys = new HashMap<>();
    private transient HashMap<Node, NodePanel> panel_map = new HashMap<>();
    private int numNodes = 0;
    public Point2D zoom = new Point2D.Float(1, 1);

    public Graph(String name) {
        this.name = name;
    }

    public Graph(String name, String filePath){
        this.name = name;
        //For creating a graph loaded from a file - no
    }

    private void addNode(NodePanel elementUI, Node item) {
        this.numNodes += 1;
        item.setId(this.numNodes);
        this.componentKeys.put(elementUI, this.numNodes);
        this.idKeys.put(this.numNodes, item);
        panel_map.put(item, elementUI);
    }

    public void addStartNode(NodePanel startNodeElement) {
        addNode(startNodeElement, new StartNode());
    }

    public void addDialogueNode(NodePanel dialogueNodeElement, String dialogueText) {
        addNode(dialogueNodeElement, new DialogueNode(dialogueText, false));
    }

    public void addChoiceNode(NodePanel choiceNodeElement, String dialogueText) {
        addNode(choiceNodeElement, new DialogueNode(dialogueText, true));
    }

    public void addAnswerNode(NodePanel answerNodeElement, String answerText) {
        addNode(answerNodeElement, new AnswerNode(answerText));
    }

    public void addEndNode(NodePanel endNodeElement) {
        addNode(endNodeElement, new EndNode());
    }

    public Node getNode(NodePanel itemElement){
        return this.idKeys.get(this.componentKeys.get(itemElement));
    }

    public void removeNode(NodePanel node) {
        idKeys.remove(componentKeys.get(node));
        componentKeys.remove(node);
    }

    public void reInit() {
        componentKeys = new HashMap<>();
        panel_map = new HashMap<>();
    }

    public void assignNodePanel(NodePanel panel, int id) {
        componentKeys.put(panel, id);
        panel_map.put(idKeys.get(id), panel);
    }

    public NodePanel getPanel(Node node) {
        return panel_map.get(node);
    }

    public Collection<Node> getNodes() {
        return idKeys.values();
    }

    public void writeToFile() {

    }

    public void createRelation(NodePanel parentElement, NodePanel childElement) {
        getNode(parentElement).addChild(getNode(childElement));
    }
}
