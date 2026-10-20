package pe.ucsm.lp3.actividad1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String URL = "jdbc:sqlite:actividad1_productos.db"; // Base local

    private Database() { }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC"); // Carga el controlador JDBC
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el controlador SQLite", e);
        }
        return DriverManager.getConnection(URL); // Abre o crea el archivo .db
    }
}
