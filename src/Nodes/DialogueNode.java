package Nodes;

import Managers.Person;

public class DialogueNode extends Node {
    private String dialogueText;
    private Person person = null;
    public final boolean isChoice;

    public DialogueNode(String dialogueText, boolean isChoice){
        this.dialogueText = dialogueText;
        this.isChoice = isChoice;
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

    public Person getPerson() {
        return person;
    }

    public void removeCharacter() {
        this.person = null;
    }
}
