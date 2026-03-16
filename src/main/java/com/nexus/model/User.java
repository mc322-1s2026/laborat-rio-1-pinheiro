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

        else if (!email.contains("@") || !email.contains(".com")) {
            throw new IllegalArgumentException("Domínio do email inválido.");
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