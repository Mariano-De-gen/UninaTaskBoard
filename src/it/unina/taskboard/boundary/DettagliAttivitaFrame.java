package it.unina.taskboard.boundary;

import it.unina.taskboard.control.TaskboardControl;
import it.unina.taskboard.entity.Attivita;
import it.unina.taskboard.entity.Progetto;
import it.unina.taskboard.entity.Studente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DettagliAttivitaFrame extends JFrame {
    private TaskboardControl control;
    private Studente utente;
    private Progetto progettoCorrente; 
    private JFrame framePadre;
    
    // elementi grafici che dovremo aggiornare dinamicamente
    private DefaultListModel<String> listModelMembri;
    private JTextField txtEmailInvito;
    private DefaultTableModel tableModelAttivita;
    private JTable tblAttivita;

    public DettagliAttivitaFrame(TaskboardControl control, Studente utente, Progetto progetto, JFrame framePadre) {
        this.control = control;
        this.utente = utente;
        this.progettoCorrente = progetto;
        this.framePadre = framePadre;

        // setup della finestra
        setTitle("TaskBoard - Dettagli Progetto");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // nome dell'utente loggato
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(new JLabel(utente.getNome() + " " + utente.getCognome()));
        add(topPanel, BorderLayout.NORTH);

        // sidebar con il pulsante per tornare alla home
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(130, 0));
        JButton btnMenuDashboard = new JButton("Dashboard >");
        sidebar.add(btnMenuDashboard);
        add(sidebar, BorderLayout.WEST);

        // pannello principale
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // titolo e info generali del progetto
        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.add(new JLabel(progettoCorrente.getNomeProg(), SwingConstants.CENTER));
        headerPanel.add(new JLabel("Numero Progetto: " + progettoCorrente.getNProgetto(), SwingConstants.CENTER));
        headerPanel.add(new JLabel("Tipo Progetto: " + progettoCorrente.getTipoDiProgetto().name(), SwingConstants.CENTER));
        
        // descrizione a sinistra e lista membri a destra
        JPanel middlePanel = new JPanel(new BorderLayout(20, 0));
        
        // pannello della descrizione
        JPanel pnlDesc = new JPanel(new BorderLayout());
        pnlDesc.setBorder(BorderFactory.createTitledBorder("Descrizione"));
        JTextArea txtDesc = new JTextArea(progettoCorrente.getDescrizione());
        txtDesc.setEditable(false); // sola lettura
        pnlDesc.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
        
        // pannello lista membri e form di invito
        JPanel pnlMembri = new JPanel(new BorderLayout(0, 5));
        pnlMembri.setBorder(BorderFactory.createTitledBorder("Membri del Gruppo"));
        listModelMembri = new DefaultListModel<>();
        pnlMembri.add(new JScrollPane(new JList<>(listModelMembri)), BorderLayout.CENTER);
        
        JPanel pnlInvito = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtEmailInvito = new JTextField(12);
        JButton btnInserisciMembro = new JButton("Inserisci");
        pnlInvito.add(new JLabel("Invita (Email):"));
        pnlInvito.add(txtEmailInvito);
        pnlInvito.add(btnInserisciMembro);
        pnlMembri.add(pnlInvito, BorderLayout.SOUTH);
        
        middlePanel.add(pnlDesc, BorderLayout.CENTER);
        middlePanel.add(pnlMembri, BorderLayout.EAST);

        // panello della tabella con la lista delle attivita
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 5));
        JPanel pnlTaskHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuovaAttivita = new JButton("Inserisci nuova Attività");
        pnlTaskHeader.add(btnNuovaAttivita);
        
        // creiamo la tabella delle attività in sola lettura
        tableModelAttivita = new DefaultTableModel(new String[]{"Id", "Descrizione", "Data Creazione", "Data Scadenza", "Stato"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblAttivita = new JTable(tableModelAttivita);
        
        bottomPanel.add(pnlTaskHeader, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(tblAttivita), BorderLayout.CENTER);

        JPanel topHalf = new JPanel(new BorderLayout());
        topHalf.add(headerPanel, BorderLayout.NORTH);
        topHalf.add(middlePanel, BorderLayout.CENTER);
        centerPanel.add(topHalf, BorderLayout.NORTH);
        centerPanel.add(bottomPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // riempiamo le liste con i dati presi dal database
        mostraListaMembri();
        mostraTabellaAttivita();

        // azione dei bottoni
        btnMenuDashboard.addActionListener(e -> chiudiETornaAllaDashboard());
        btnInserisciMembro.addActionListener(e -> apriInvitaStudente());
        btnNuovaAttivita.addActionListener(e -> apriCreaAttivita());
        
        // gestiamo il doppio clic sulle righe della tabella delle attività
        tblAttivita.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblAttivita.getSelectedRow();
                    if (row != -1) {
                        Attivita selezionata = progettoCorrente.getListaAttivita().get(row);
                        CreaAttivitaFrame dialogDettaglio = new CreaAttivitaFrame(control, utente, progettoCorrente, DettagliAttivitaFrame.this);
                        dialogDettaglio.impostaModalitaDettaglio(selezionata);
                        dialogDettaglio.setVisible(true);
                    }
                }
            }
        });
    }

    // aggiorna i dati a schermo riprendendoli dal database
    public void ricaricaDati() {
        this.progettoCorrente = control.apriDettaglioProgetto(progettoCorrente.getNProgetto());
        mostraListaMembri();
        mostraTabellaAttivita();
    }

    // mostra l'elenco degli studenti che partecipano al progetto
    public void mostraListaMembri() {
        listModelMembri.clear(); // pulisce la lista prima di riempirla
        List<Studente> membri = progettoCorrente.getMembri();
        if (membri != null && !membri.isEmpty()) {
            for (Studente s : membri) {
                listModelMembri.addElement(s.getNome() + " " + s.getCognome());
            }
        } else {
            listModelMembri.addElement("Nessun membro aggiuntivo");
        }
    }

    // riempie la tabella con tutte le attività
    public void mostraTabellaAttivita() {
        tableModelAttivita.setRowCount(0); // svuota la tabella
        if (progettoCorrente.getListaAttivita() != null) {
            for (Attivita a : progettoCorrente.getListaAttivita()) {
                tableModelAttivita.addRow(new Object[]{
                    a.getIdAttivita(), 
                    a.getDescrizione(), 
                    a.getDataCreazione(), 
                    a.getDataScadenza(), 
                    a.getStatoAvanzamento() + "%"
                });
            }
        }
    }

    // prende l'email scritta e manda l'invito al nuovo studente
    public void apriInvitaStudente() {
        String emailDest = txtEmailInvito.getText().trim();
        if(control.invitaStudente(utente.getEmail(), emailDest, progettoCorrente.getNProgetto())) {
            JOptionPane.showMessageDialog(this, "Studente invitato con successo.");
            txtEmailInvito.setText(""); // svuota la casella di testo
            ricaricaDati(); // ricarica la pagina con i dati aggiornati
        } else {
            JOptionPane.showMessageDialog(this, "Errore durante l'invito.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // apre la finestra per creare una nuova attività
    public void apriCreaAttivita() {
        CreaAttivitaFrame dialogCrea = new CreaAttivitaFrame(control, utente, progettoCorrente, this);
        dialogCrea.setVisible(true);
    }
    
    // chiude questa finestra e torna alla schermata principale
    public void chiudiETornaAllaDashboard() {
        this.dispose();
        if (framePadre != null) framePadre.dispose(); 
        new DashboardFrame(control, utente).setVisible(true); 
    }
}