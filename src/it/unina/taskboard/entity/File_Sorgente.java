package it.unina.taskboard.entity;

import java.time.LocalDate;

//Mappa la tabella 'File_Sorgente' associata a Sviluppo.
public class File_Sorgente {
    private String path; 
    private String nomeFile;
    private double dimensioni;
    private LocalDate dataCreazione;
    private String estensione;
    
    private int idSviluppo;
    
    //costruttore vuoto
    public File_Sorgente() {}
    
    //costruttore
    public File_Sorgente(String path, String nomeFile, double dimensioni, 
                         LocalDate dataCreazione, String estensione, int idSviluppo) {
        this.path = path;
        this.nomeFile = nomeFile;
        this.dimensioni = dimensioni;
        this.dataCreazione = dataCreazione;
        this.estensione = estensione;
        this.idSviluppo = idSviluppo;
    }

    // Getters e Setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getNomeFile() { return nomeFile; }
    public void setNomeFile(String nomeFile) { this.nomeFile = nomeFile; }

    public double getDimensioni() { return dimensioni; }
    public void setDimensioni(double dimensioni) { this.dimensioni = dimensioni; }

    public LocalDate getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDate dataCreazione) { this.dataCreazione = dataCreazione; }

    public String getEstensione() { return estensione; }
    public void setEstensione(String estensione) { this.estensione = estensione; }

    public int getIdSviluppo() { return idSviluppo; }
    public void setIdSviluppo(int idSviluppo) { this.idSviluppo = idSviluppo; }
}