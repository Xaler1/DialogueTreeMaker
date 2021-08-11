package Managers;

import Nodes.*;
import Panels.NodePanel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;


public class Graph implements Serializable {

    public String name;
    private transient HashMap<Component, Integer> componentKeys = new HashMap<Component, Integer>();
    private HashMap<Integer, Node> idKeys = new HashMap<Integer, Node>();
    private int numNodes = 0;
    public Point2D zoom = new Point2D.Float(1, 1);

    public Graph(String name) {
        this.name = name;
    }

    public Graph(String name, String filePath){
        this.name = name;
        //For creating a graph loaded from a file
    }

    private void addNode(Component elementUI, Node item) {
        this.numNodes += 1;
        item.setId(this.numNodes);
        this.componentKeys.put(elementUI, this.numNodes);
        this.idKeys.put(this.numNodes, item);
    }

    public void addStartNode(Component startNodeElement) {
        addNode(startNodeElement, new StartNode());
    }

    public void addDialogueNode(Component dialogueNodeElement, String dialogueText) {
        addNode(dialogueNodeElement, new DialogueNode(dialogueText, false));
    }

    public void addChoiceNode(Component choiceNodeElement, String dialogueText) {
        addNode(choiceNodeElement, new DialogueNode(dialogueText, true));
    }

    public void addAnswerNode(Component answerNodeElement, String answerText) {
        addNode(answerNodeElement, new AnswerNode(answerText));
    }

    public void addEndNode(Component endNodeElement) {
        addNode(endNodeElement, new EndNode());
    }

    public Node getNode(Component itemElement){
        return this.idKeys.get(this.componentKeys.get(itemElement));
    }

    public void removeNode(Component node) {
        idKeys.remove(componentKeys.get(node));
        componentKeys.remove(node);
    }

    public void reInit() {
        componentKeys = new HashMap<>();
    }

    public void assignNodePanel(NodePanel node, int id) {
        componentKeys.put(node, id);
    }

    public Collection<Node> getNodes() {
        return idKeys.values();
    }

    public void writeToFile() {

    }

    public void createRelation(Component parentElement, Component childElement) {
        getNode(parentElement).addChild(getNode(childElement));
    }
}
