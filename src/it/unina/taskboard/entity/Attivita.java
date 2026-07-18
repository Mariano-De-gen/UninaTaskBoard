package it.unina.taskboard.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Classe astratta/base che rappresenta un'Attività generica.
public class Attivita {
    private int idAttivita; 
    private String descrizione;
    private LocalDate dataCreazione;
    private LocalDate dataScadenza;
    private double statoAvanzamento; 
    
    private int matricolaCreatore; 
    private int nProgetto;         

    // Relazioni
    private List<Studente> studentiAssegnati;
    //Costruttore vuoto
    public Attivita() {
        this.studentiAssegnati = new ArrayList<>();
    }
    //costruttore
    public Attivita(int idAttivita, String descrizione, LocalDate dataCreazione, 
                    LocalDate dataScadenza, double statoAvanzamento, 
                    int matricolaCreatore, int nProgetto) {
        this.idAttivita = idAttivita;
        this.descrizione = descrizione;
        this.dataCreazione = dataCreazione;
        this.dataScadenza = dataScadenza;
        this.statoAvanzamento = statoAvanzamento;
        this.matricolaCreatore = matricolaCreatore;
        this.nProgetto = nProgetto;
        this.studentiAssegnati = new ArrayList<>();
    }

    // Getters e Setters
    public int getIdAttivita() { return idAttivita; }
    public void setIdAttivita(int idAttivita) { this.idAttivita = idAttivita; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public LocalDate getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDate dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDate getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(LocalDate dataScadenza) { this.dataScadenza = dataScadenza; }

    public double getStatoAvanzamento() { return statoAvanzamento; }
    public void setStatoAvanzamento(double statoAvanzamento) { this.statoAvanzamento = statoAvanzamento; }

    public int getMatricolaCreatore() { return matricolaCreatore; }
    public void setMatricolaCreatore(int matricolaCreatore) { this.matricolaCreatore = matricolaCreatore; }

    public int getNProgetto() { return nProgetto; }
    public void setNProgetto(int nProgetto) { this.nProgetto = nProgetto; }

    public List<Studente> getStudentiAssegnati() { return studentiAssegnati; }
    public void setStudentiAssegnati(List<Studente> studentiAssegnati) { this.studentiAssegnati = studentiAssegnati; }
}