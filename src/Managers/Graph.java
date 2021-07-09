package Managers;

import Managers.DialogueGraph;
import Nodes.*;

import java.awt.*;
import java.util.HashMap;

public class Graph implements DialogueGraph {
    public String name;
    private HashMap<Component, Integer> companantKeys = new HashMap<Component, Integer>();
    private HashMap<Integer, Node> idKeys = new HashMap<Integer, Node>();
    private int numNodes = 0;

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
        this.companantKeys.put(elementUI, this.numNodes);
        this.idKeys.put(this.numNodes, item);
    }

    @Override
    public void addStartNode(Component startNodeElement) {
        addNode(startNodeElement, new StartNode());
    }

    @Override
    public void addDialogueNode(Component dialogueNodeElement, String dialogueText) {
        addNode(dialogueNodeElement, new DialogueNode(dialogueText));
    }

    @Override
    public void addAnswerNode(Component answerNodeElement, String answerText) {
        addNode(answerNodeElement, new AnswerNode(answerText));
    }

    @Override
    public void addEndNode(Component endNodeElement) {
        addNode(endNodeElement, new EndNode());
    }

    public Node getNode(Component itemElement){
        return this.idKeys.get(this.companantKeys.get(itemElement));
    }

    @Override
    public void removeNode(int id) {

    }

    @Override
    public void writeToFile() {

    }
}
