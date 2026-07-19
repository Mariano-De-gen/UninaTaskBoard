package it.unina.taskboard.boundary;

import it.unina.taskboard.control.TaskboardControl;
import it.unina.taskboard.entity.Studente;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private TaskboardControl control;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame(TaskboardControl control) {
        this.control = control;
        
        // configurazione di base della finestra
        setTitle("TaskBoard - Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new GridLayout(4, 1, 10, 10)); 

        // creazione della riga per l'Email
        JPanel panelEmail = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblEmail = new JLabel("Email:");
        
        lblEmail.setPreferredSize(new Dimension(80, 20));
        panelEmail.add(lblEmail);
        emailField = new JTextField(20);
        panelEmail.add(emailField);

        // creazione della riga per la Password
        JPanel panelPassword = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setPreferredSize(new Dimension(80, 20));
        panelPassword.add(lblPassword);
        passwordField = new JPasswordField(20);
        panelPassword.add(passwordField);

        // creazione del bottone di accesso
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Accedi");
        panelButton.add(loginButton);

        // cggiunta di tutti i pezzi alla finestra principale
        add(new JLabel("Inserisci le tue credenziali", SwingConstants.CENTER));
        add(panelEmail);
        add(panelPassword);
        add(panelButton);

        // cosa succede quando l'utente clicca su accedi
        loginButton.addActionListener(e -> {
        	// recupera cosa ha scritto l'utente
            String[] cred = raccogliCredenziali();
            // passa i dati al controller per verificare se l'utente esiste
            Studente utenteLoggato = control.login(cred[0], cred[1]); 

            if (utenteLoggato != null) {
            	// login OK: chiudi questa finestra e apri la Dashboard
                this.dispose(); 
                DashboardFrame dashboard = new DashboardFrame(control, utenteLoggato);
                dashboard.setVisible(true);
            } else {
            	// login Fallito: mostra un messaggio di errore
                mostraErroreAccesso();
            }
        });
    }
    // rende visibile la finestra di login
    public void mostraFormLogin() {
        this.setVisible(true);
    }

    public String[] raccogliCredenziali() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        return new String[]{email, password};
    }
    // mostra un messaggio di errore se i dati inseriti sono sbagliati
    public void mostraErroreAccesso() {
        JOptionPane.showMessageDialog(this, "Credenziali errate o errore di connessione.", "Errore Accesso", JOptionPane.ERROR_MESSAGE);
    }
}