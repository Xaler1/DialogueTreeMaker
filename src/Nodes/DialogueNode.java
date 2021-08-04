package Nodes;

import Managers.Person;

public class DialogueNode extends Node {
    private String dialogueText;
    private Person person = null;

    public DialogueNode(String dialogueText){
        this.dialogueText = dialogueText;
    }

    public String getDialogueText(){
        return this.dialogueText;
    }

    public void setDialogueText(String dialogueText){
        this.dialogueText = dialogueText;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void removeCharacter() {
        this.person = null;
    }
}
