package it.unina.taskboard.boundary;

import it.unina.taskboard.control.TaskboardControl;
import it.unina.taskboard.entity.Altro_File;
import it.unina.taskboard.entity.Attivita;
import it.unina.taskboard.entity.Documentazione;
import it.unina.taskboard.entity.File_Sorgente;
import it.unina.taskboard.entity.Progetto;
import it.unina.taskboard.entity.Studente;
import it.unina.taskboard.entity.Sviluppo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;

public class CreaAttivitaFrame extends JDialog {
	
	// dati principali che collegano questa finestra al resto del programma
    private TaskboardControl control;
    private Studente utente;
    private Progetto progettoCorrente;
    private DettagliAttivitaFrame framePadre;

    // elementi grafici in cui l'utente scriverà o sceglierà i dati
    private JTextArea txtDescrizione;
    private JSpinner spinDataScadenza; 
    private JCheckBox chkScadenza;     
    private JSpinner spinStato;
    private JComboBox<String> comboTipologia;
    
    // componenti per far cambiare la schermata in base alle scelte dell'utente
    private JPanel pnlDettagliSpecifici;
    private CardLayout cardLayout;
    
    // elementi per gestire i file allegati e i documenti
    private DefaultTableModel tableModelFile;
    private JTextField txtTipoDoc;
    private JTextField txtLinkDoc;
    private JButton btnCreaOSalva;
    
    private Object fileTemporaneo; 
    private Attivita attivitaInLettura = null; 

