package StateMachines;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.function.Function;

public class DialogueMachine {

    private String project_name;
    private int project_version;
    private HashMap<Integer, Person> people = new HashMap<>();
    private HashMap<String, Variable> variables = new HashMap<>();
    private Map<Integer, Answer> answers = new HashMap<>();
    private Map<Integer, Node> nodes = new HashMap<>();
    private Map<Node, List<Integer>> child_map = new HashMap<>();
    private HashMap<String, StartNode> starts = new HashMap<>();
    private List<Conditional> all_conditionals = new LinkedList<>();
    private HashMap<Conditional, Integer> conditional_children = new HashMap<>();
    private HashMap<Answer, Integer> answer_children = new HashMap<>();
    public Node current_node;

    public enum TYPE {
        START,
        DIALOGUE,
        CHOICE,
        END
    }

    public class NoSuchGraph extends Exception {
        public NoSuchGraph(String name) {
            super("A graph with the name " + name + " does not exist in this project");
        }
    }

    public class InfoState {
        public String person_name;
        public Image person_image;
        public String text;
        public String[] answers = null;

        public InfoState(Person person, String text) {
            if (person != null) {
                person_name = person.name;
                person_image = person.img;
            }
            this.text = text;
        }

        public InfoState(Person person, String text, String[] answers) {
            if (person != null) {
                person_name = person.name;
                person_image = person.img;
            }
            this.text = text;
            this.answers = answers;
        }
    }

    private class Variable {
        public String name;
        public String type;
        public String default_value;
        public Field source = null;

        public Variable(String name, String type, String default_value) {
            this.name = name;
        }

        public void setSource(Field field) {
            field.setAccessible(true);
        }
    }

    private class Person {
        public String name;
        public int id;
        public String img_name;
        public Image img;
        public Map<String, Property> properties;

        public Person(int id, String name, String img_name) {
            this.id = id;
            this.name = name;
            this.img_name = img_name;
            try {
                img = ImageIO.read(new File(img_name));
            } catch (IOException ignored) {}
            properties = new HashMap<>();
        }

        public void addProperty(String name, String type, String value) {
            Comparable default_value = null;
            switch (type) {
                case "string" -> default_value = value;
                case "int" -> default_value = Integer.valueOf(value);
                case "float" -> default_value = Float.valueOf(value);
                case "bool" -> default_value = Boolean.valueOf(value);
            }
            properties.put(name, new Property(default_value));
        }
    }

    private class Property {
        public Class type;
        public Comparable value;

        public Property(Comparable value) {
            this.value = value;
            this.type = value.getClass();
        }
    }

    private abstract class Node {
        public TYPE type;
        public Node child;
        public Person person;
        protected final List<Conditional> conditionals = new LinkedList<>();

        protected Node(TYPE type) {
            this.type = type;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public void setChild(Node child) {
            this.child = child;
        }

        public void addConditional(Conditional conditional) {
            conditionals.add(conditional);
        }

        public abstract void advance();
        public void advance(String answer) {};
    }

    private class StartNode extends Node {

        public StartNode() {
            super(TYPE.START);
        }

        @Override
        public void advance() {
            current_node = child;
        }
    }

    private class EndNode extends Node {

        public EndNode() {
            super(TYPE.END);
        }


        @Override
        public void advance() {
            current_node = null;
        }
    }

    private class DialogueNode extends Node {
        public final String text;

        public DialogueNode(String text) {
            super(TYPE.DIALOGUE);
            this.text = text;
        }


        @Override
        public void advance() {
            for (Conditional conditional : conditionals) {
                if (conditional.isSatisfied()) {
                    current_node = conditional.child;
                    return;
                }
            }
            current_node = child;
        }
    }

    private class ChoiceNode extends Node {

        private final Map<String, Answer> node_answers = new HashMap<>();
        //This exists separately from the above since it may be necessary to preserve the ordering of the answers
        //and the keyset in the map does not do that.
        private final List<String> answer_texts = new LinkedList<>();
        private final String text;

        protected ChoiceNode(String text) {
            super(TYPE.CHOICE);
            this.text = text;

        }

        public void addAnswer(Answer answer) {
            node_answers.put(answer.text, answer);
            answer_texts.add(answer.text);
        }

        public String[] getAnswerOptions() {
            String[] options = new String[node_answers.size()];
            int counter = 0;
            for (String answer : answer_texts) {
                options[counter] = answer;
                counter++;
            }
            return options;
        }

        @Override
        public void advance() {
            if (node_answers.size() == 0) {
                current_node = null;
                return;
            }
            current_node = node_answers.values().iterator().next().getChild();
        }

        @Override
        public void advance(String answer) {
            Answer selected_answer = node_answers.get(answer);
            if (selected_answer == null) {
                current_node = null;
                return;
            }
            current_node = selected_answer.getChild();
        }
    }

