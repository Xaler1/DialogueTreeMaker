package Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
    public List<Graph> graphs;
    public Map<Integer, Character> people;
    public String name;

    public Project(String name) {
        graphs = new ArrayList<>();
        people = new HashMap<>();
    }
}
