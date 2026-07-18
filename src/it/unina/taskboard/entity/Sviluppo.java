package it.unina.taskboard.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Sottoclasse di Attivita.
 * Mappa la tabella 'Sviluppo'.
 */
public class Sviluppo extends Attivita {
    // L'ID_Sviluppo coinciderÃ  con getIdAttivita() ereditato dal padre
    private String tipoLinguaggio;

    // Relazioni
    private List<File_Sorgente> fileSorgenti;
    //costruttore vuoto
    public Sviluppo() {
        super();
        this.fileSorgenti = new ArrayList<>();
    }
    // costruttore
    public Sviluppo(int idAttivita, String descrizione, LocalDate dataCreazione, 
                    LocalDate dataScadenza, double statoAvanzamento, 
                    int matricolaCreatore, int nProgetto, 
                    String tipoLinguaggio) {
        super(idAttivita, descrizione, dataCreazione, dataScadenza, statoAvanzamento, matricolaCreatore, nProgetto);
        this.tipoLinguaggio = tipoLinguaggio;
        this.fileSorgenti = new ArrayList<>();
    }

    public void aggiungiFileSorgente(File_Sorgente file) {
        this.fileSorgenti.add(file);
    }

    // Getters e Setters
    public String getTipoLinguaggio() { return tipoLinguaggio; }
    public void setTipoLinguaggio(String tipoLinguaggio) { this.tipoLinguaggio = tipoLinguaggio; }

    public List<File_Sorgente> getFileSorgenti() { return fileSorgenti; }
    public void setFileSorgenti(List<File_Sorgente> fileSorgenti) { this.fileSorgenti = fileSorgenti; }
}