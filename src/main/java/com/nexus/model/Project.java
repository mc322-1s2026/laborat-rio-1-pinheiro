package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String nome;
    private List<Task> tasks;
    private float totalBudget;


    public Project(String nome, float totalBudget){
        this.nome = nome;
        this.tasks = new ArrayList<>();
        this.totalBudget = totalBudget;
    }

}