package IO;

import Managers.*;
import Nodes.AnswerNode;
import Nodes.DialogueNode;
import Nodes.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SkeletonWriter {

    private static ObjectOutputStream writer;

    public static void writeProject(Project project, String file) throws IOException {
        writer = new ObjectOutputStream(new FileOutputStream(file));
        writer.writeUTF(project.name);
        //The next line writes the version number to distinguish between versions of saves. In case
        //older versions store less information, so the read functions can take that into account.
        writer.writeInt(1);
        writer.writeInt(project.people.size());
        for (Person person : project.people.values()) {
            writePerson(person);
        }
        writer.writeInt(project.variables.size());
        for (Variable variable : project.variables.values()) {
            writeVariable(variable);
        }
        writer.writeInt(project.graphs.size());
        for (Graph graph : project.graphs) {
            writeGraph(graph);
        }
        writer.close();
    }

    private static void writePerson(Person person) throws IOException {
        writer.writeInt(person.id);
        writer.writeUTF(person.name);
        writer.writeUTF(person.img_name);
        writer.writeInt(person.properties.size());
        for (Property property : person.properties.values()) {
            writeProperty(property);
        }
    }

    private static void writeProperty(Property property) throws IOException {
        writer.writeInt(property.id);
        writer.writeUTF(property.name);
        writer.writeUTF(property.type);
        writer.writeUTF(property.value);
    }

    private static void writeVariable(Variable variable) throws IOException {
        writer.writeInt(variable.id);
        writer.writeUTF(variable.name);
        writer.writeUTF(variable.type);
        writer.writeUTF(variable.default_value);
    }

    private static void writeGraph(Graph graph) throws IOException {
        writer.writeUTF(graph.name);
        writer.writeObject(graph.zoom);
        writer.writeObject(graph.getNodes().size());
        for (Node node : graph.getNodes()) {
            writer.writeUTF(node.getClass().getSimpleName());
            writeNode(node);
        }
    }

    private static void writeNode(Node node) throws IOException {
        writer.writeInt(node.getId());
        writer.writeObject(node.location);
        if (node.getPerson() != null) {
            writer.writeInt(node.getPerson().id);
        } else {
            writer.writeInt(-1);
        }
        if (node instanceof DialogueNode) {
            writer.writeUTF(((DialogueNode) node).getDialogueText());
            writer.writeBoolean(((DialogueNode) node).isChoice);
        } else if (node instanceof AnswerNode) {
            writer.writeUTF(((AnswerNode) node).getAnswerText());
        }
        writer.writeInt(node.getChildren().size());
        for (Node child : node.getChildren()) {
            writer.writeInt(child.getId());
        }
        writer.writeInt(node.getConditionals().size());
        for (Conditional conditional : node.getConditionals()) {
            writeConditional(conditional);
        }
    }

    private static void writeConditional(Conditional conditional) throws IOException {
        writer.writeUTF(conditional.var1_type);
        writer.writeUTF(conditional.var1);
        writer.writeUTF(conditional.comparator);
        writer.writeUTF(conditional.var2_type);
        writer.writeUTF(conditional.var2);
        writer.writeInt(conditional.child.getId());
    }
}
