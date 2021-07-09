package Managers;

import Nodes.*;

import java.awt.*;
import java.util.HashMap;

public class Graph{
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

    public void addStartNode(Component startNodeElement) {
        addNode(startNodeElement, new StartNode());
    }

    public void addDialogueNode(Component dialogueNodeElement, String dialogueText) {
        addNode(dialogueNodeElement, new DialogueNode(dialogueText));
    }

    public void addAnswerNode(Component answerNodeElement, String answerText) {
        addNode(answerNodeElement, new AnswerNode(answerText));
    }

    public void addEndNode(Component endNodeElement) {
        addNode(endNodeElement, new EndNode());
    }

    public Node getNode(Component itemElement){
        return this.idKeys.get(this.companantKeys.get(itemElement));
    }

    public void removeNode(int id) {

    }

    public void writeToFile() {

    }

    public void createRelation(Component parentElement, Component childElement) {
        getNode(parentElement).addChild(getNode(childElement));
    }
}
