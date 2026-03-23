package com.nexus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;


public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    /*Imprime os três usuarios com mais tasks DONE */
    public void topPerformers(List<Task> tasks){
        Map<User, Long> topOwners = tasks.stream()
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
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .forEach(System.out::println);

    }

    /*Imprime o(s) status com maior(es) número(s) de Tasks, exceto DONE */
    public void globalBottleNecks(List<Task> tasks){

        Map<TaskStatus, Long> topTasks = tasks.stream()
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
    public void overloadedUsers(List<Task> tasks){
        Map<User, Long> busyUsers = tasks.stream()
            .filter(Task -> Task.getStatus() == TaskStatus.IN_PROGRESS)
            .collect(Collectors.groupingBy(
                Task::getOwner,
                Collectors.counting()
            ));

        busyUsers.entrySet().stream()
            .filter(entry -> entry.getValue() > 10)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .forEach(System.out::println);

    }

    public Task getTask(int id){
        return tasks.stream()
            .filter(t -> t.getId() == id).findFirst()
            .orElse(null);
    }

    public void reportStatus(){
        if(tasks.isEmpty()){
            System.out.println("\n[!] Nenhuma tarefa no sistema.");
            return;
        }
        System.out.println("\nUsuarios com carga de trabalho maior que 10:");
        overloadedUsers(tasks);

        System.out.println("\nStatus com maior número de tasks:");
        globalBottleNecks(tasks);

        System.out.println("\nUsuarios com mais taks concluidas:");
        topPerformers(tasks);
    }
}