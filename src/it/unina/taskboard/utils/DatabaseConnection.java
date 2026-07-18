package it.unina.taskboard.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // questa e la parte di codice per collegarsi al database creato da noi per il progetto
    private static final String URL = "jdbc:postgresql://localhost:5432/Uninataskboard";
    private static final String USER = "postgres"; 
    private static final String PASSWORD = "";

    private static Connection connection = null;

    // Costruttore privato per impedire l'istanza multipla (Pattern Singleton)
    private DatabaseConnection() {}

    public static Connection getInstance() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connessione al database stabilita con successo.");
            } catch (SQLException e) {
                System.err.println("Errore di connessione al database.");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }
}