    // fa comparire la finestra a schermo
    public CreaAttivitaFrame(TaskboardControl control, Studente utente, Progetto progetto, DettagliAttivitaFrame framePadre) {
        super(framePadre, "TaskBoard - Gestione Attività", true); // la finestra blocca le altre finché non la chiudi
        this.control = control;
        this.utente = utente;
        this.progettoCorrente = progetto;
        this.framePadre = framePadre;

        setSize(700, 500);
        setLocationRelativeTo(framePadre);
        setLayout(new BorderLayout());

        // divido la parte centrale in due colonne distanziate
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // colonna di sinistra: dedicata solo alla descrizione dell'attività
        JPanel colSinistra = new JPanel(new BorderLayout());
        colSinistra.add(new JLabel("Descrizione", SwingConstants.CENTER), BorderLayout.NORTH);
        txtDescrizione = new JTextArea();
        colSinistra.add(new JScrollPane(txtDescrizione), BorderLayout.CENTER);

        // colonna di destra: conterrà scadenze, stato e allegati
        JPanel colDestra = new JPanel();
        colDestra.setLayout(new BoxLayout(colDestra, BoxLayout.Y_AXIS));

        // zona per scegliere la data di scadenza
        JPanel pnlScadenza = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkScadenza = new JCheckBox("Scadenza:");
        chkScadenza.setSelected(true); // la scadenza è attiva di default
        spinDataScadenza = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorScadenza = new JSpinner.DateEditor(spinDataScadenza, "yyyy-MM-dd");
        spinDataScadenza.setEditor(editorScadenza);
        pnlScadenza.add(chkScadenza); 
        pnlScadenza.add(spinDataScadenza);
        
        // se l'utente toglie la spunta, disabilitiamo la scelta della data
        chkScadenza.addActionListener(e -> spinDataScadenza.setEnabled(chkScadenza.isSelected()));
        
        // zona per indicare a che punto è l'attività
        JPanel pnlStato = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinStato = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        pnlStato.add(new JLabel("Stato Avanzamento (%):")); pnlStato.add(spinStato);

        // zona per scegliere se è un'attività di sviluppo o di documentazione
        JPanel pnlTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboTipologia = new JComboBox<>(new String[]{"Sviluppo", "Documentazione"});
        pnlTipo.add(new JLabel("Tipologia:")); pnlTipo.add(comboTipologia);

        // prepariamo i pannelli che cambieranno in base al tipo scelto
        cardLayout = new CardLayout();
        pnlDettagliSpecifici = new JPanel(cardLayout);
        
        // schermata vuota per lo sviluppo
        JPanel cardSviluppo = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // schermata con i campi in più per la documentazione
        JPanel cardDoc = new JPanel(new GridLayout(2, 1)); 
        JPanel pnlTipoDoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTipoDoc = new JTextField(10);
        pnlTipoDoc.add(new JLabel("Tipo Doc:")); pnlTipoDoc.add(txtTipoDoc);
        
        JPanel pnlLinkDoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtLinkDoc = new JTextField(10);
        pnlLinkDoc.add(new JLabel("Link Doc:")); pnlLinkDoc.add(txtLinkDoc);
        cardDoc.add(pnlTipoDoc); cardDoc.add(pnlLinkDoc);

        // aggiungiamo i due pannelli intercambiabili
        pnlDettagliSpecifici.add(cardSviluppo, "Sviluppo");
        pnlDettagliSpecifici.add(cardDoc, "Documentazione");

        // zona per allegare i file
        JPanel pnlFileHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCarica = new JButton("Allega File");
        pnlFileHeader.add(btnCarica);
        
        // tabella per vedere i file che abbiamo caricato
        tableModelFile = new DefaultTableModel(new String[]{"Nome", "Estens.", "Dim."}, 0);
        JTable tblFile = new JTable(tableModelFile);

        colDestra.add(pnlScadenza); 
        colDestra.add(pnlStato); 
        colDestra.add(pnlTipo); 
        colDestra.add(pnlDettagliSpecifici);
        colDestra.add(pnlFileHeader); 
        colDestra.add(new JScrollPane(tblFile));

        // unisco le due colonne nel pannello centrale
        centerPanel.add(colSinistra); 
        centerPanel.add(colDestra);
        add(centerPanel, BorderLayout.CENTER);
        
        // sezione in basso con i bottoni per confermare o annulare
        // la creazione dell'attivita
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAnnulla = new JButton("Chiudi");
        btnCreaOSalva = new JButton("Crea Attività"); 
        bottomPanel.add(btnAnnulla);
        bottomPanel.add(btnCreaOSalva);
        add(bottomPanel, BorderLayout.SOUTH);

        // azioni dei bottoni
        btnAnnulla.addActionListener(e -> this.dispose());
        comboTipologia.addActionListener(e -> selezionaTipologia());
        btnCarica.addActionListener(e -> caricaFile());
        
        // se stiamo leggendo creiamo una nuova attività, altrimenti salviamo le modifiche
        btnCreaOSalva.addActionListener(e -> {
            if (attivitaInLettura == null) confermaCreazione();
            else salvaModifiche();
        });
    }

