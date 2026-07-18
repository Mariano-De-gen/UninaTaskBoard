package it.unina.taskboard.Main;

import it.unina.taskboard.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Avvio test di connessione al database...");

        try {
            // Tenta di invocare il Singleton della connessione che hai definito nel package utils
            Connection conn = DatabaseConnection.getInstance();

            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESSO: Il collegamento al database è funzionante.");
            } else {
                System.out.println("ERRORE: La connessione è stata inizializzata ma risulta chiusa o nulla.");
            }

        } catch (SQLException e) {
            System.err.println("FALLIMENTO CRITICO: Impossibile stabilire il collegamento con il database.");
            System.err.println("Codice di errore SQL: " + e.getSQLState());
            System.err.println("Messaggio: " + e.getMessage());
        }
    }
}