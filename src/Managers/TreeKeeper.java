package Managers;

import Frames.MainWindow;

import java.util.Collection;

/*
    This is a manager class that is responsible for modifying the project - e.g. adding people and variables.
 */
public class TreeKeeper {

    private Project project;
    private int latest_person_id = 0;
    private int latest_variable_id = 0;

    public TreeKeeper() {
        project = new Project("Untitled project");
        MainWindow window = new MainWindow(project.graphs, "Untitled project", this);
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
}
