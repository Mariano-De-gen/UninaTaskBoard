package it.unina.taskboard.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//Classe Entity che rappresenta un Progetto.
public class Progetto {
    private int nProgetto; 
    private String nomeProg;
    private String descrizione;
    private int matricolaCreatore; 
    private TipoProgetto tipoDiProgetto;

    // Attributi derivanti dall'accorpamento verso l'alto
    private String noteCollaborazione; // Solo per Progetti_Gruppo
    private String esame;              // Solo per Preparazione_Esame
    private LocalDate dataEsame;       // Solo per Preparazione_Esame
    private String nomeApp;            // Solo per Sviluppo_APP

    // Relazioni
    private List<Studente> membri;
    private List<Attivita> listaAttivita;
    
    //costruttore vuoto
    public Progetto() {
        this.membri = new ArrayList<>();
        this.listaAttivita = new ArrayList<>();
    }
    
    //costruttore
    public Progetto(int nProgetto, String nomeProg, String descrizione, int matricolaCreatore, 
                    TipoProgetto tipoDiProgetto, String noteCollaborazione, 
                    String esame, LocalDate dataEsame, String nomeApp) {
        this.nProgetto = nProgetto;
        this.nomeProg = nomeProg;
        this.descrizione = descrizione;
        this.matricolaCreatore = matricolaCreatore;
        this.tipoDiProgetto = tipoDiProgetto;
        this.noteCollaborazione = noteCollaborazione;
        this.esame = esame;
        this.dataEsame = dataEsame;
        this.nomeApp = nomeApp;
        this.membri = new ArrayList<>();
        this.listaAttivita = new ArrayList<>();
    }

    // Getters e Setters
    public int getNProgetto() { return nProgetto; }
    public void setNProgetto(int nProgetto) { this.nProgetto = nProgetto; }

    public String getNomeProg() { return nomeProg; }
    public void setNomeProg(String nomeProg) { this.nomeProg = nomeProg; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public int getMatricolaCreatore() { return matricolaCreatore; }
    public void setMatricolaCreatore(int matricolaCreatore) { this.matricolaCreatore = matricolaCreatore; }

    public TipoProgetto getTipoDiProgetto() { return tipoDiProgetto; }
    public void setTipoDiProgetto(TipoProgetto tipoDiProgetto) { this.tipoDiProgetto = tipoDiProgetto; }

    public String getNoteCollaborazione() { return noteCollaborazione; }
    public void setNoteCollaborazione(String noteCollaborazione) { this.noteCollaborazione = noteCollaborazione; }

    public String getEsame() { return esame; }
    public void setEsame(String esame) { this.esame = esame; }

    public LocalDate getDataEsame() { return dataEsame; }
    public void setDataEsame(LocalDate dataEsame) { this.dataEsame = dataEsame; }

    public String getNomeApp() { return nomeApp; }
    public void setNomeApp(String nomeApp) { this.nomeApp = nomeApp; }

    public List<Studente> getMembri() { return membri; }
    public void setMembri(List<Studente> membri) { this.membri = membri; }

    public List<Attivita> getListaAttivita() { return listaAttivita; }
    public void setListaAttivita(List<Attivita> listaAttivita) { this.listaAttivita = listaAttivita; }
}