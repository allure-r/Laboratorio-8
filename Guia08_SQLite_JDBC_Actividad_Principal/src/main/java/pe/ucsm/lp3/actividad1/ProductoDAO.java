package pe.ucsm.lp3.actividad1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public void crearTabla() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, precio REAL NOT NULL CHECK(precio >= 0))"; // 3 campos
        try (Connection con = Database.conectar(); Statement st = con.createStatement()) {
            st.execute(sql); // Crea la tabla si falta
        }
    }

    public int insertar(String nombre, double precio) throws SQLException {
        String sql = "INSERT INTO productos(nombre, precio) VALUES(?, ?)";
        try (Connection con = Database.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre); // Parametro 1: nombre
            ps.setDouble(2, precio); // Parametro 2: precio
            return ps.executeUpdate();
        }
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM productos ORDER BY id";
        try (Connection con = Database.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio")));
            }
        }
        return productos;
    }

    public int actualizar(int id, String nombre, double precio) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, precio = ? WHERE id = ?";
        try (Connection con = Database.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre); // Nuevo nombre
            ps.setDouble(2, precio); // Nuevo precio
            ps.setInt(3, id); // Registro que cambia
            return ps.executeUpdate();
        }
    }

    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection con = Database.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); // ID a eliminar
            return ps.executeUpdate();
        }
    }

    public boolean insertarEnTransaccion(String nombre, double precio, boolean confirmar) throws SQLException {
        String sql = "INSERT INTO productos(nombre, precio) VALUES(?, ?)";
        try (Connection con = Database.conectar()) {
            con.setAutoCommit(false); // Inicia la transaccion
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setDouble(2, precio);
                    ps.executeUpdate();
                }
                if (confirmar) {
                    con.commit(); // Guarda el cambio
                } else {
                    con.rollback(); // Revierte el cambio
                }
                return confirmar;
            } catch (SQLException e) {
                con.rollback(); // Evita dejar cambios incompletos
                throw e;
            }
        }
    }
}
