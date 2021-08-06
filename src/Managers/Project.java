package Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    This represents a single project.
 */
public class Project {
    public List<Graph> graphs;
    public Map<Integer, Person> people;
    public String name;

    public Project(String name) {
        graphs = new ArrayList<>();
        people = new HashMap<>();
    }
}
