package Managers;

import Frames.MainWindow;

public class TreeKeeper {

    private Project project;
    private int latest_id = 0;

    public TreeKeeper() {
        project = new Project("Untitled project");
        MainWindow window = new MainWindow(project.graphs, "Untitled project", this);
    }

    public int addCharacter(String name) {
        for (Character character : project.people.values()) {
            if (character.name.equals(name)) {
                return -1;
            }
        }
        project.people.put(latest_id, new Character(latest_id, name));
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

    public Character getCharacter(int id) {
        return project.people.get(id);
    }
}
