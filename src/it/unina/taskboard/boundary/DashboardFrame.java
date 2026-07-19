package it.unina.taskboard.boundary;

import it.unina.taskboard.control.TaskboardControl;
import it.unina.taskboard.entity.Studente;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private TaskboardControl control;
    private Studente utente;

    public DashboardFrame(TaskboardControl control, Studente utente) {
    	// salviamo il controller e l'utente per sapere chi ha fatto l'accesso
        this.control = control;
        this.utente = utente;

        // configurazione generale della finestra
        setTitle("TaskBoard - DashBoard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centra sullo schermo
        setLayout(new BorderLayout()); // divide la finestra in sezioni

        // sezione superiore con il testo di logout cliccabile per uscire
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblLogout = new JLabel("Logout: " + utente.getNome() + " " + utente.getCognome());
        lblLogout.setForeground(Color.BLUE);
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(lblLogout);
        add(topPanel, BorderLayout.NORTH);

        // sezione centrale in cui creiamo il titolo e la colonna dei bottoni
        JLabel lblTitolo = new JLabel("DashBoard", SwingConstants.CENTER);
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitolo.setAlignmentX(Component.CENTER_ALIGNMENT); 

        // usiamo BoxLayout per impilare gli elementi uno sotto l'altro
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        
        Dimension buttonSize = new Dimension(220, 45);
        
        JButton btnMieiProgetti = new JButton("I Miei Progetti");
        btnMieiProgetti.setFont(new Font("Arial", Font.PLAIN, 20));
        btnMieiProgetti.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMieiProgetti.setMaximumSize(buttonSize); 
        
        JButton btnCreaProgetto = new JButton("Crea Progetto");
        btnCreaProgetto.setFont(new Font("Arial", Font.PLAIN, 20));
        btnCreaProgetto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCreaProgetto.setMaximumSize(buttonSize); 

        // aggiungiamo titolo e bottoni al pannello centrale
        buttonPanel.add(Box.createVerticalStrut(40));
        buttonPanel.add(lblTitolo);
        buttonPanel.add(Box.createVerticalStrut(40));
        buttonPanel.add(btnMieiProgetti);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnCreaProgetto);

        add(buttonPanel, BorderLayout.CENTER);

        btnCreaProgetto.addActionListener(e -> apriCreaProgetto());
        btnMieiProgetti.addActionListener(e -> apriListaProgetti());
        
        // listener per il Logout: chiude la Dashboard e riapre il Login
        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose(); // chiude questa finestra
                new LoginFrame(control).mostraFormLogin(); // riapre il login
            }
        });
    }
    
    // nasconde la Dashboard e apre la lista dei progetti
    public void apriListaProgetti() {
        IMieiProgettiFrame frameProgetti = new IMieiProgettiFrame(control, utente, this);
        frameProgetti.setVisible(true);
        this.setVisible(false);
    }

    // apre una finestra per creare un nuovo progetto
    public void apriCreaProgetto() {
        CreaNuovoProgettoFrame formProgetto = new CreaNuovoProgettoFrame(control, utente, this);
        formProgetto.setVisible(true);
    }
}