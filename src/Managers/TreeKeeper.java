package Managers;

import Frames.Canvas;
import Frames.MainWindow;

import javax.swing.*;
import java.io.*;
import java.util.Collection;

/*
    This is a manager class that is responsible for modifying the project - e.g. adding people and variables.
 */
public class TreeKeeper {

    private Project project;
    private int latest_person_id = 0;
    private int latest_variable_id = 0;

    private MainWindow window;

    public TreeKeeper() {
        project = new Project("Untitled project");
        window = new MainWindow(project.graphs, "Untitled project", this);
    }

    public void saveProject(File destination) {
        if (!destination.getName().matches(".+(.tree)$")) {
            destination = new File(destination + ".tree");
        }
        try {
            destination.createNewFile();
            ObjectOutputStream object_writer = new ObjectOutputStream(new FileOutputStream(destination));
            for (Canvas canvas : window.canvases) {
                canvas.writeLayout();
            }
            object_writer.writeObject(project);
            object_writer.close();
            project.name = destination.getName().replace(".tree", "");
            window.setTitle(project.name);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error accessing file: " + ex.getClass().getSimpleName(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error writing to file: " + ex.getClass().getSimpleName(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadProject(File source) {
        try {
            String name = source.getName().replace(".tree", "");
            ObjectInputStream object_reader = new ObjectInputStream(new FileInputStream(source));
            project = (Project) object_reader.readObject();
            for (Graph graph : project.graphs) {
                graph.reInit();
            }
            window.dispose();
            window = new MainWindow(project.graphs, name, this);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error accessing file: " + ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error reading from file", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error reading from file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int addCharacter(String name) {
        for (Person person : project.people.values()) {
            if (person.name.equals(name)) {
                return -1;
            }
        }
        project.people.put(latest_person_id, new Person(latest_person_id, name));
        latest_person_id++;
        return latest_person_id - 1;
    }

    public boolean isPersonNameValid(String name) {
        for (Person person : project.people.values()) {
            if (person.name.equals(name)) return false;
        }
        return true;
    }

    public void removeCharacter(int id) {
        project.people.remove(id);
    }

    public int getNumCharacters() {
        return project.people.size();
    }

    public String getCharacterName(int id) {
        try {
            return project.people.get(id).name;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public Person getPerson(int id) {
        return project.people.get(id);
    }

    public Person getPersonByName(String name) {
        for (Person person : project.people.values()) {
            if (person.name.equals(name)) return person;
        }
        return null;
    }

    public Collection<Person> getPeople() {
        return project.people.values();
    }

    public Variable addVariable(String name) {
        if (!isVariableNameValid(name)) return null;
        Variable new_variable = new Variable(name, latest_variable_id);
        project.variables.put(latest_variable_id, new_variable);
        latest_variable_id++;
        return new_variable;
    }

    public boolean isVariableNameValid(String name) {
        for (Variable variable : project.variables.values()) {
            if (variable.name.equals(name)) {
                return false;
            }
        }
        return true;
    }

    public void removeVariable(int id) {
        project.variables.remove(id);
    }

    public Collection<Variable> getVariables() {
        return project.variables.values();
    }

    public Variable getVariableByName(String name) {
        for (Variable variable : project.variables.values()) {
            if (variable.name.equals(name)) {
                return variable;
            }
        }
        return null;
    }
}