    // mostra i dati di un'attivita già esistente per poterla leggere
    public void impostaModalitaDettaglio(Attivita a) {
        this.attivitaInLettura = a;
        btnCreaOSalva.setText("Salva Modifiche");

        // inseriamo il testo della descrizione
        txtDescrizione.setText(a.getDescrizione());
        
        // controllo se c'è una data di scadenza e la impostiamo
        if (a.getDataScadenza() != null) {
            chkScadenza.setSelected(true);
            java.util.Date utilDate = java.util.Date.from(a.getDataScadenza().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            spinDataScadenza.setValue(utilDate);
        } else {
            chkScadenza.setSelected(false);
        }
        
        spinStato.setValue((int) a.getStatoAvanzamento());

        // blocchiamo le caselle in modo che l'utente non possa modificarle
        txtDescrizione.setEditable(false);
        chkScadenza.setEnabled(false);
        spinDataScadenza.setEnabled(false);
        comboTipologia.setEnabled(false);
        txtTipoDoc.setEditable(false);
        txtLinkDoc.setEditable(false);

        if (a instanceof Sviluppo) {
            comboTipologia.setSelectedItem("Sviluppo");
        } else if (a instanceof Documentazione) {
            comboTipologia.setSelectedItem("Documentazione");
        }

        // puliamo la tabella dei file
        tableModelFile.setRowCount(0);

     // riempiamo la tabella dei file in base al tipo di attività
        if (a instanceof Sviluppo) {
            Sviluppo svi = (Sviluppo) a;
            if (svi.getFileSorgenti() != null) {
                for (File_Sorgente fs : svi.getFileSorgenti()) {
                    tableModelFile.addRow(new Object[]{fs.getNomeFile(), fs.getEstensione(), fs.getDimensioni()});
                }
            }
        } else if (a instanceof Documentazione) {
            Documentazione doc = (Documentazione) a;
            txtTipoDoc.setText(doc.getTipoDocumento());
            txtLinkDoc.setText(doc.getLinkDocumento());
            if (doc.getFileAllegati() != null) {
                for (Altro_File af : doc.getFileAllegati()) {
                    tableModelFile.addRow(new Object[]{af.getNomeFile(), af.getEstensione(), af.getDimensioni()});
                }
            }
        }
    }
    
    // fa apparire i campi giusti quando cambi il tipo di attivita
    public void selezionaTipologia() {
        cardLayout.show(pnlDettagliSpecifici, (String) comboTipologia.getSelectedItem());
        // se stiamo creando una nuova attività, azzeriamo i file allegati quando cambiamo tipo
        if (attivitaInLettura == null) {
            tableModelFile.setRowCount(0);
            fileTemporaneo = null; 
        }
    }

    // apre una nuov finestra per farci cercare e scegliere un file
    public void caricaFile() {
        String tipo = (String) comboTipologia.getSelectedItem();
        JFileChooser fileChooser = new JFileChooser();

        // filtriamo i file che si possono scegliere in base alla tipologia
        if (tipo.equals("Sviluppo")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Codice", "java", "py", "sql", "js", "html"));
        } else {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Documenti", "pdf", "docx", "txt"));
        }

     // se l'utente sceglie un file, prendiamo i suoi dati e li mettiamo in tabella
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            String name = f.getName();
            int dot = name.lastIndexOf('.');
            String ext = (dot == -1) ? "" : name.substring(dot);
            double dim = Math.round((f.length() / 1024.0) * 100.0) / 100.0;

            tableModelFile.setRowCount(0);
            if (tipo.equals("Sviluppo")) {
                File_Sorgente fs = new File_Sorgente();
                fs.setNomeFile((dot == -1) ? name : name.substring(0, dot)); 
                fs.setEstensione(ext);
                fs.setDimensioni(dim);
                fileTemporaneo = fs;
                tableModelFile.addRow(new Object[]{fs.getNomeFile(), fs.getEstensione(), fs.getDimensioni()});
            } else {
                txtTipoDoc.setText(ext.replace(".", ""));
                Altro_File af = new Altro_File();
                af.setNomeFile((dot == -1) ? name : name.substring(0, dot));
                af.setEstensione(ext);
                af.setDimensioni(dim);
                fileTemporaneo = af;
                tableModelFile.addRow(new Object[]{af.getNomeFile(), af.getEstensione(), af.getDimensioni()});
            }
        }
    }

    // funzione per inserire i dati delle attivita
    public Attivita inserisciDatiAttivita() throws Exception {
        String desc = txtDescrizione.getText().trim();
        if(desc.isEmpty()) throw new Exception("Descrizione obbligatoria.");
        
        Attivita att = new Attivita();
        att.setDescrizione(desc);
        att.setDataCreazione(LocalDate.now());
        
        // legge la data di scadenza solo se l'opzione è stata spuntata
        if (chkScadenza.isSelected()) {
            java.util.Date utilDate = (java.util.Date) spinDataScadenza.getValue();
            LocalDate scadenza = utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            att.setDataScadenza(scadenza);
        } else {
            att.setDataScadenza(null);
        }
        
        att.setStatoAvanzamento(((Integer) spinStato.getValue()).doubleValue());
        att.setNProgetto(progettoCorrente.getNProgetto());
        return att;
    }

