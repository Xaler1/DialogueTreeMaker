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
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public Object variables_source = null;

    public Logger logger;

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

    public class WrongSourceType extends Exception {
        public WrongSourceType(String name, String target_type, String source_type) {
            super("The variable '" + name + "' was found in the source class, however in the graph it is of type " + target_type + " and in the source class it is of type " + source_type);
        }
    }

    /**
     * Provides all the basic info about the current state in one object.
     */
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

    /**
     * Represents a single variable in the project. Stores the name, type, default value, as well as the source field from
     * which the supplier function can take values.
     */
    private class Variable {
        public String name;
        public String type;
        public Object default_value;
        public Field source = null;
        public Supplier<Object> default_supplier;

        public Variable(String name, String type, String default_value) {
            this.name = name;
            this.type = type;
            switch (type) {
                case "string" -> this.default_value = default_value;
                case "int" -> this.default_value = Integer.parseInt(default_value);
                case "float" -> {
                    this.default_value = Float.parseFloat(default_value);
                }
                case "bool" -> this.default_value = Boolean.parseBoolean(default_value);
            }
            default_supplier = () -> {
                if (source == null) return this.default_value;
                try {
                    return source.get(variables_source);
                } catch (IllegalAccessException ex) {
                    return this.default_value;
                }
            };
        }

        /**
         * Sets the source field for this variable to be used by the supplier function.
         * @param field
         */
        public void setSource(Field field) {
            field.setAccessible(true);
            this.source = field;
        }
    }

    /**
     * Represents a single person in the project. Stores the name, the id, and the name of the image. Upon construction
     * also attempts to load the image from the working directory. Also stores a list of properties. Can also store the
     * source object from the fields of which the properties can take value.
     */
    private class Person {
        public String name;
        public int id;
        public String img_name;
        public Image img;
        public Map<String, Property> properties = new HashMap<>();
        private Object source;

        public Person(int id, String name, String img_name) {
            this.id = id;
            this.name = name;
            this.img_name = img_name;
            try {
                img = ImageIO.read(new File(img_name));
            } catch (IOException ignored) {}
        }

        /**
         * Adds a new property to this person.
         * @param name the name of the property.
         * @param type the type of the property.
         * @param value the default value of the property.
         */
        public void addProperty(String name, String type, String value) {
            Object default_value = null;
            switch (type) {
                case "string" -> default_value = value;
                case "int" -> default_value = Integer.valueOf(value);
                case "float" -> default_value = Float.valueOf(value);
                case "bool" -> default_value = Boolean.valueOf(value);
            }
            properties.put(name, new Property(name, type, default_value));
        }

        /**
         * Generates a new supplier for the property with the given name.
         * @param name the name of the property for which to generate the supplier.
         * @return the supplier function which will provide either the default value of the property or take the value
         * from the corresponding field of the given source object.
         */
        public Supplier<Object> generateSupplier (String name) {
            return () -> {
                Property property = properties.get(name);
                if (property.source == null) return property.default_value;
                try {
                    return property.source.get(source);
                } catch (IllegalAccessException ex) {
                    return property.default_value;
                }
            };
        }

        /**
         * Sets the source object. Its fields will be used to get the values of properties which have the same name as the field.
         * @param source the source object which should have fields with names equal to the names of the properties of this person.
         * @throws WrongSourceType in case the source object contains a field with the same name as some property of this
         * person, but the type of the field does not match the type of the property.
         */
        public void setPropertiesSource(Object source) throws WrongSourceType {
            this.source = source;
            for (Property property : properties.values()) {
                try {
                    String source_type = source.getClass().getField(property.name).getType().getSimpleName();
                    source_type = source_type.toLowerCase().replace("ean", "").replace("eger", "");
                    if (!source_type.equals(property.type)) throw new WrongSourceType(property.name, property.type, source_type);
                    property.source = source.getClass().getField(property.name);
                } catch (NoSuchFieldException e) {
                    logger.log(Level.WARNING, "Could not find field for variable '" + property.name + "'");
                }
            }
        }
    }

    /**
     * Represents a property of a person. Stores the name, type, default value and the source field from which to take the
     * value.
     */
    private class Property {
        public String name;
        public Field source = null;
        public String type;
        public Object default_value;

        public Property (String name, String type, Object default_value) {
            this.name = name;
             this.type = type;
             this.default_value = default_value;
        }

        /**
         * Sets the source field for this property.
         * @param field the source field from which the value of this property will be taken.
         */
        public void setSource(Field field) {
            field.setAccessible(true);
            source = field;
        }
    }

    /**
     * This is the base class of all the nodes. Stores features common to all nodes - the type, the child, the person
     * (the person is not common to all nodes, but to 2 and makes it more convenient). Also stores the list of conditionals
     * attached to this node. (Also only common to two of the nodes, but makes it more convenient with less casting and
     * instanceof comparisons required when advancing.
     */
    private abstract class Node {
        public TYPE type;
        public Node child;
        public Person person;
        protected final List<Conditional> conditionals = new LinkedList<>();

        protected Node(TYPE type) {
            this.type = type;
        }

        /**
         * Sets the person for this node.
         * @param person the person of this node.
         */
        public void setPerson(Person person) {
            this.person = person;
        }

        /**
         * Sets the child of this node i.e. the default node to go to when advancing.
         * @param child the child of this node.
         */
        public void setChild(Node child) {
            this.child = child;
        }

        /**
         * Adds a conditional to this node.
         * @param conditional a conditional of this node.
         */
        public void addConditional(Conditional conditional) {
            conditionals.add(conditional);
        }

        //These are the two advancing functions, the standard one is mandatory, but the one taking in an answer is optional, since
        // only the dialogue node needs to implement it.
        public abstract void advance();
        public void advance(String answer) {
            advance();
        };
    }

    /**
     * Represents a start node of the graph. Very basic only stores a child.
     */
    private class StartNode extends Node {

        public StartNode() {
            super(TYPE.START);
        }

        /**
         * Simply sets the current node to this node's child.
         */
        @Override
        public void advance() {
            current_node = child;
        }
    }

    /**
     * Represents a end node of the graph. Also very basic, does not even have a child.
     */
    private class EndNode extends Node {

        public EndNode() {
            super(TYPE.END);
        }

        /**
         * Does nothing as this node represents the end of a graph.
         */
        @Override
        public void advance() {}
    }

    /**
     * Represents a dialogue node in the graph. Stores the text and can also have conditionals.
     */
    private class DialogueNode extends Node {
        public final String text;

        public DialogueNode(String text) {
            super(TYPE.DIALOGUE);
            this.text = text;
        }


        /**
         * Checks whether any of the conditionals of this node are satisfied. If there is one then it sets the current
         * node to the child of the first satisfied conditional. If no conditionals are satisfied (or there are none) the
         * it sets the current node to this node's default child.
         */
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

    /**
     * Represents a choice node. Stores the text and also a list of answers.
     */
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

        /**
         * Adds a new answer to this node.
         * @param answer the new answer to be added.
         */
        public void addAnswer(Answer answer) {
            node_answers.put(answer.text, answer);
            answer_texts.add(answer.text);
        }

        /**
         * Returns an array of strings that are the texts tof the answers.
         * @return a String[] of answer texts.
         */
        public String[] getAnswerOptions() {
            String[] options = new String[node_answers.size()];
            int counter = 0;
            for (String answer : answer_texts) {
                options[counter] = answer;
                counter++;
            }
            return options;
        }

        /**
         * Sets the current node to the child of the first answer in the list (or the child of one of its conditionals), since no text was provided.
         */
        @Override
        public void advance() {
            if (node_answers.size() == 0) {
                current_node = null;
                return;
            }
            current_node = node_answers.get(answer_texts.get(0)).getChild();
        }

        /**
         * Sets the current node to the child of the answer with the text provided (or the child of one of its conditionals).
         * @param answer
         */
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

    /**
     * Represents an answer in a choice node. Stores the child and the text, as well as a list of conditionals.
     * Does not extend Node since inside the state machine it is more convenient to think of it as just a container or info
     * and a direct part of the choice node, rather than a separate node.
     */
    private class Answer {
        private Node child;
        public String text;
        public List<Conditional> conditionals = new LinkedList<>();

        public Answer(String text) {
            this.text = text;
        }

        /**
         * Sets the child of this answer.
         * @param child the child of the answer.
         */
        public void setChild(Node child) {
            this.child = child;
        }

        /**
         * Adds a conditional to this answer.
         * @param conditional the new conditional
         */
        public void addConditional(Conditional conditional) {
            conditionals.add(conditional);
        }

        /**
         * Does the same process with conditionals as the dialogue node - checking if any are satisfied and returning the
         * child of the satisfied conditional or returning the default child.
         * @return
         */
        public Node getChild() {
            for (Conditional conditional : conditionals) {
                if (conditional.isSatisfied()) {
                    return conditional.child;
                }
            }
            return child;
        }
    }

    /**
     * Represents a conditional statement. Stores the two variables and their types, as well as the comparator.
     * Also stores the two final suppliers of the variables' values.
     * For speed, simplicity and avoidance of an even bigger mess of nested switches and/or if statements (occurring from
     * the fact that variables can be of 4 different classes and come from 4 different sources.) The values inside the comparison
     * are all taken from a supplier for each of the two variables. This means that the comparison does not case whether
     * variable value comes from a constant or from a variable or from a person's property. That is determined at initialization.
     * The comparison itself only switches on the comparison type. This still means that there are two separate switche,
     * one for the variable source and one for the variable type, however it means that the switches are not nested and there
     * is no code duplication. It also just looks neater. The use of supplier functions also means that it can determine easier
     * at runtime whether there is a field provided from which the variable or the person's property can take the value, or
     * if it should just give it the default.
     */
    private class Conditional {
        public String var1_type;
        public String var2_type;
        public String var1;
        public String var2;
        public String comparator;

        Supplier<Object> var1_supplier;
        Supplier<Object> var2_supplier;
        String comparison_type;

        public Node child;

        public Conditional (String var1_type, String var1, String comparator, String var2_type, String var2, Person person) {
            this.var1_type = var1_type;
            this.var1 = var1;
            this.var2_type = var2_type;
            this.var2 = var2;
            this.comparator = comparator;

            switch (var1_type) {
                case "string" -> var1_supplier = () -> var1;
                case "int" -> var1_supplier = () -> Float.parseFloat(var1);
                case "float" -> var1_supplier = () -> Float.parseFloat(var1);
                case "bool" -> var1_supplier = () -> Boolean.parseBoolean(var1);
                case "Variable" -> {
                    Variable source = variables.get(var1);
                    var1_type = source.type;
                    var1_supplier = source.default_supplier;
                }
                case "Person Property" -> {
                    var1_type = person.properties.get(var1).type;
                    var1_supplier = person.generateSupplier(var1);
                }
            }

            switch (var2_type) {
                case "string" -> var2_supplier = () -> var2;
                case "int" -> var2_supplier = () -> Float.parseFloat(var2);
                case "float" -> var2_supplier = () -> Float.parseFloat(var2);
                case "bool" -> var2_supplier = () -> Boolean.parseBoolean(var2);
                case "Variable" -> {
                    Variable source = variables.get(var2);
                    var2_type = source.type;
                    var2_supplier = source.default_supplier;
                }
                case "Person Property" -> {
                    var2_type = person.properties.get(var2).type;
                    var2_supplier = person.generateSupplier(var2);
                }
            }


            if (var1_type.equals("string")) comparison_type = "string";
            else if (var1_type.equals("bool")) comparison_type = "bool";
            else comparison_type = "float";
        }

        public void setChild(Node child) {
            this.child = child;
        }

        /**
         * Performs the set comparison between the two values supplied by the supplier functions.
         * For practical purposes integers are treated as floats.
         * @return whether the conditional is satisfied with the present values.
         */
        public boolean isSatisfied() {
            switch (comparison_type) {
                case "string":
                    String str1 = (String) var1_supplier.get();
                    String str2 = (String) var2_supplier.get();
                    if (comparator.equals("=")) return str1.equals(str2);
                    else return !str1.equals(str2);
                case "float":
                    Float fl1 = (Float) var1_supplier.get();
                    Float fl2 = (Float) var2_supplier.get();
                    if (comparator.equals("=")) return fl1.equals(fl2);
                    else if (comparator.equals(">")) return fl1 > fl2;
                    else if (comparator.equals("<")) return fl1 < fl2;
                    else if (comparator.equals(">=")) return fl1 >= fl2;
                    else if (comparator.equals("<=")) return fl1 <= fl2;
                    else return !(fl1.equals(fl2));
                case "bool":
                    Boolean bool1 = (Boolean) var1_supplier.get();
                    Boolean bool2 = (Boolean) var2_supplier.get();
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
        logger = Logger.getLogger("StateMachineLog");
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
     * Returns a list of people objects in the current project.
     * @return a Collection of Person objects present in the current project.
     */
    public Collection<Person> getPeople() {
        return people.values();
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

    /**
     * Sets the source object for all the variables' values. The object needs to have fields that have the same name as the
     * variables in the project. Each variable will take its value (during the evaluation of a conditional) directly
     * from the field with the same name as the variable.
     * Be warned: this will make these fields accessible
     * @param source the object whose fields are to be used as a source for values of variables.
     * @throws WrongSourceType if there is a field that has the same name as a variable in this project, but the type of
     * the field is different from that of the variable.
     */
    public void setVariablesSource(Object source) throws WrongSourceType {
        variables_source = source;
        for (Variable variable : variables.values()) {
            try {
                String source_type = source.getClass().getField(variable.name).getType().getSimpleName();
                source_type = source_type.toLowerCase().replace("ean", "").replace("eger", "");
                if (!source_type.equals(variable.type)) throw new WrongSourceType(variable.name, variable.type, source_type);
                variable.setSource(source.getClass().getField(variable.name));
            } catch (NoSuchFieldException e) {
                logger.log(Level.WARNING, "Could not find field for variable '" + variable.name + "'");
            }
        }
    }

    /**
     * Sets the given object as the source for all the person's (with the given name) properties' values. The object needs
     * to have fields with the same name as the person's property names for the fields to be used as the source. Each property
     * will directly take its value (during the evaluation of a conditional) from a field in this object with the same name
     * as the property.
     * @param name the name of the person for which to set the object as the source.
     * @param source the object whose fields are to be used as a source.
     * @return true if a person with this name was found. false if not.
     * @throws WrongSourceType if the object contains a field with the same name as one of the person's properties, but the type
     * of the field is different to that of the person's property.
     */
    public boolean setPersonSource(String name, Object source) throws WrongSourceType {
        for (Person person : people.values()) {
            if (person.name.equals(name)) {
                person.setPropertiesSource(source);
                return true;
            }
        }
        return false;
    }

    /**
     * Tells the current node to advance and then generates a new InfoState if the new node is not the end node and is not null.
     * @return the newly generated InfoState.
     */
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

    /**
     * Tells the current node to advance while selecting an answer. At present only used for choice nodes. After advancing
     * generates a new InfoState.
     * @param answer the answer to use (for the choice node)
     * @return the newly generated InfoState.
     */
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

    /**
     * Returns the text of the current node, or null if the node cannot have text.
     * @return the text of the current node.
     */
    public String getText() {
        if (current_node == null) return null;
        if (current_node.type == TYPE.DIALOGUE) return ((DialogueNode) current_node).text;
        if (current_node.type == TYPE.CHOICE) return ((ChoiceNode) current_node).text;
        return null;
    }

    /**
     * Returns the name of the current project.
     * @return the name of the current project.
     */
    public String getProjectName() {
        return project_name;
    }

    /**
     * Reads a person from the input stream given. Reades the id, the name and the image name. Next it also reads all the
     * properties of the person and adds them to the person.
     * @param reader the ObjectInputStream from which to read a person.
     * @return a new Person object with the info read.
     * @throws IOException if there is a problem with reading.
     */
    private Person readPerson(ObjectInputStream reader) throws IOException {
        int id = reader.readInt();
        String name = reader.readUTF();
        String img_name = reader.readUTF();
        int num = reader.readInt();
        Person person = new Person(id, name, img_name);
        for (int i = 0; i < num; i++) {
            reader.readInt();
            String prop_name = reader.readUTF();
            String type = reader.readUTF();
            String value = reader.readUTF();
            person.addProperty(prop_name, type, value);
        }
        return person;
    }

    /**
     * Reads a variable from an input stream. Reads the name, type and the default value.
     * @param reader the ObjectInputStream from which to read the variable.
     * @return a new Variable object with the info read.
     * @throws IOException if there is a problem reading.
     */
    private Variable readVariable(ObjectInputStream reader) throws IOException {
        reader.readInt();
        String name = reader.readUTF();
        String type = reader.readUTF();
        String val = reader.readUTF();
        return new Variable(name, type, val);
    }

    /**
     * Reads a new node from an input stream. Reads all the info associated with a node. Assignes a child if it has one,
     * assigns a person if it has one and adds all the answers/conditionals that the node might have.
     * @param reader the ObjectInputStream from which to read the person.
     * @param graph the name of the graph to which this node belongs. This is needed for reconstructing the graph. Since
     * a node might be read before its child, they are first all stored is a map with their ids, but since ids can repeat
     * between graphs, there is also a map between the graph name and the list of ids.
     * @throws IOException if there is a problem reading.
     */
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
            Conditional conditional = readConditional(reader, person);
            all_conditionals.add(conditional);
            if (node != null) {
                node.addConditional(conditional);
            } else if (answer != null) {
                answer.addConditional(conditional);
            }
        }
    }

    /**
     * Reads a conditional from the input stream.
     * @param reader the ObjectInputStream from which to read the conditional.
     * @param person the person to which this conditional belongs, since the conditional needs to know the person to
     * which it belongs at construction time.
     * @return a new Conditional with all the info read.
     * @throws IOException if there is a problem reading.
     */
    private Conditional readConditional(ObjectInputStream reader, Person person) throws IOException {
        String var1_type = reader.readUTF();
        String var1 = reader.readUTF();
        String comparator = reader.readUTF();
        String var2_type = reader.readUTF();
        String var2 = reader.readUTF();
        Conditional new_conditional = new Conditional(var1_type, var1, comparator, var2_type, var2, person);
        conditional_children.put(new_conditional, reader.readInt());
        return new_conditional;
    }

}
