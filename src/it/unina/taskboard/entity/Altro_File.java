package it.unina.taskboard.entity;

import java.time.LocalDate;

// Mappa la tabella 'Altro_File' associata a Documentazione.
public class Altro_File {
    private int idFile; 
    private String nomeFile;
    private double dimensioni;
    private LocalDate dataCreazione;
    private String tipoFile;
    private boolean soloLettura;
    private String estensione;
    
    private int idDocumento;

    public Altro_File() {}

    public Altro_File(int idFile, String nomeFile, double dimensioni, LocalDate dataCreazione, 
                      String tipoFile, boolean soloLettura, String estensione, int idDocumento) {
        this.idFile = idFile;
        this.nomeFile = nomeFile;
        this.dimensioni = dimensioni;
        this.dataCreazione = dataCreazione;
        this.tipoFile = tipoFile;
        this.soloLettura = soloLettura;
        this.estensione = estensione;
        this.idDocumento = idDocumento;
    }

    // Getters e Setters
    public int getIdFile() { return idFile; }
    public void setIdFile(int idFile) { this.idFile = idFile; }

    public String getNomeFile() { return nomeFile; }
    public void setNomeFile(String nomeFile) { this.nomeFile = nomeFile; }

    public double getDimensioni() { return dimensioni; }
    public void setDimensioni(double dimensioni) { this.dimensioni = dimensioni; }

    public LocalDate getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDate dataCreazione) { this.dataCreazione = dataCreazione; }

    public String getTipoFile() { return tipoFile; }
    public void setTipoFile(String tipoFile) { this.tipoFile = tipoFile; }

    public boolean isSoloLettura() { return soloLettura; }
    public void setSoloLettura(boolean soloLettura) { this.soloLettura = soloLettura; }

    public String getEstensione() { return estensione; }
    public void setEstensione(String estensione) { this.estensione = estensione; }

    public int getIdDocumento() { return idDocumento; }
    public void setIdDocumento(int idDocumento) { this.idDocumento = idDocumento; }
}