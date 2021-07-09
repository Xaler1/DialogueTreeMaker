public class DialogueNode extends Node {
    private String dialogueText;

    public DialogueNode(String dialogueText){
        this.dialogueText = dialogueText;
    }

    public String getDialogueText(){
        return this.dialogueText;
    }

    public void setDialogueText(String dialogueText){
        this.dialogueText = dialogueText;
    }
}
