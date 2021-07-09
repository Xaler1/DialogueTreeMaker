public interface DialogueGraph {
    void addStartNode(StartNode newStart);
    void addDialogueNode(DialogueNode newDialogue);
    void addAnswerNode(AnswerNode newAnswer);
    void addEndNode(EndNode newEnd);
    Node getNode(int id);
}
