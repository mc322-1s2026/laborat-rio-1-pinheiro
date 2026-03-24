package com.nexus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;


public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addProject(Project project){
        projects.add(project);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public List<Project> getProjects(){
        return Collections.unmodifiableList(projects);
    }

    /*Imprime os três usuarios com mais tasks DONE */
    public void topPerformers(List<Task> getTasks){
        Map<User, Long> topOwners = getTasks.stream()
            .filter(Task -> Task.getStatus() == TaskStatus.DONE)
            .collect(Collectors.groupingBy(
                Task::getOwner,
                Collectors.counting()
            ));
        
        if(topOwners.isEmpty()){
            System.out.println("[!] Nenhum usuario possui tarefas concluidas.");
            return;
        }

        topOwners.entrySet().stream()
            .sorted(Map.Entry.<User,Long>comparingByValue().reversed())
            .limit(3)
            .forEach(usuarios -> {
                System.out.println(usuarios.getKey().consultUsername());
            });

    }

    /*Imprime o(s) status com maior(es) número(s) de Tasks, exceto DONE */
    public void globalBottleNecks(List<Task> getTasks){

        Map<TaskStatus, Long> topTasks = getTasks.stream()
        .filter(Task -> Task.getStatus() != TaskStatus.DONE)
        .collect(Collectors.groupingBy(
            Task::getStatus,
            Collectors.counting()
        ));

        long maxCount = topTasks.values().stream()
            .max(Long::compare).get();
        
        if(maxCount == 0){
            System.out.println("[!] Nenhuma tarefa no sistema, exceto com status DONE.");
            return;
        }

        topTasks.entrySet().stream()
            .filter(entry -> entry.getValue() == maxCount)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .forEach(System.out::println);
        
    }

    /*Imprime os usuários que possuem carga de trabalho maior que 10*/
    public void overloadedUsers(List<Task> getTasks){
        Map<User, Long> busyUsers = getTasks.stream()
            .filter(Task -> Task.getStatus() == TaskStatus.IN_PROGRESS)
            .collect(Collectors.groupingBy(
                Task::getOwner,
                Collectors.counting()
            ));

        busyUsers.entrySet().stream()
            .filter(entry -> entry.getValue() > 10)
            .forEach(usuarios -> {
                System.out.println(usuarios.getKey().consultUsername());
            });

    }



    public Task findTask(int id){
        return getTasks().stream()
            .filter(t -> t.getId() == id).findFirst()
            .orElse(null);
    }

    public Project findProject(String nome, List<Project> getProjects){
        return getProjects.stream()
            .filter(t -> t.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElse(null);
    }

    public void projectHealth(List<Project> getProjects){
         getProjects.stream().forEach(p ->{

        long completed_tasks = p.getProjectTasks().stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .count();

        long total_tasks = p.getProjectTasks().size();

        if(total_tasks == 0)
            System.out.println(p.getNome()+ " " + 0.0);
        else
            System.out.println(p.getNome() + " " + completed_tasks * 100 / total_tasks);
        });

    }

    public void reportStatus(){
        if(tasks.isEmpty()){
            System.out.println("\n[!] Nenhuma tarefa no sistema.");
            return;
        }
        System.out.println("\nUsuarios com carga de trabalho maior que 10:");
        overloadedUsers(getTasks());

        System.out.println("\nStatus com maior número de tasks:");
        globalBottleNecks(getTasks());

        System.out.println("\nUsuarios com mais taks concluidas:");
        topPerformers(getTasks());

        System.out.println("\nPorcentagem de tarefas concluidas:");
        projectHealth(getProjects());


    }
}