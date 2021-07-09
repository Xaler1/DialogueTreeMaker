package Managers;

import Nodes.AnswerNode;
import Nodes.DialogueNode;
import Nodes.EndNode;
import Nodes.StartNode;

import java.awt.*;

public interface DialogueGraph {
    void addStartNode(Component startNodeElement);
    void addDialogueNode(Component dialogueNodeElement, String dialogueText);
    void addAnswerNode(Component answerNodeElement, String answerText);
    void addEndNode(Component endNodeElement);
    void removeNode(int id);
    void writeToFile();
}