    private class Answer {
        private Node child;
        public String text;
        public List<Conditional> conditionals = new LinkedList<>();

        public Answer(String text) {
            this.text = text;
        }

        public void setChild(Node child) {
            this.child = child;
        }

        public void addConditional(Conditional conditional) {
            conditionals.add(conditional);
        }

        public Node getChild() {
            for (Conditional conditional : conditionals) {
                if (conditional.isSatisfied()) {
                    return conditional.child;
                }
            }
            return child;
        }
    }

    private class Conditional {
        public String var1_type;
        public String var2_type;
        public String var1;
        public String var2;
        public String comparator;

        Function<String, Object> var1_supplier;
        Function<String, Object> var2_supplier;
        String comparison_type;

        public Node child;

        public Conditional (String var1_type, String var1, String comparator, String var2_type, String var2) {
            this.var1_type = var1_type;
            this.var1 = var1;
            this.var2_type = var2_type;
            this.var2 = var2;
            this.comparator = comparator;

            switch (var1_type) {
                case "string" -> var1_supplier = (e) -> {return var1;};
                case "int" -> var1_supplier = (e) -> {return Float.parseFloat(var1);};
                case "float" -> var1_supplier = (e) -> {return Float.parseFloat(var1);};
                case "bool" -> var1_supplier = (e) -> {return Boolean.parseBoolean(var1);};
            }

            switch (var2_type) {
                case "string" -> var2_supplier = (e) -> {return var2;};
                case "int" -> var2_supplier = (e) -> {return Float.parseFloat(var2);};
                case "float" -> var2_supplier = (e) -> {return Float.parseFloat(var2);};
                case "bool" -> var2_supplier = (e) -> {return Boolean.parseBoolean(var2);};
            }


            if (var1_type.equals("string")) comparison_type = "string";
            else if (var1_type.equals("bool")) comparison_type = "bool";
            else comparison_type = "float";
        }

        public void setChild(Node child) {
            this.child = child;
        }

        public boolean isSatisfied() {
            switch (comparison_type) {
                case "string":
                    String str1 = (String) var1_supplier.apply(this.var1);
                    String str2 = (String) var2_supplier.apply(this.var2);
                    if (comparator.equals("=")) return str1.equals(str2);
                    else return !str1.equals(str2);
                case "float":
                    Float fl1 = (Float) var1_supplier.apply(this.var1);
                    Float fl2 = (Float) var2_supplier.apply(this.var2);
                    if (comparator.equals("=")) return fl1.equals(fl2);
                    else if (comparator.equals(">")) return fl1 > fl2;
                    else if (comparator.equals("<")) return fl1 < fl2;
                    else if (comparator.equals(">=")) return fl1 >= fl2;
                    else if (comparator.equals("<=")) return fl1 <= fl2;
                    else return !(fl1.equals(fl2));
                case "bool":
                    Boolean bool1 = (Boolean) var1_supplier.apply(this.var1);
                    Boolean bool2 = (Boolean) var2_supplier.apply(this.var2);
                    if (comparator.equals("=")) return bool1 == bool2;
                    else return bool1 != bool2;
            };
            return false;
        }
    }

    /**
     * Constructs the dialogue machine by loading a project from a specified file.
     * @param source - the file from which to load the dialogue graph. Should have a .tree extension.
     * @throws IOException - if the file specified cannot be found or cannot be loaded properly and io exception is thrown.
     */
    public DialogueMachine(File source) throws IOException {
        ObjectInputStream reader = new ObjectInputStream(new FileInputStream(source));
        project_name = reader.readUTF();
        project_version = reader.readInt();
        int num = reader.readInt();
        for (int i = 0; i < num; i++) {
            Person new_person = readPerson(reader);
            people.put(new_person.id, new_person);
        }
        num = reader.readInt();
        for (int i = 0; i < num; i++) {
            Variable new_variable = readVariable(reader);
            variables.put(new_variable.name, new_variable);
        }
        num = reader.readInt();
        for (int i =0; i < num; i++) {
            String graph_name = reader.readUTF();
            try {
                reader.readObject();
            } catch (ClassNotFoundException ignored) { }
            int node_num = reader.readInt();
            for (int j = 0; j < node_num; j++) {
                readNode(reader, graph_name);
            }
            for (Node node : child_map.keySet()) {
                if (node instanceof ChoiceNode) {
                    for (int child_id : child_map.get(node)) {
                        ((ChoiceNode) node).addAnswer(answers.get(child_id));
                    }
                } else {
                    if (child_map.get(node).size() > 0) node.setChild(nodes.get(child_map.get(node).get(0)));
                }
            }
            for (Answer answer : answer_children.keySet()) {
                answer.setChild(nodes.get(answer_children.get(answer)));
            }
            for (Conditional conditional : conditional_children.keySet()) {
                conditional.setChild(nodes.get(conditional_children.get(conditional)));
            }
        }
    }

