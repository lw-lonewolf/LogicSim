package com.logisim.domain;

import com.logisim.data.ProjectDAO;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private long id;
    private String name;
    private List<Circuit> circuits = new ArrayList<>();
    private ProjectDAO projectdao = new ProjectDAO();

    public void save() {
        System.out.println("Saving to database...");
        projectdao.saveProject(this);
    }

    public void load() {
        //TODO: IMPLEMENT DATA SIDE BEFORE WRITING THIS`
    }

    public void export() {
        //TODO: HAVEN'T FIGURED THIS OUT YET
    }

    public Project(String name) {
        this.name = name;
    }

    public Project(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Project() {
        this("Project");
    }

    public String getName() {
        return name;
    }

    public List<Circuit> getCircuits() {
        return circuits;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCircuits(List<Circuit> circuits) {
        this.circuits = circuits;
    }

    public ProjectDAO getProjectdao() {
        return projectdao;
    }

    public void setProjectdao(ProjectDAO projectdao) {
        this.projectdao = projectdao;
    }

    @Override
    public String toString() {
        return name;
    }
}
