package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public void topPerformers(List<Task> tasks){
        //List<Task> topTasks = tasks.stream()
        //.sorted(Comparator.comparing(Task::));
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
}