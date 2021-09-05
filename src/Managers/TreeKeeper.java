package Managers;

import Frames.Canvas;
import Frames.LoadInfoFrame;
import Frames.MainWindow;
import IO.JSONWriter;
import IO.SkeletonReader;
import IO.SkeletonWriter;
import IO.XMLWriter;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

/*
    This is a manager class that is responsible for modifying the project - e.g. adding people and variables.
 */
public class TreeKeeper {

    private Project project;
    private int latest_person_id = 0;
    private int latest_variable_id = 0;
    public boolean saved_before = false;
    private String last_location = "";

    private MainWindow window;

    private final Properties config;

    public TreeKeeper() {
        config = new Properties();
        try {
            FileInputStream reader = new FileInputStream("config.properties");
            config.load(reader);
            reader.close();
        } catch (IOException ignored) {}

        boolean load_last = Boolean.parseBoolean(config.getProperty("load_last", "True"));
        String last = config.getProperty("last", "");
        project = new Project("Untitled project");
        window = new MainWindow(project.graphs, "Untitled project", this, config);

        if (load_last && !last.strip().equals("")) {
            loadProject(new File(last));
        }
    }

    public void saveProject(File destination, boolean copy) {
        if (!copy) {
            destination = new File(last_location);
        }
        if (!destination.getName().matches(".+(.tree)$")) {
            destination = new File(destination + ".tree");
        }
        for (Canvas canvas : window.canvases) {
            canvas.writeLayout();
        }
        project.name = destination.getName().replace(".tree", "");
        window.setTitle(project.name);
        try {
            SkeletonWriter.writeProject(project, "tmp");
            destination.delete();
            File file = new File("tmp");
            file.renameTo(destination);
            saved_before = true;
            last_location = destination.getAbsolutePath();
            config.setProperty("last", last_location);
            window.saveConfig();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error saving project: " + ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            File temp = new File("tmp");
            temp.delete();
        }
    }

    public void loadProject(File source) {
        LoadInfoFrame frame = new LoadInfoFrame(source.getName());
        frame.update(frame.getGraphics());
        try {
            project = SkeletonReader.readProject(source.getPath());
            saved_before = true;
            window.dispose();
            window = new MainWindow(project.graphs, project.name, this, config);
            IntStream stream = project.people.keySet().stream().mapToInt(new ToIntFunction<Integer>() {
                @Override
                public int applyAsInt(Integer value) { return value; }
            });
            latest_person_id = stream.max().orElse(0) + 1;
            stream = project.variables.keySet().stream().mapToInt(new ToIntFunction<Integer>() {
                @Override
                public int applyAsInt(Integer value) { return value; }
            });
            latest_variable_id = stream.max().orElse(0) + 1;
            saved_before = true;
            last_location = source.getAbsolutePath();
            config.setProperty("last", last_location);
            window.saveConfig();
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
        frame.dispose();
    }

    public void newProject() {
        int result = JOptionPane.showConfirmDialog(window, "Are you sure? All unsaved changes will be deleted.", "Confirm", JOptionPane.YES_NO_OPTION);
        if (result != 0) return;
        window.dispose();
        saved_before = false;
        last_location = "";
        project = new Project("Untitled project");
        window = new MainWindow(project.graphs, "Untitled project", this, config);
        config.setProperty("last", "");
        window.saveConfig();
    }

    public void export(File destination, String extension){
        if (!destination.getName().matches(".+(." + extension + ")$")) {
            destination = new File(destination + "." + extension);
        }
        try {
            switch (extension) {
                case "xml" -> XMLWriter.writeProject(project, destination, Boolean.parseBoolean(config.getProperty("readable_xml", "false")));
                case "json" -> JSONWriter.writeProject(project, destination);
            }
            JOptionPane.showMessageDialog(window, "Export complete.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, "Error when exporting: " + ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    };

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
