package com.example.vamomarcarintegrado;

import java.util.Calendar;

public class User {
    String id;
    String nome;
    Calendar dtNasc;
    String bio;
    String cidade;
    String estado;

    public User(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
