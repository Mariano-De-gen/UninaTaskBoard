package it.unina.taskboard.boundary;

import it.unina.taskboard.control.TaskboardControl;
import it.unina.taskboard.entity.Progetto;
import it.unina.taskboard.entity.Studente;
import it.unina.taskboard.entity.TipoProgetto;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CreaNuovoProgettoFrame extends JFrame {
    private TaskboardControl control;
    private Studente utente;
    private JFrame dashboardPadre;

    // componenti grafici per i dati base
    private JTextField txtNome;
    private JTextArea txtDescrizione;
    private JComboBox<TipoProgetto> comboTipo;
    
    // componenti per gestire la lista degli inviti
    private DefaultListModel<String> listModelInvitati;
    private JList<String> listInvitati;
    private JTextField txtEmailInvito;
    
    // componenti dinamici (vengono evidenziati in base al tipo di progetto)
    private JPanel panelDettagli;
    private JTextField txtNoteCollab;
    private JTextField txtEsame;
    private JSpinner spinDataEsame;
    private JTextField txtNomeApp;

    public CreaNuovoProgettoFrame(TaskboardControl control, Studente utente, JFrame dashboardPadre) {
        this.control = control;
        this.utente = utente;
        this.dashboardPadre = dashboardPadre;

        // configurazione della finestra
        setTitle("TaskBoard - Crea Nuovo Progetto");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // sezione superiore con il titolo
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTitolo = new JLabel("Crea Nuovo Progetto   ");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(lblTitolo);
        add(topPanel, BorderLayout.NORTH);

        // sidebar con un pulsante per tornare indietro alla dashboard
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        sidebar.setPreferredSize(new Dimension(140, 0));
        JButton btnMenuDashboard = new JButton("Dashboard >");
        sidebar.add(btnMenuDashboard);
        add(sidebar, BorderLayout.WEST);

        // sezione centrale divisa in due colonne
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // colonna di sinistra con inserimento del nome e della descrizione del progetto
        JPanel colSinistra = new JPanel(new BorderLayout(0, 10));
        JPanel pnlNome = new JPanel(new BorderLayout());
        pnlNome.add(new JLabel("Nome Progetto: "), BorderLayout.WEST);
        txtNome = new JTextField();
        pnlNome.add(txtNome, BorderLayout.CENTER);
        
        JPanel pnlDesc = new JPanel(new BorderLayout());
        pnlDesc.add(new JLabel("Descrizione:", SwingConstants.CENTER), BorderLayout.NORTH);
        txtDescrizione = new JTextArea();
        txtDescrizione.setLineWrap(true); // manda il testo a capo automaticamente
        pnlDesc.add(new JScrollPane(txtDescrizione), BorderLayout.CENTER);

        colSinistra.add(pnlNome, BorderLayout.NORTH);
        colSinistra.add(pnlDesc, BorderLayout.CENTER);

        // colonna destra con membri, tipo e dettagli specifici del progetto
        JPanel colDestra = new JPanel();
        colDestra.setLayout(new BoxLayout(colDestra, BoxLayout.Y_AXIS));
        
        // riquadro per aggiungere colleghi tramite email
        JPanel boxMembri = new JPanel(new BorderLayout(5, 5));
        boxMembri.setBorder(BorderFactory.createTitledBorder("Lista Membri (Email)"));
        boxMembri.setMaximumSize(new Dimension(400, 150));
        
        JPanel pnlInputMembro = new JPanel(new BorderLayout());
        txtEmailInvito = new JTextField();
        JButton btnAggiungiMembro = new JButton("+");
        pnlInputMembro.add(txtEmailInvito, BorderLayout.CENTER);
        pnlInputMembro.add(btnAggiungiMembro, BorderLayout.EAST);
        
        listModelInvitati = new DefaultListModel<>();
        listInvitati = new JList<>(listModelInvitati);
        boxMembri.add(pnlInputMembro, BorderLayout.NORTH);
        boxMembri.add(new JScrollPane(listInvitati), BorderLayout.CENTER);

        // menu a tendina per scegliere il tipo di progetto
        JPanel boxTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boxTipo.add(new JLabel("Tipo Progetto:"));
        comboTipo = new JComboBox<>(TipoProgetto.values());
        boxTipo.add(comboTipo);

        // riquadro per i dati specifici
        panelDettagli = new JPanel(new GridLayout(4, 2, 5, 5));
        panelDettagli.setBorder(BorderFactory.createTitledBorder("Dettagli Specifici"));
        
        panelDettagli.add(new JLabel("Note Collab.:")); 
        txtNoteCollab = new JTextField(); 
        panelDettagli.add(txtNoteCollab);
        
        panelDettagli.add(new JLabel("Esame:")); 
        txtEsame = new JTextField(); 
        panelDettagli.add(txtEsame);
        
        // creazione di uno spinner per la data esame
        panelDettagli.add(new JLabel("Data Esame:")); 
        spinDataEsame = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorData = new JSpinner.DateEditor(spinDataEsame, "YYYY-MM-DD");
        spinDataEsame.setEditor(editorData);
        panelDettagli.add(spinDataEsame);
        
        panelDettagli.add(new JLabel("Nome App:")); 
        txtNomeApp = new JTextField(); 
        panelDettagli.add(txtNomeApp);

        // assembliamo la colonna destra
        colDestra.add(boxMembri);
        colDestra.add(Box.createVerticalStrut(15));
        colDestra.add(boxTipo);
        colDestra.add(Box.createVerticalStrut(15));
        colDestra.add(panelDettagli);

        // inseriamo colonna sinistra e destra al centro della finestra
        centerPanel.add(colSinistra);
        centerPanel.add(colDestra);
        add(centerPanel, BorderLayout.CENTER);

        // bottoni per la creazione e l'annullamento del progetto
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAnnulla = new JButton("Annulla Progetto");
        btnAnnulla.setForeground(Color.RED); 
        JButton btnCrea = new JButton("Crea Progetto");
        bottomPanel.add(btnAnnulla); bottomPanel.add(btnCrea);
        add(bottomPanel, BorderLayout.SOUTH);

        // all'avvio, assicuriamoci che siano attivi solo i campi giusti
        selezionaTipoProgetto(); 

        // azioni dei pulsanti
        comboTipo.addActionListener(e -> selezionaTipoProgetto()); // aggiorna i campi se cambi tipo
        btnMenuDashboard.addActionListener(e -> annullaOperazione());
        btnAnnulla.addActionListener(e -> annullaOperazione());
        btnAggiungiMembro.addActionListener(e -> apriInvitaStudente());
        btnCrea.addActionListener(e -> confermaCreazione());
    }
    
    // memorizza tutto ciò che l'utente ha scritto nel form
    public Object[] memorizzaDatiProgetto() throws Exception {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) throw new Exception("Il nome del progetto è obbligatorio.");
        
        String desc = txtDescrizione.getText().trim();
        TipoProgetto tipo = (TipoProgetto) comboTipo.getSelectedItem();
        
        // salviamo i dati specifici SOLO se il campo era abilitato
        String note = txtNoteCollab.isEnabled() && !txtNoteCollab.getText().isEmpty() ? txtNoteCollab.getText().trim() : null;
        String esame = txtEsame.isEnabled() && !txtEsame.getText().isEmpty() ? txtEsame.getText().trim() : null;
        
        LocalDate data = null;
        if (spinDataEsame.isEnabled()) {
            java.util.Date utilDate = (java.util.Date) spinDataEsame.getValue();
            data = utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        
        String app = txtNomeApp.isEnabled() && !txtNomeApp.getText().isEmpty() ? txtNomeApp.getText().trim() : null;
        
        // ritorna tutto
        return new Object[]{nome, desc, tipo, note, esame, data, app};
    }

    // evidenzia i campi specifici in base a cosa c'è scritto nel menu a tendina
    public void selezionaTipoProgetto() {
        TipoProgetto tipo = (TipoProgetto) comboTipo.getSelectedItem();
        txtNoteCollab.setEnabled(tipo == TipoProgetto.Progetti_Gruppo);
        txtEsame.setEnabled(tipo == TipoProgetto.Preparazione_Esame);
        spinDataEsame.setEnabled(tipo == TipoProgetto.Preparazione_Esame); 
        txtNomeApp.setEnabled(tipo == TipoProgetto.Sviluppo_APP);
        
        // svuota il testo dei campi che sono stati disattivati
        if (!txtNoteCollab.isEnabled()) txtNoteCollab.setText("");
        if (!txtEsame.isEnabled()) txtEsame.setText("");
        if (!txtNomeApp.isEnabled()) txtNomeApp.setText("");
    }

    // prende l'email scritta e la aggiunge alla lista a schermo
    public void apriInvitaStudente() {
        String email = txtEmailInvito.getText().trim();
        if(!email.isEmpty() && email.contains("@")) {
            listModelInvitati.addElement(email); // aggiunge alla lista
            txtEmailInvito.setText(""); // svuota la casella di testo
        } else {
            JOptionPane.showMessageDialog(this, "Inserisci un'email valida.");
        }
    }
    
    // procedura principale: crea il progetto e aggiunge i colleghi invitati
    public void confermaCreazione() {
        try {
        	// prende i dati
            Object[] dati = memorizzaDatiProgetto(); 
            // salviamo il progetto nel database
            boolean successo = control.creaNuovoProgetto(
                (String)dati[0], (String)dati[1], (TipoProgetto)dati[2], 
                utente.getMatricola(), (String)dati[3], (String)dati[4], 
                (LocalDate)dati[5], (String)dati[6]
            );
            
            // trova l'ID del progetto appena creato
            if (successo) {
                List<Progetto> mieiProgetti = control.ottieniListaProgetti(utente.getMatricola());
                int idUltimoProgetto = -1;
                for (Progetto p : mieiProgetti) {
                    if (p.getNProgetto() > idUltimoProgetto) idUltimoProgetto = p.getNProgetto();
                }

                // per ogni email nella lista, crea un invito legato a questo nuovo progetto
                StringBuilder reportInviti = new StringBuilder();
                for (int i = 0; i < listModelInvitati.getSize(); i++) {
                    String emailDest = listModelInvitati.getElementAt(i);
                    boolean invitato = control.invitaStudente(utente.getEmail(), emailDest, idUltimoProgetto);
                    if(!invitato) reportInviti.append("Fallito invito per: ").append(emailDest).append("\n");
                }
                
                // mostra messaggio di successo e torna alla dashboard
                JOptionPane.showMessageDialog(this, "Progetto creato!\n" + reportInviti.toString(), "Successo", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                dashboardPadre.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Errore SQL nel salvataggio.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
        }
    }

    // chiude tutto e torna indietro
    public void annullaOperazione() {
        this.dispose();
        dashboardPadre.setVisible(true);
    }
}