    // salva la nuova attività creata nel database
    public void confermaCreazione() {
        try {
            Attivita att = inserisciDatiAttivita();
            String tipo = (String) comboTipologia.getSelectedItem();
            String tipoDoc = tipo.equals("Documentazione") ? txtTipoDoc.getText() : null;
            String linkDoc = tipo.equals("Documentazione") ? txtLinkDoc.getText() : null;
            
            // se c'è un file di sviluppo cerchiamo di capire in che linguaggio è
            String linguaggio = null;
            if (tipo.equals("Sviluppo") && fileTemporaneo instanceof File_Sorgente) {
                linguaggio = ((File_Sorgente) fileTemporaneo).getEstensione().replace(".", "");
            }

            boolean successo = control.creaNuovaAttivita(
                att.getDescrizione(), att.getDataCreazione(), att.getDataScadenza(), 
                att.getStatoAvanzamento(), utente.getMatricola(), att.getNProgetto(),
                tipo, tipoDoc, linkDoc, linguaggio
            );
            
            // se il salvataggio è andato a buon fine ci occupiamo anche dei file allegati
            if (successo) {
                Progetto progettoAggiornato = control.apriDettaglioProgetto(progettoCorrente.getNProgetto());
                
                if (fileTemporaneo != null) {
                    int size = progettoAggiornato.getListaAttivita().size();
                    if (size > 0) {
                        int idNuovaAttivita = progettoAggiornato.getListaAttivita().get(size - 1).getIdAttivita();
                        
                        if (fileTemporaneo instanceof File_Sorgente) {
                            File_Sorgente fs = (File_Sorgente) fileTemporaneo;
                            String pathUnivoco = "/src/task_" + idNuovaAttivita + "/" + fs.getNomeFile() + fs.getEstensione();
                            control.caricaFile(idNuovaAttivita, pathUnivoco, fs.getEstensione(), fs.getDimensioni(), "Sviluppo");
                        } else if (fileTemporaneo instanceof Altro_File) {
                            Altro_File af = (Altro_File) fileTemporaneo;
                            control.caricaFile(idNuovaAttivita, af.getNomeFile(), af.getEstensione(), af.getDimensioni(), "Documentazione");
                        }
                    }
                }
                
                // mostra il messaggio di successo e chiude la finestra
                JOptionPane.showMessageDialog(this, "Attività e file salvati con successo!");
                this.dispose();
                framePadre.ricaricaDati(); 
                
            } else {
                JOptionPane.showMessageDialog(this, "Errore SQL nell'inserimento dell'attività.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
        	// se manca qualcosa (come la descrizione), avviso l'utente
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // salva i cambiamenti fatti ad un'attività che già esisteva
    public void salvaModifiche() {
        try {
            double nuovoStato = ((Integer) spinStato.getValue()).doubleValue();
            
            // aggiorniamo lo stato di avanzamento
            boolean successo = control.aggiornaStatoAttivita(attivitaInLettura.getIdAttivita(), nuovoStato);
            
            // se abbiamo allegato un nuovo file aggiorniamo anche quello
            if (fileTemporaneo != null) {
                if (fileTemporaneo instanceof File_Sorgente) {
                    File_Sorgente fs = (File_Sorgente) fileTemporaneo;
                    String pathUnivoco = "/src/task_" + attivitaInLettura.getIdAttivita() + "/" + fs.getNomeFile() + fs.getEstensione();
                    control.caricaFile(attivitaInLettura.getIdAttivita(), pathUnivoco, fs.getEstensione(), fs.getDimensioni(), "Sviluppo");
                } else if (fileTemporaneo instanceof Altro_File) {
                    Altro_File af = (Altro_File) fileTemporaneo;
                    control.caricaFile(attivitaInLettura.getIdAttivita(), af.getNomeFile(), af.getEstensione(), af.getDimensioni(), "Documentazione");
                }
            }
            
            // se è andato tutto bene mostra il messaggio e chiude
            if (successo) {
                JOptionPane.showMessageDialog(this, "Attività aggiornata.");
                this.dispose();
                framePadre.ricaricaDati(); 
            } else {
                JOptionPane.showMessageDialog(this, "Errore DB durante l'aggiornamento.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore Modifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}