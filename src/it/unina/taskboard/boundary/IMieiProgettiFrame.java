package it.unina.taskboard.boundary;

import it.unina.taskboard.control.TaskboardControl;
import it.unina.taskboard.entity.Progetto;
import it.unina.taskboard.entity.Studente;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class IMieiProgettiFrame extends JFrame {
    private TaskboardControl control;
    private Studente utente;
    private JFrame dashboardPadre; // ci serve per riaprire la finestra precedente senza crearne una nuova
    private JTable tabellaProgetti;
    private DefaultTableModel tableModel;

    public IMieiProgettiFrame(TaskboardControl control, Studente utente, JFrame dashboardPadre) {
        this.control = control;
        this.utente = utente;
        this.dashboardPadre = dashboardPadre;

        // configurazione della finestra principale
        setTitle("TaskBoard - I MIEI PROGETTI");
        setSize(850, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // mostriamo nella sezione superiore il nome dell'utente
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblLogout = new JLabel(utente.getNome() + " " + utente.getCognome());
        lblLogout.setForeground(Color.BLUE);
        topPanel.add(lblLogout);
        add(topPanel, BorderLayout.NORTH);

        // creiamo una sidebar per la navigazione
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        sidebar.setPreferredSize(new Dimension(130, 0));
        JButton btnMenuDashboard = new JButton("Dashboard >");
        JButton btnMenuProgetti = new JButton("I Miei Progetti >");
        btnMenuProgetti.setBackground(Color.LIGHT_GRAY); // evidenziamo dove ci troviamo attualmente

        // rendiamo i bottoni larghi quanto tutta la sidebar
        btnMenuDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnMenuDashboard.getMinimumSize().height));
        btnMenuProgetti.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnMenuProgetti.getMinimumSize().height));

        sidebar.add(btnMenuDashboard);
        sidebar.add(btnMenuProgetti);
        add(sidebar, BorderLayout.WEST);

        // mostra il titolo e tabella dei progetti
        JPanel centerPanel = new JPanel(new BorderLayout());
        JLabel lblTitolo = new JLabel("I MIEI PROGETTI", SwingConstants.CENTER);
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 24));
        centerPanel.add(lblTitolo, BorderLayout.NORTH);

        // creiamo la struttura della tabella
        String[] colonne = {"Numero Progetto", "Nome Progetto", "Descrizione", "Tipo Progetto", ""};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabellaProgetti = new JTable(tableModel);
        
        // coloriamo la colonna 2 di blu per far capire che si deve cliccare li
        // se l'utente vuole aprire la descrizione di quel progetto
        tabellaProgetti.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.BLUE);
                return c;
            }
        });
        
        // coloriamo "Elimina" di rosso per segnalare l'azione di cancellazione
        tabellaProgetti.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.RED);
                return c;
            }
        });

        centerPanel.add(new JScrollPane(tabellaProgetti), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // gestiamo i clic sulla tabella
        tabellaProgetti.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	// troviamo la riga e la colonna che l'utente ha cliccato
                int col = tabellaProgetti.columnAtPoint(e.getPoint());
                int row = tabellaProgetti.rowAtPoint(e.getPoint());
                
                // se ha cliccato una riga valida
                if (row != -1 && e.getClickCount() == 1) {
                	// recuperiamo l'ID del progetto dalla primissima colonna (indice 0)
                    int idProgetto = (int) tabellaProgetti.getValueAt(row, 0);
                    if (col == 2) { // cliccato su "Apri"
                        apriDettaglioProgetto(idProgetto);
                    }
                    if (col == 4) { // cliccato su "Elimina"
                        eliminaProgetto(idProgetto);
                    }
                }
            }
        });
        
        // pulsante per tornare alla Dashboard
        btnMenuDashboard.addActionListener(e -> {
            this.dispose();
            dashboardPadre.setVisible(true);
        });

        mostraListaProgetti();
    }

    // chiede al database tutti i progetti e li inserisce riga per riga nella tabella    
    public void mostraListaProgetti() {
        List<Progetto> progetti = control.ottieniListaProgetti(utente.getMatricola());
        tableModel.setRowCount(0); // svuota la tabella prima di riempirla
        
        for (Progetto p : progetti) {
            Object[] riga = {p.getNProgetto(), p.getNomeProg(), "Apri " + p.getNomeProg(), p.getTipoDiProgetto().name(), "Elimina"};
            tableModel.addRow(riga); 
        }
    }
    
    // apre la finestra con i dettagli e le attività del singolo progetto
    public void apriDettaglioProgetto(int n_progetto) {
        Progetto progettoCompleto = control.apriDettaglioProgetto(n_progetto);
        if (progettoCompleto != null) {
            DettagliAttivitaFrame frameDettagli = new DettagliAttivitaFrame(control, utente, progettoCompleto, this);
            frameDettagli.setVisible(true);
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento dei dati del progetto dal Database.", "Errore SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Mostra un messaggio di avviso e se confermato cancella il progetto
    public void eliminaProgetto(int n_progetto) {
        int conferma = JOptionPane.showConfirmDialog(this, 
            "Vuoi davvero eliminare questo progetto? Tutte le attività e i file verranno distrutti irreversibilmente.", 
            "Attenzione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (conferma == JOptionPane.YES_OPTION) {
            if (control.eliminaProgetto(n_progetto)) {
                JOptionPane.showMessageDialog(this, "Progetto eliminato con successo.");
                mostraListaProgetti(); // Aggiorna la vista dopo aver cancellato
            } else {
                JOptionPane.showMessageDialog(this, "Errore durante l'eliminazione SQL.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}