package com.nexus.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.nexus.Main;
import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.User;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);

            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));

                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    try {
                        String[] p = line.split(";");
                        String action = p[0];

                        switch (action) {

                            case "CREATE_USER" -> {
                                if (p.length < 3) throw new IllegalArgumentException("Parâmetros insuficientes"); 
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }

                            case "CREATE_TASK" -> {
                                if (p.length < 5) throw new IllegalArgumentException("Parâmetros insuficientes"); 
                                try{
                                    Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]));
                                    Project currentProject = workspace.findProject(p[4], workspace.getProjects());
                                    if(currentProject == null){
                                        throw new IllegalArgumentException("O projeto não existe");
                                    }
                                    currentProject.addTask(t);
                                    workspace.addTask(t);
                                    System.out.println("[LOG] Tarefa criada: " + p[1]);
                                }catch(NexusValidationException e){
                                    Task.recallID();
                                    System.err.println("[ERRO DE REGRAS] Tarefa excede o limite de horas do projeto");
                                }
                                
                            }

                            case "ASSIGN_USER" -> {
                                if (p.length < 3) throw new IllegalArgumentException("Parâmetros insuficientes");

                                String username = p[2];
                                int taskId = Integer.parseInt(p[1]);

                                if (username == null || username.isBlank()) {
                                    System.err.println("[ERRO] Usuário vazio");
                                    break;
                                }

                                User currentUser = Main.findUser(username);
                                if (currentUser == null) {
                                    System.err.println("[ERRO] Usuário não encontrado: " + username);
                                    break;
                                }

                                Task currentTask = workspace.findTask(taskId);
                                if (currentTask == null) {
                                    System.err.println("[ERRO] Tarefa " + taskId + " não existe");
                                    break;
                                }

                                currentTask.assignUser(currentUser);
                            }

                            case "CHANGE_STATUS" -> {
                                if (p.length < 3) throw new IllegalArgumentException("Parâmetros insuficientes");

                                int taskId = Integer.parseInt(p[1]);
                                String status = p[2];

                                Task currentTask = workspace.findTask(taskId);

                                if (currentTask == null) {
                                    System.err.println("[ERRO] Tarefa " + taskId + " não existe");
                                    break;
                                }

                                try {
                                    switch (status) {
                                        case "IN_PROGRESS" -> currentTask.moveToInProgress();
                                        case "DONE" -> currentTask.markAsDone();
                                        case "BLOCKED" -> currentTask.setBlocked(true);
                                        default -> System.err.println("[WARN] Status desconhecido: " + status);
                                    }
                                } catch (NexusValidationException e) {
                                    System.err.println("[ERRO DE REGRAS] " + e.getMessage());
                                }
                            }

                            case "REPORT_STATUS" -> {
                                workspace.reportStatus();
                            }

                            case "CREATE_PROJECT" -> {
                                if (p.length < 2) throw new IllegalArgumentException("Parâmetros insuficientes");

                                if(workspace.findProject(p[1], workspace.getProjects()) != null){
                                    throw new IllegalArgumentException("Projeto já está no sistema");
                                }

                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }

                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }

                    } catch (NexusValidationException e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE REGRAS] Linha '" + line + "': " + e.getMessage());
                    

                    } catch(NumberFormatException e){
                        System.err.println("[ERRO DE FORMATAÇÃO DE NÚMERO] Linha '" + line + "': " + e.getMessage());

                    }catch (NullPointerException e) {
                        System.err.println("[ERRO] Argumento inválido");
                    }catch (Exception e) {
                        System.err.println("[ERRO] Linha '" + line + "': " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}