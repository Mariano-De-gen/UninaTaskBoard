package it.unina.taskboard.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Sottoclasse di Attivita.
public class Documentazione extends Attivita {
    // L'ID_Documento coinciderÃ  con getIdAttivita() ereditato dal padre
    private String tipoDocumento;
    private String linkDocumento;

    // Relazioni
    private List<Altro_File> fileAllegati;
    
    //costruttore vuoto
    public Documentazione() {
        super();
        this.fileAllegati = new ArrayList<>();
    }
    
    //costruttore
    public Documentazione(int idAttivita, String descrizione, LocalDate dataCreazione, 
                          LocalDate dataScadenza, double statoAvanzamento, 
                          int matricolaCreatore, int nProgetto, 
                          String tipoDocumento, String linkDocumento) {
        super(idAttivita, descrizione, dataCreazione, dataScadenza, statoAvanzamento, matricolaCreatore, nProgetto);
        this.tipoDocumento = tipoDocumento;
        this.linkDocumento = linkDocumento;
        this.fileAllegati = new ArrayList<>();
    }

    public void aggiungiFileAllegato(Altro_File file) {
        this.fileAllegati.add(file);
    }

    // Getters e Setters
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getLinkDocumento() { return linkDocumento; }
    public void setLinkDocumento(String linkDocumento) { this.linkDocumento = linkDocumento; }

    public List<Altro_File> getFileAllegati() { return fileAllegati; }
    public void setFileAllegati(List<Altro_File> fileAllegati) { this.fileAllegati = fileAllegati; }
}