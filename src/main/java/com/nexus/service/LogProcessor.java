package com.nexus.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
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

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }
                            case "CREATE_TASK" -> {
                                Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]));
                                workspace.addTask(t);
                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }
                            case "ASSIGN_USER" -> {

                            }
                            case "CHANGE_STATUS" -> {
                                if(workspace.getTask(Integer.parseInt(p[1])) == null){ // Para id vazio lançar exceção ou apenas dizer que não existe?
                                    System.out.println("A tarefa não existe: ");
                                    return;
                                }
                                switch (p[2]) {
                                    case "IN_PROGRESS" -> {
                                         
                                    }
                                    case "DONE" -> {

                                    }
                                    case "BLOCKED" -> {

                                    }
                                    default -> System.err.println("[WARN] Status desconhecida: " + p[2]);
                                }
                            }
                            case "REPORT_STATUS" -> {
                                workspace.reportStatus();
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}