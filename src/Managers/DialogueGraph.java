package Managers;

import Nodes.AnswerNode;
import Nodes.DialogueNode;
import Nodes.EndNode;
import Nodes.StartNode;

public interface DialogueGraph {
    void addStartNode(StartNode newStart);
    void addDialogueNode(DialogueNode newDialogue);
    void addAnswerNode(AnswerNode newAnswer);
    void addEndNode(EndNode newEnd);
    void removeNode(int id);
    void writeToFile();
}
