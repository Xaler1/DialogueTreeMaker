package IO;

import Managers.Conditional;
import Managers.*;
import Nodes.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class SkeletonReader {

    private static ObjectInputStream reader;
    private static int version;
    private static Map<Integer, Person> person_map;
    private static Map<Integer, List<Integer>> child_map;
    private static Map<Conditional, Integer> conditional_map;

    public static Project readProject(String source) throws IOException, ClassNotFoundException {
        reader = new ObjectInputStream(new FileInputStream(source));
        conditional_map = new HashMap<>();
        person_map = new HashMap<>();
        child_map = new HashMap<>();
        Project project = new Project(reader.readUTF());
        version = reader.readInt();
        int num = reader.readInt();
        System.out.printf("Reading in %d people\n", num);
        for (int i = 0; i < num; i++) {
            Person person = readPerson();
            project.people.put(person.id, person);
        }
        num = reader.readInt();
        System.out.printf("Reading in %d variables\n", num);
        for (int i = 0; i < num; i++) {
            Variable variable = readVariable();
            project.variables.put(variable.id, variable);
        }
        num = reader.readInt();
        System.out.printf("Reading in %d graphs\n", num);
        for (int i = 0; i < num; i++) {
            Graph graph = readGraph();
            for (Node node : graph.getNodes()) {
                for (int id : child_map.get(node.getId())) {
                    node.addChild(graph.getNode(id));
                }
                for (Conditional conditional : node.getConditionals()) {
                    conditional.child = graph.getNode(conditional_map.get(conditional));
                    conditional.person = node.getPerson();
                }
            }
            project.graphs.add(graph);
        }
        return project;
    }

    private static Person readPerson() throws IOException {
        int id = reader.readInt();
        String name = reader.readUTF();
        Person person = new Person(id, name);
        person.img_name = reader.readUTF();
        if (person.img_name.equals("null")) {
            person.img_name = null;
        }
        int num = reader.readInt();
        System.out.printf("Person %s has %d properties\n", name, num);
        for (int i = 0; i < num; i++) {
            Property property = readProperty();
            person.properties.put(property.id, property);
        }
        IntStream stream =  person.properties.keySet().stream().mapToInt(new ToIntFunction<Integer>() {
            @Override
            public int applyAsInt(Integer value) { return value; }
        });
        person.property_id = stream.max().orElse(0) + 1;
        return person;
    }

    private  static Property readProperty() throws IOException{
        Property property = new Property(reader.readInt());
        property.name = reader.readUTF();
        property.type = reader.readUTF();
        property.value = reader.readUTF();
        return property;
    }

    private static Variable readVariable() throws IOException {
        int id = reader.readInt();
        String name = reader.readUTF();
        Variable variable = new Variable(name, id);
        variable.type = reader.readUTF();
        variable.default_value = reader.readUTF();
        return variable;
    }

    private static Graph readGraph() throws IOException, ClassNotFoundException {
        Graph graph = new Graph(reader.readUTF());
        graph.zoom = (Point2D) reader.readObject();
        int num = reader.readInt();
        System.out.printf("Graph %s has %d nodes\n", graph.name, num);
        for (int i = 0; i < num; i++) {
            Node node = readNode();
            graph.addNode(node.getId(), node);
        }
        return graph;
    }

    private static Node readNode() throws IOException, ClassNotFoundException {
        String type = reader.readUTF();
        int id = reader.readInt();
        child_map.put(id, new LinkedList<>());
        Point location = (Point) reader.readObject();
        int person_id = reader.readInt();
        System.out.printf("Reading %s", type);
        Node node = null;
        switch (type) {
            case "StartNode":
                node = new StartNode();
                break;
            case "EndNode":
                node = new EndNode();
                break;
            case "DialogueNode":
                String text = reader.readUTF();
                boolean bool = reader.readBoolean();
                node = new DialogueNode(text, bool);
                break;
            case "AnswerNode":
                node = new AnswerNode(reader.readUTF());

        }
        node.setId(id);
        node.location = location;
        if (person_id > -1) {
            node.setPerson(person_map.get(person_id));
        }
        int num = reader.readInt();
        System.out.printf(" with %d children", num);
        for (int i = 0; i < num; i++) {
            child_map.get(id).add(reader.readInt());
        }
        num = reader.readInt();
        System.out.printf(" and %d conditionals\n", num);
        for (int i = 0; i < num; i++) {
            Conditional conditional = readConditional();
            node.addConditional(conditional);
        }
        return node;
    }

    private static Conditional readConditional() throws IOException {
        Conditional conditional = new Conditional(null);
        conditional.var1_type = reader.readUTF();
        conditional.var1 = reader.readUTF();
        conditional.comparator = reader.readUTF();
        conditional.var2_type = reader.readUTF();
        conditional.var2 = reader.readUTF();
        conditional_map.put(conditional, reader.readInt());
        return conditional;
    }


}
