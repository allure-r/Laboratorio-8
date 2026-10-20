package pe.ucsm.lp3.actividad1;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SC = new Scanner(System.in);
    private static final ProductoDAO DAO = new ProductoDAO();

    public static void main(String[] args) {
        try {
            DAO.crearTabla(); // Prepara la tabla al iniciar
            int opcion;
            do {
                mostrarMenu();
                opcion = leerEntero("Opcion: ");
                try {
                    switch (opcion) {
                        case 1 -> insertar();
                        case 2 -> listar();
                        case 3 -> actualizar();
                        case 4 -> eliminar();
                        case 5 -> probarTransaccion();
                        case 0 -> System.out.println("Programa finalizado.");
                        default -> System.out.println("Elige una opcion del menu.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error BD: " + e.getMessage()); // Error SQL
                } catch (IllegalArgumentException e) {
                    System.out.println("Dato no valido: " + e.getMessage());
                }
            } while (opcion != 0);
        } catch (SQLException e) {
            System.out.println("No se pudo iniciar la aplicacion: " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== ACTIVIDAD 1: CRUD CON SQLITE ===");
        System.out.println("1. Insertar producto");
        System.out.println("2. Mostrar productos");
        System.out.println("3. Actualizar producto");
        System.out.println("4. Eliminar producto");
        System.out.println("5. Probar transaccion (commit / rollback)");
        System.out.println("0. Salir");
    }

    private static void insertar() throws SQLException {
        String nombre = leerTexto("Nombre: ");
        double precio = leerPrecio();
        System.out.println(DAO.insertar(nombre, precio) > 0 ? "Registro insertado." : "No se inserto el registro.");
    }

    private static void listar() throws SQLException {
        List<Producto> productos = DAO.listar();
        if (productos.isEmpty()) {
            System.out.println("Todavia no hay productos.");
            return;
        }
        productos.forEach(System.out::println); // Muestra los registros encontrados
    }

    private static void actualizar() throws SQLException {
        int id = leerEntero("ID a actualizar: ");
        String nombre = leerTexto("Nuevo nombre: ");
        double precio = leerPrecio();
        int filas = DAO.actualizar(id, nombre, precio);
        System.out.println(filas > 0 ? "Registro actualizado." : "No existe ese ID.");
    }

    private static void eliminar() throws SQLException {
        int id = leerEntero("ID a eliminar: ");
        int filas = DAO.eliminar(id);
        System.out.println(filas > 0 ? "Registro eliminado." : "No existe ese ID.");
    }

    private static void probarTransaccion() throws SQLException {
        String nombre = leerTexto("Nombre temporal: ");
        double precio = leerPrecio();
        String respuesta = leerTexto("Confirmar cambio? (S/N): ");
        boolean confirmar = respuesta.equalsIgnoreCase("S");
        boolean guardado = DAO.insertarEnTransaccion(nombre, precio, confirmar);
        System.out.println(guardado ? "COMMIT realizado." : "ROLLBACK realizado; no se guardo el registro.");
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        String valor = SC.nextLine().trim();
        if (valor.isEmpty()) throw new IllegalArgumentException("el texto no puede estar vacio");
        return valor;
    }

    private static int leerEntero(String mensaje) {
        try {
            System.out.print(mensaje);
            return Integer.parseInt(SC.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ingresa un numero entero");
        }
    }

    private static double leerPrecio() {
        try {
            System.out.print("Precio (S/): ");
            double precio = Double.parseDouble(SC.nextLine().trim().replace(',', '.'));
            if (precio < 0) throw new IllegalArgumentException("el precio no puede ser negativo");
            return precio;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ingresa un precio numerico");
        }
    }
}
