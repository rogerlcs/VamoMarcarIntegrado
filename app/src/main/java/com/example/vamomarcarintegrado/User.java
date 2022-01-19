package com.example.vamomarcarintegrado;

import java.util.Calendar;
import java.util.Locale;

public class User {
    String id;
    String nome;
    Calendar dtNasc;
    String bio;
    String estado;

    public User(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public User(String id, String nome, String bio, Calendar dtNasc, String estado) {
        this.id = id;
        this.nome = nome;
        this.bio = bio;
        this.estado = estado;
        this.dtNasc = dtNasc;
    }

    public String getMonthName(){
        String monthName = this.dtNasc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        return monthName;
    }

    public String getYear(){
        String year = String.valueOf(this.dtNasc.get(Calendar.YEAR));
        return year;
    }

    public String getDay(){
        int day = this.dtNasc.get(Calendar.DAY_OF_MONTH);
        String dayofmonth = String.valueOf(day);
        return dayofmonth;
    }
}
