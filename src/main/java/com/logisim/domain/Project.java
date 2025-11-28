package com.logisim.domain;

import java.util.ArrayList;
import java.util.List;

public class Project {

    private String name;
    private String path;
    private List<Circuit> circuits = new ArrayList<>();

    public void save() {
        //TODO: IMPLEMENT DATA SIDE BEFORE WRITING THIS
    }

    public void load() {
        //TODO: IMPLEMENT DATA SIDE BEFORE WRITING THIS`
    }

    public void export() {
        //TODO: HAVEN'T FIGURED THIS OUT YET
    }

    public Project(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Project() {
        this("Project", "/home/lonewolf");
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public List<Circuit> getCircuits() {
        return circuits;
    }
}
