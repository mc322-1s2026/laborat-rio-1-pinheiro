package com.nexus.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.User;
import com.nexus.Main;

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
                                if (p.length < 4) throw new IllegalArgumentException("Parâmetros insuficientes"); 
                                Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]));
                                workspace.addTask(t);
                                Project currentProject = workspace.findProject(p[4], workspace.getProjects());
                                currentProject.addTask(t);
                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }

                            case "ASSIGN_USER" -> {
                                User currentUser = Main.findUser(p[2]);

                                if(currentUser == null){ // Para user que não existe lançar exceção ou apenas dizer que não existe?
                                    System.out.println("O usuário não existe não existe: ");
                                    return;
                                }

                                Task currentTask = workspace.findTask(Integer.parseInt(p[1]));

                                if(currentTask == null){ // Para id vazio lançar exceção ou apenas dizer que não existe?
                                    System.out.println("A tarefa não existe: ");
                                    return;
                                }

                                currentTask.assignUser(currentUser);


                            }

                            case "CHANGE_STATUS" -> {
                                Task currentTask = workspace.findTask(Integer.parseInt(p[1]));

                                if(currentTask == null){ // Para id vazio lançar exceção ou apenas dizer que não existe?
                                    System.out.println("A tarefa não existe: ");
                                    return;
                                }

                                switch (p[2]) {
                                    case "IN_PROGRESS" -> {
                                        currentTask.moveToInProgress();
                                    }
                                    case "DONE" -> {
                                        currentTask.markAsDone();
                                    }
                                    case "BLOCKED" -> {
                                        currentTask.setBlocked(true);
                                    }
                                    default -> System.err.println("[WARN] Status desconhecida: " + p[2]);
                                }
                            }

                            case "REPORT_STATUS" -> {
                                workspace.reportStatus();
                            }

                            case "CREATE_PROJECT" -> {
                                if (p.length < 2) throw new IllegalArgumentException("Parâmetros insuficientes"); 
                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }

                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }

                    } catch (NexusValidationException e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE REGRAS] Linha '" + line + "': " + e.getMessage());

                    } catch (Exception e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO] Linha '" + line + "': " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}