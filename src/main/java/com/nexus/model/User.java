package com.nexus.model;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio."); //Existe uma redundância nesse teste em relação ao próximo, mas preferi fazer assim para imprimir um texto apropriado.
        }

        //No README a definição do email valido foi "usuario@dominio.com"
        else if (!email.matches("^[^@\\s]+@[^@\\s]+\\.com")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        
        this.username = username;
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload() {
        return 0; 
    }
}