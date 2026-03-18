package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;

public class Project {
    private String nome;
    private List<Task> tasks; //lembrar de n deixar ter tasks com mesmo nome dentro do mesmo projeto
    private int totalBudget;

    private  int currentBudget;


    public Project(String nome, int totalBudget){
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do projeto não pode ser vazio.");
        }

        if (totalBudget < 0) {
            throw new IllegalArgumentException("Orçamento de horas deve ser maior que zero.");
        }


        this.nome = nome;
        this.tasks = new ArrayList<>();
        this.totalBudget = totalBudget;
        this.currentBudget = 0;
    }

    public void addTask(Task T){
        //se pode adicionar
        if (T.getEstimatedEffort() + currentBudget <= this.totalBudget ){
            this.currentBudget += T.getEstimatedEffort();
            this.tasks.add(T);
        }
        else{
            throw new NexusValidationException("Excedeu o budget de tempo.");
        }
    }

}