package IO;

import Managers.*;
import Nodes.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XMLWriter {

    private static Document doc;
    private static boolean spaces;

    public static void writeProject(Project project, File destination, boolean readable) throws ParserConfigurationException, TransformerException {
        getProjectXML(project, readable);
        writeToFile(destination);
    }

    public static Document getProjectXML(Project project, boolean readable) throws ParserConfigurationException {
        spaces = readable;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.newDocument();
        Element root = doc.createElement("project");
        doc.appendChild(root);
        root.setAttribute("name", project.name);
        Element person_parent = doc.createElement("people");
        for (Person person : project.people.values()) {
            if (spaces) person_parent.appendChild(doc.createTextNode("\n\t\t"));
            person_parent.appendChild(getPerson(person));
        }
        if (spaces && project.people.size() > 0) person_parent.appendChild(doc.createTextNode("\n\t"));
        if (spaces) root.appendChild(doc.createTextNode("\n\t"));
        root.appendChild(person_parent);
        Element variable_parent = doc.createElement("variables");
        for (Variable variable : project.variables.values()) {
            if (spaces) variable_parent.appendChild(doc.createTextNode("\n\t\t"));
            variable_parent.appendChild(getVariable(variable));
        }
        if (spaces && project.variables.size() > 0) variable_parent.appendChild(doc.createTextNode("\n\t"));
        if (spaces) root.appendChild(doc.createTextNode("\n\t"));
        root.appendChild(variable_parent);
        Element graph_parent = doc.createElement("graphs");
        for (Graph graph : project.graphs) {
            if (spaces) graph_parent.appendChild(doc.createTextNode("\n\t\t"));
            graph_parent.appendChild(getGraph(graph));
        }
        if (spaces && project.graphs.size() > 0) graph_parent.appendChild(doc.createTextNode("\n\t"));
        if (spaces) root.appendChild(doc.createTextNode("\n\t"));
        root.appendChild(graph_parent);
        if (spaces) root.appendChild(doc.createTextNode("\n"));
        return doc;
    }

    private static void writeToFile(File destination) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult stream = new StreamResult(destination);
        transformer.transform(source, stream);
    }

    private static Element getPerson(Person person) {
        Element person_elem = doc.createElement("person");
        person_elem.setAttribute("id", String.valueOf(person.id));
        person_elem.setAttribute("name", person.name);
        person_elem.setAttribute("img_name", person.img_name);
        Element properties = doc.createElement("properties");
        for (Property property : person.properties.values()) {
            if (spaces) properties.appendChild(doc.createTextNode("\n\t\t\t\t"));
            properties.appendChild(getProperty(property));
        }
        if (spaces && person.properties.size() > 0) properties.appendChild(doc.createTextNode("\n\t\t\t"));
        if (spaces) person_elem.appendChild(doc.createTextNode("\n\t\t\t"));
        person_elem.appendChild(properties);
        if (spaces) person_elem.appendChild(doc.createTextNode("\n\t\t"));
        return person_elem;
    }

    private static Element getProperty(Property property) {
        Element property_elem = doc.createElement("property");
        property_elem.setAttribute("id", String.valueOf(property.id));
        property_elem.setAttribute("name", property.name);
        property_elem.setAttribute("type", property.type);
        property_elem.setAttribute("value", property.value);
        return property_elem;
    }

    private static Element getVariable(Variable variable) {
        Element variable_elem = doc.createElement("variable");
        variable_elem.setAttribute("id", String.valueOf(variable.id));
        variable_elem.setAttribute("name", variable.name);
        variable_elem.setAttribute("type", variable.type);
        variable_elem.setAttribute("default_value", variable.default_value);
        return variable_elem;
    }

    private static Element getGraph(Graph graph) {
        Element graph_elem = doc.createElement("graph");
        graph_elem.setAttribute("name", graph.name);
        Element node_parent = doc.createElement("nodes");
        for (Node node : graph.getNodes()) {
            if (spaces) node_parent.appendChild(doc.createTextNode("\n\t\t\t\t"));
            node_parent.appendChild(getNode(node));
        }
        if (spaces && graph.getNodes().size() > 0) node_parent.appendChild(doc.createTextNode("\n\t\t\t"));
        if (spaces) graph_elem.appendChild(doc.createTextNode("\n\t\t\t"));
        graph_elem.appendChild(node_parent);
        if (spaces) graph_elem.appendChild(doc.createTextNode("\n\t\t"));
        return graph_elem;
    }

    private static Element getNode(Node node) {
        Element node_elem = doc.createElement(node.getClass().getSimpleName());
        node_elem.setAttribute("id", String.valueOf(node.getId()));
        int person_id = -1;
        if (node.getPerson() != null) {
            person_id = node.getPerson().id;
        }
        node_elem.setAttribute("person_id", String.valueOf(person_id));
        Element children_parent = doc.createElement("children");
        for (Node child : node.getChildren()) {
            if (spaces) children_parent.appendChild(doc.createTextNode("\n\t\t\t\t\t\t"));
            Element child_elem = doc.createElement(child.getClass().getSimpleName());
            child_elem.setAttribute("id", String.valueOf(child.getId()));
            children_parent.appendChild(child_elem);
        }
        if (spaces && node.getChildren().size() > 0) children_parent.appendChild(doc.createTextNode("\n\t\t\t\t\t"));
        if (spaces) node_elem.appendChild(doc.createTextNode("\n\t\t\t\t\t"));
        node_elem.appendChild(children_parent);
        Element conditional_parent = doc.createElement("conditionals");
        for (Conditional conditional : node.getConditionals()) {
            if (spaces) conditional_parent.appendChild(doc.createTextNode("\n\t\t\t\t\t\t"));
            conditional_parent.appendChild(getConditional(conditional));
        }
        if (spaces && node.getConditionals().size() > 0) conditional_parent.appendChild(doc.createTextNode("\n\t\t\t\t\t"));
        if (spaces) node_elem.appendChild(doc.createTextNode("\n\t\t\t\t\t"));
        node_elem.appendChild(conditional_parent);
        if (spaces) node_elem.appendChild(doc.createTextNode("\n\t\t\t\t"));
        return node_elem;
    }

    private static Element getConditional(Conditional conditional) {
        Element conditional_elem = doc.createElement("conditional");
        conditional_elem.setAttribute("var1_type", conditional.var1_type);
        conditional_elem.setAttribute("var1", conditional.var1);
        conditional_elem.setAttribute("comparator", conditional.comparator);
        conditional_elem.setAttribute("var2_type", conditional.var2_type);
        conditional_elem.setAttribute("var2", conditional.var2);
        int child = (conditional.child == null) ? -1 : conditional.child.getId();
        conditional_elem.setAttribute("child", String.valueOf(child));
        return conditional_elem;
    }
}
