package it.unina.taskboard.entity;

import java.util.ArrayList;
import java.util.List;

//Classe Entity che rappresenta uno Studente.
public class Studente {
    private int matricola; 
    private String nome;
    private String cognome;
    private String email;
    private String password;

    // Liste contenenti le righe contenute nelle tabelle ponte
    private List<Progetto> progettiPartecipati;
    private List<Attivita> attivitaAssegnate;

    //Costruttore Vuoto
    public Studente() {
        this.progettiPartecipati = new ArrayList<>();
        this.attivitaAssegnate = new ArrayList<>();
    }
    
    //costruttore
    public Studente(int matricola, String nome, String cognome, String email, String password) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.progettiPartecipati = new ArrayList<>();
        this.attivitaAssegnate = new ArrayList<>();
    }

    // Getters e Setters
    public int getMatricola() { return matricola; }
    public void setMatricola(int matricola) { this.matricola = matricola; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Progetto> getProgettiPartecipati() { return progettiPartecipati; }
    public void setProgettiPartecipati(List<Progetto> progettiPartecipati) { this.progettiPartecipati = progettiPartecipati; }

    public List<Attivita> getAttivitaAssegnate() { return attivitaAssegnate; }
    public void setAttivitaAssegnate(List<Attivita> attivitaAssegnate) { this.attivitaAssegnate = attivitaAssegnate; }
}