package it.unina.taskboard.control;

import it.unina.taskboard.entity.*;
import it.unina.taskboard.utils.DatabaseConnection;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.time.LocalDate;

public class TaskboardControl {
    private Connection conn;

    /**
     * COSTRUTTORE
     * Inizializza l'istanza stabilendo la connessione al database.
     */
    public TaskboardControl() {
        try {
            this.conn = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            System.err.println("Errore di connessione nel Livello Control: " + e.getMessage());
        }
    }

    /**
     * 1. LOGIN
     * Verifica email e password nel database. Restituisce l'oggetto Studente popolato.
     */
    public Studente login(String email, String password) {
        String query = "SELECT * FROM Studente WHERE Email = ? AND Password = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Studente(
                    rs.getInt("Matricola"),
                    rs.getString("Nome"),
                    rs.getString("Cognome"),
                    rs.getString("Email"),
                    rs.getString("Password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Errore critico durante il login: " + e.getMessage());
        }
        return null; 
    }

    /**
     * 2. CREA NUOVO PROGETTO
     * Richiama la procedura SQL Inserisci_Progetto. Gestisce l'accorpamento verso l'alto
     * passando NULL ai campi non pertinenti in base al TipoProgetto.
     */
    public boolean creaNuovoProgetto(String nomeProg, String descrizione, TipoProgetto tipo, 
                                     int matricolaCreatore, String noteCollab, String esame, 
                                     LocalDate dataEsame, String nomeApp) {
        
        // La procedura richiede esattamente 8 parametri come da schema DDL
        String sql = "CALL Inserisci_Progetto(?, ?, ?, CAST(? AS TipoProgetto), ?, ?, ?, ?)"; 
        
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, nomeProg);
            cstmt.setString(2, descrizione);
            cstmt.setInt(3, matricolaCreatore);
            cstmt.setString(4, tipo.name()); 
            
            // Gestione vincoli di mutua esclusione per i null
            if (noteCollab != null) cstmt.setString(5, noteCollab);
            else cstmt.setNull(5, Types.VARCHAR);
            
            if (esame != null) cstmt.setString(6, esame);
            else cstmt.setNull(6, Types.VARCHAR);
            
            if (dataEsame != null) cstmt.setDate(7, Date.valueOf(dataEsame));
            else cstmt.setNull(7, Types.DATE);
            
            if (nomeApp != null) cstmt.setString(8, nomeApp);
            else cstmt.setNull(8, Types.VARCHAR);
            
            cstmt.execute();
            return true; 
            
        } catch (SQLException e) {
            System.err.println("Errore SQL nella creazione del progetto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 3. OTTIENI LISTA PROGETTI
     * Restituisce tutti i progetti a cui lo studente partecipa, sfruttando la tabella ponte.
     */
    public List<Progetto> ottieniListaProgetti(int matricola) {
        List<Progetto> listaProgetti = new ArrayList<>();
        
        String query = "SELECT p.* FROM Progetto p " +
                       "JOIN Partecipazione part ON p.N_Progetto = part.N_Progetto " +
                       "WHERE part.Matricola = ?";
                       
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, matricola);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Progetto p = new Progetto();
                p.setNProgetto(rs.getInt("N_Progetto"));
                p.setNomeProg(rs.getString("Nome_PROG"));
                p.setDescrizione(rs.getString("Descrizione"));
                p.setMatricolaCreatore(rs.getInt("Matricola_Creatore"));
                p.setTipoDiProgetto(TipoProgetto.valueOf(rs.getString("Tipo_di_progetto")));
                
                // Campi opzionali
                p.setNoteCollaborazione(rs.getString("Note_Collaborazione"));
                p.setEsame(rs.getString("Esame"));
                p.setNomeApp(rs.getString("Nome_App"));
                
                Date sqlDate = rs.getDate("data_Esame");
                if(sqlDate != null) p.setDataEsame(sqlDate.toLocalDate());
                
                listaProgetti.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recupero della lista progetti: " + e.getMessage());
        }
        
        return listaProgetti;
    }
    
    /**
     * 4. APRI DETTAGLIO PROGETTO
     * Recupera un progetto specifico e popola le sue liste interne di Attività e Membri.
     * Sfrutta le Viste e il polimorfismo delle Entità Java.
     */
    public Progetto apriDettaglioProgetto(int nProgetto) {
        Progetto p = null;

        // Fase 1: Dati base del Progetto
        String queryProgetto = "SELECT * FROM Progetto WHERE N_Progetto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryProgetto)) {
            pstmt.setInt(1, nProgetto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                p = new Progetto();
                p.setNProgetto(rs.getInt("N_Progetto"));
                p.setNomeProg(rs.getString("Nome_PROG"));
                p.setDescrizione(rs.getString("Descrizione"));
                p.setTipoDiProgetto(TipoProgetto.valueOf(rs.getString("Tipo_di_progetto")));
            }
        } catch (SQLException e) {
            System.err.println("Errore Progetto: " + e.getMessage());
            return null;
        }

        if (p != null) {
            // Fase 2: Popolamento Attività usando la Vista_Dettaglio_Attivita
            String queryAttivita = "SELECT * FROM Vista_Dettaglio_Attivita WHERE N_Progetto = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(queryAttivita)) {
                pstmt2.setInt(1, nProgetto);
                ResultSet rs2 = pstmt2.executeQuery();

                while (rs2.next()) {
                    Attivita a;
                    String categoria = rs2.getString("Categoria_Attivita");
                    
                    // Polimorfismo: Crea l'oggetto specifico in base al DB
                    if ("Documentazione".equals(categoria)) {
                        Documentazione doc = new Documentazione();
                        doc.setTipoDocumento(rs2.getString("Tipo_Documento"));
                        doc.setLinkDocumento(rs2.getString("Link_Documento"));
                        a = doc;
                    } else if ("Sviluppo".equals(categoria)) {
                        Sviluppo svi = new Sviluppo();
                        svi.setTipoLinguaggio(rs2.getString("Tipo_Linguaggio"));
                        a = svi;
                    } else {
                        a = new Attivita(); 
                    }

                    // Impostazione attributi ereditati dal padre
                    a.setIdAttivita(rs2.getInt("ID_Attivita"));
                    a.setDescrizione(rs2.getString("Descrizione"));
                    a.setStatoAvanzamento(rs2.getDouble("Stato_avanzamento"));
                    a.setMatricolaCreatore(rs2.getInt("Matricola_Creatore"));
                    a.setNProgetto(nProgetto);

                    Date sqlCreazione = rs2.getDate("Data_Creazione");
                    if(sqlCreazione != null) a.setDataCreazione(sqlCreazione.toLocalDate());

                    Date sqlScadenza = rs2.getDate("Data_Scadenza");
                    if(sqlScadenza != null) a.setDataScadenza(sqlScadenza.toLocalDate());

                    p.getListaAttivita().add(a);
                }
            } catch (SQLException e) {
                System.err.println("Errore Attività: " + e.getMessage());
            }

            // Fase 3: Popolamento Membri del team
            String queryMembri = "SELECT s.* FROM Studente s JOIN Partecipazione part ON s.Matricola = part.Matricola WHERE part.N_Progetto = ?";
            try (PreparedStatement pstmt3 = conn.prepareStatement(queryMembri)) {
                pstmt3.setInt(1, nProgetto);
                ResultSet rs3 = pstmt3.executeQuery();

                while (rs3.next()) {
                    Studente s = new Studente();
                    s.setMatricola(rs3.getInt("Matricola"));
                    s.setNome(rs3.getString("Nome"));
                    s.setCognome(rs3.getString("Cognome"));
                    s.setEmail(rs3.getString("Email"));

                    p.getMembri().add(s);
                }
            } catch (SQLException e) {
                System.err.println("Errore Membri: " + e.getMessage());
            }
        }
        return p;
    }

    /**
     * 5. INVITA STUDENTE
     * Trova la matricola dal DB tramite email e lo aggiunge alla tabella Partecipazione.
     */
    public boolean invitaStudente(String emailMittente, String emailDestinatario, int nProgetto) {
        String queryMatricola = "SELECT Matricola FROM Studente WHERE Email = ?";
        int matricolaDestinatario = -1;
        
        try (PreparedStatement pstmt = conn.prepareStatement(queryMatricola)) {
            pstmt.setString(1, emailDestinatario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                matricolaDestinatario = rs.getInt("Matricola");
            } else {
                System.err.println("Destinatario non trovato nel database.");
                return false;
            }
            
            // Chiamata alla procedura di popolamento per la tabella ponte
            String sqlCall = "CALL Aggiungi_Partecipazione(?, ?)";
            try (CallableStatement cstmt = conn.prepareCall(sqlCall)) {
                cstmt.setInt(1, matricolaDestinatario);
                cstmt.setInt(2, nProgetto);
                cstmt.execute();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Errore durante l'invito dello studente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 6. CREA NUOVA ATTIVITÀ
     * Invia tutti i dati alla procedura Inserisci_Attivita. Il DB gestisce la suddivisione 
     * gerarchica tra Documentazione e Sviluppo in base alla 'tipologia'.
     */
    public boolean creaNuovaAttivita(String descrizione, LocalDate dataCreazione, LocalDate scadenza, double statoAvanzamento, 
                                     int matricolaCreatore, int nProgetto, String tipologia, 
                                     String tipoDocumento, String linkDocumento, String tipoLinguaggio) {
        
        // 10 parametri esatti della stored procedure
        String sql = "CALL Inserisci_Attivita(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, descrizione);
            cstmt.setDate(2, Date.valueOf(dataCreazione));
            
            if (scadenza != null) cstmt.setDate(3, Date.valueOf(scadenza));
            else cstmt.setNull(3, Types.DATE);
            
            cstmt.setDouble(4, statoAvanzamento);
            cstmt.setInt(5, matricolaCreatore);
            cstmt.setInt(6, nProgetto);
            cstmt.setString(7, tipologia); // Il discriminante ('Documentazione' o 'Sviluppo')
            
            if (tipoDocumento != null) cstmt.setString(8, tipoDocumento);
            else cstmt.setNull(8, Types.VARCHAR);
            
            if (linkDocumento != null) cstmt.setString(9, linkDocumento);
            else cstmt.setNull(9, Types.VARCHAR);
            
            if (tipoLinguaggio != null) cstmt.setString(10, tipoLinguaggio);
            else cstmt.setNull(10, Types.VARCHAR);

            cstmt.execute();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Errore SQL nella creazione dell'attività: " + e.getMessage());
            return false;
        }
    }

    /**
     * 7. AGGIORNA STATO ATTIVITÀ
     */
    public boolean aggiornaStatoAttivita(int idAttivita, double nuovoStato) {
        String query = "UPDATE Attivita SET Stato_avanzamento = ? WHERE ID_Attivita = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, nuovoStato);
            pstmt.setInt(2, idAttivita);
            int righeModificate = pstmt.executeUpdate();
            return righeModificate > 0;
        } catch (SQLException e) {
            System.err.println("Errore nell'aggiornamento dell'attività: " + e.getMessage());
            return false;
        }
    }

    /**
     * 8. CARICA FILE
     * Permette l'inserimento polimorfico dei file in base al target (Sorgente o Documentazione).
     */
    public boolean caricaFile(int idAttivita, String pathONomeFile, String estensione, double dimensioni, String tipoFile) {
        try {
            if ("Sviluppo".equalsIgnoreCase(tipoFile)) { // Per Entità File_Sorgente
                String sql = "CALL Inserisci_File_Sorgente(?, ?, ?, CURRENT_DATE, ?, ?)";
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.setString(1, pathONomeFile); // Primary Key (Path)
                    cstmt.setString(2, "FileSorg_" + idAttivita); 
                    cstmt.setDouble(3, dimensioni);
                    cstmt.setString(4, estensione);
                    cstmt.setInt(5, idAttivita); // FK
                    cstmt.execute();
                }
            } else { // Per Entità Altro_File
                String sql = "CALL Inserisci_Altro_File(?, ?, CURRENT_DATE, ?, FALSE, ?, ?)";
                try (CallableStatement cstmt = conn.prepareCall(sql)) {
                    cstmt.setString(1, pathONomeFile); // Nome File
                    cstmt.setDouble(2, dimensioni);
                    cstmt.setString(3, "Allegato"); 
                    cstmt.setString(4, estensione);
                    cstmt.setInt(5, idAttivita); // FK
                    cstmt.execute();
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Errore nel caricamento del file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 9. ELIMINA PROGETTO
     * Rimuove il progetto. Il database lo eliminerà insieme ad attività, file e membri
     * per effetto dei vincoli ON DELETE CASCADE.
     */
    public boolean eliminaProgetto(int nProgetto) {
        String sql = "DELETE FROM Progetto WHERE N_Progetto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nProgetto);
            int righe = pstmt.executeUpdate();
            return righe > 0;
        } catch (SQLException e) {
            System.err.println("Errore nell'eliminazione del progetto: " + e.getMessage());
            return false;
        }
    }
}