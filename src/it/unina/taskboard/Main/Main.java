package it.unina.taskboard.Main;
import it.unina.taskboard.boundary.LoginFrame;
import it.unina.taskboard.control.TaskboardControl;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        
        // SwingUtilities.invokeLater serve per evitare crash quando si aprono le finestre grafiche
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Crea il controller
                TaskboardControl control = new TaskboardControl();
                
                // Inizializza la finestra di Login passandole il control
                LoginFrame finestraLogin = new LoginFrame(control);
                
                // Mostra la finestra richiamando il metodo richiesto
                finestraLogin.mostraFormLogin();
            }
        });
    }
}