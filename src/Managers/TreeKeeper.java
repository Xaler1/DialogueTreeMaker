package Managers;

import Frames.MainWindow;

import java.util.Collection;

/*
    This is a manager class that is responsible for modifying the project - e.g. adding people and variables.
 */
public class TreeKeeper {

    private Project project;
    private int latest_id = 0;

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
        project.people.put(latest_id, new Person(latest_id, name));
        latest_id++;
        return latest_id - 1;
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
}