    /**
     * Returns a list of graphs in the current project represented as Strings (their names).
     * @return a Collection of strings representing the names of available graphs.
     */
    public Collection<String> getGraphs() {
        return starts.keySet();
    }

    /**
     * This switches the current node to the start node of a graph with the given name.
     * @param name - the name of the graph to start.
     * @throws NoSuchGraph - if the loaded project does not contain a graph with the given name.
     */
    public void startGraph(String name) throws NoSuchGraph {
        if (!starts.containsKey(name)) throw new NoSuchGraph(name);
        current_node = starts.get(name);
    }

    public InfoState advance() {
        if (current_node == null) return null;
        current_node.advance();

        if (current_node == null) return null;
        if (current_node.type == TYPE.END) return null;
        if (current_node.type == TYPE.DIALOGUE) {
            return new InfoState(current_node.person, ((DialogueNode) current_node).text);
        } else if (current_node.type == TYPE.CHOICE) {
            return new InfoState(current_node.person, ((ChoiceNode) current_node).text, ((ChoiceNode) current_node).getAnswerOptions());
        }

        return null;
    }

    public InfoState advance(String answer) {
        try {
            current_node.advance(answer);
        } catch (Exception ex) {
            return null;
        }
        if (current_node == null) return null;
        if (current_node.type == TYPE.END) return null;
        if (current_node.type == TYPE.DIALOGUE) {
            return new InfoState(current_node.person, ((DialogueNode) current_node).text);
        } else if (current_node.type == TYPE.CHOICE) {
            return new InfoState(current_node.person, ((ChoiceNode) current_node).text, ((ChoiceNode) current_node).getAnswerOptions());
        }
        return null;
    }

    public String getProjectName() {
        return project_name;
    }

    private Person readPerson(ObjectInputStream reader) throws IOException {
        int id = reader.readInt();
        String name = reader.readUTF();
        String img_name = reader.readUTF();
        int num = reader.readInt();
        Person person = new Person(id, name, img_name);
        for (int i = 0; i < num; i++) {
            String prop_name = reader.readUTF();
            String type = reader.readUTF();
            String value = reader.readUTF();
            person.addProperty(prop_name, type, value);
        }
        return person;
    }

    private Variable readVariable(ObjectInputStream reader) throws IOException {
        reader.readInt();
        String name = reader.readUTF();
        String type = reader.readUTF();
        String val = reader.readUTF();
        return new Variable(name, type, val);
    }

    private void readNode(ObjectInputStream reader, String graph) throws IOException {
        String type = reader.readUTF();
        int id = reader.readInt();
        try {
            reader.readObject();
        } catch (ClassNotFoundException ignored) {}
        int person_id = reader.readInt();
        Person person = null;
        if (person_id > -1) {
            person = people.get(person_id);
        }

        Node node = null;
        Answer answer = null;
        switch (type) {
            case "StartNode":
                node = new StartNode();
                starts.put(graph, (StartNode) node);
                child_map.put(node, new LinkedList<>());
                nodes.put(id, node);
                break;
            case "EndNode":
                node = new EndNode();
                nodes.put(id, node);
                child_map.put(node, new LinkedList<>());
                break;
            case "DialogueNode":
                String text = reader.readUTF();
                boolean is_choice = reader.readBoolean();
                if (is_choice) {
                    node = new ChoiceNode(text);
                } else {
                    node = new DialogueNode(text);
                }
                nodes.put(id, node);
                child_map.put(node, new LinkedList<>());
                break;
            case "AnswerNode":
                answer = new Answer(reader.readUTF());
                answers.put(id, answer);
                break;
        }

        int num = reader.readInt();
        for (int i = 0; i < num; i++) {
            if (node != null) {
                child_map.get(node).add(reader.readInt());
            } else if (answer != null) {
                answer_children.put(answer, reader.readInt());
            }
        }
        num = reader.readInt();
        for (int i = 0; i < num; i++) {
            Conditional conditional = readConditional(reader);
            all_conditionals.add(conditional);
            if (node != null) {
                node.addConditional(conditional);
            } else if (answer != null) {
                answer.addConditional(conditional);
            }
        }
    }

    private Conditional readConditional(ObjectInputStream reader) throws IOException {
        String var1_type = reader.readUTF();
        String var1 = reader.readUTF();
        String comparator = reader.readUTF();
        String var2_type = reader.readUTF();
        String var2 = reader.readUTF();
        Conditional new_conditional = new Conditional(var1_type, var1, comparator, var2_type, var2);
        conditional_children.put(new_conditional, reader.readInt());
        return new_conditional;
    }

}
