package com.example.vamomarcarintegrado;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class User {
    String id;
    String nome;
    Calendar dtNasc;
    String bio;
    String estado;
    Bitmap img;

    public User(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public User(String id, String nome, Bitmap img) {
        this.id = id;
        this.nome = nome;
        this.img = img;
    }

    public User(String id, String nome, String bio, Calendar dtNasc, String estado) {
        this.id = id;
        this.nome = nome;
        this.bio = bio;
        this.estado = estado;
        this.dtNasc = dtNasc;
    }

    public User(String id, String nome, String bio, Calendar dtNasc, String estado, Bitmap img) {
        this.id = id;
        this.nome = nome;
        this.bio = bio;
        this.estado = estado;
        this.dtNasc = dtNasc;
        this.img = img;
    }

    public String getMonthName(){
        String monthName = this.dtNasc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        return monthName;
    }

    public String getYear(){
        int year = this.dtNasc.get(Calendar.YEAR);
        String yearString = String.valueOf(year);
        return yearString;
    }

    public String getDay(){
        int day = this.dtNasc.get(Calendar.DAY_OF_MONTH);
        String dayofmonth = String.valueOf(day);
        return dayofmonth;
    }
}
