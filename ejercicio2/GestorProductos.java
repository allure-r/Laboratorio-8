package ejercicio2;

import java.sql.Connection;        // Para manejar la conexión a la base de datos
import java.sql.DriverManager;     // Para establecer la conexión JDBC
import java.sql.ResultSet;         // Para almacenar los datos leídos de SQLite
import java.sql.SQLException;    // Para capturar errores de SQL
import java.sql.Statement;         // Para ejecutar la consulta inicial
import java.util.ArrayList;        // Para usar listas dinámicas/arreglos de objetos
import java.util.Comparator;       // Para aplicar ordenamiento ascendente/descendente
import java.util.List;             // Para definir la estructura de lista
import java.util.function.Predicate;// Para manejar las condiciones de filtrado (WHERE en Java)
import java.util.stream.Collectors;// Para transformar los resultados de Stream a Lista

public class GestorProductos {

    private List<Producto> listaProductos; // Arreglo/Lista para guardar los objetos en memoria
    private static final String URL = "jdbc:sqlite:./ejercicio1/inventario.db"; // Ruta a la BD del ejercicio 1

    public GestorProductos() { 
        this.listaProductos = new ArrayList<>(); // Inicializa la lista vacía de productos
        cargarRegistrosDesdeBD(); // Al instanciar la clase, llena la lista con los registros de SQLite
    }

    // Método para leer la tabla SQLite y llenar el arreglo de objetos Producto en memoria
    public void cargarRegistrosDesdeBD() {
        listaProductos.clear(); // Limpia la lista antes de cargar los datos
        String sql = "SELECT * FROM productos"; // Consulta SQL estática para traer todos los registros de la BD

        try (Connection conn = DriverManager.getConnection(URL); // Conecta a la base de datos SQLite
             Statement stmt = conn.createStatement();           // Crea el Statement
             ResultSet rs = stmt.executeQuery(sql)) {           // Ejecuta la consulta y obtiene el ResultSet

            while (rs.next()) { // Recorre cada fila devuelta por la base de datos
                // Instancia un objeto Producto con los datos de la fila actual y lo añade al arreglo
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );
                listaProductos.add(p); // Agrega el objeto a la lista en memoria
            }

        } catch (SQLException e) { // Captura posibles errores de lectura SQL
            System.out.println("Error al cargar registros desde la BD: " + e.getMessage()); // Imprime el mensaje de error
        }
    }

    // Método solicitado en el Ejercicio 2 para realizar la consulta sobre el arreglo de objetos
    public void consultar(List<String> camposMostrar, Predicate<Producto> condicion, String campoOrden, boolean ascendente, int limite) {

        var stream = listaProductos.stream(); // Convierte el arreglo de objetos a un Stream para procesarlo

        // REQUISITO b): Filtrar los registros si el usuario especifica una condición
        if (condicion != null) { // Si el usuario ingresó una condición válida
            stream = stream.filter(condicion); // Aplica el filtro en Java sobre la lista de objetos
        }

        // REQUISITO c): Ordenar los registros ascendente o descendentemente por el campo elegido
        if (campoOrden != null && !campoOrden.trim().isEmpty()) { // Si se indicó un campo de ordenamiento
            Comparator<Producto> comparador = null; // Inicializa el comparador

            // Determina la lógica de comparación según el campo especificado
            switch (campoOrden.toLowerCase().trim()) {
                case "id":
                    comparador = Comparator.comparingInt(Producto::getId); // Ordena por ID
                    break;
                case "nombre":
                    comparador = Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER); // Ordena por Nombre
                    break;
                case "precio":
                    comparador = Comparator.comparingDouble(Producto::getPrecio); // Ordena por Precio
                    break;
                case "stock":
                    comparador = Comparator.comparingInt(Producto::getStock); // Ordena por Stock
                    break;
            }

            if (comparador != null) { // Si el campo ingresado es válido
                if (!ascendente) { // Si el usuario indicó orden descendente
                    comparador = comparador.reversed(); // Invierte el orden de la comparación
                }
                stream = stream.sorted(comparador); // Aplica el ordenamiento al Stream
            }
        }

        // REQUISITO d): Limitar la cantidad de registros a mostrar
        if (limite > 0) { // Si el usuario ingresó un límite mayor a cero
            stream = stream.limit(limite); // Limita la cantidad de elementos en el Stream
        }

        // Recolecta los elementos procesados del Stream en una nueva Lista de objetos Producto
        List<Producto> resultado = stream.collect(Collectors.toList());

        // REQUISITO a): Muestra los resultados especificando únicamente los campos deseados
        mostrarResultadoProyectado(resultado, camposMostrar);
    }

    // Método auxiliar para imprimir los campos seleccionados por el usuario
    private void mostrarResultadoProyectado(List<Producto> lista, List<String> camposMostrar) {
        System.out.println("\n--- RESULTADO DE LA CONSULTA EN MEMORIA ---");

        if (lista.isEmpty()) { // Verifica si el filtro devolvió algún registro
            System.out.println("No se encontraron registros que cumplan las condiciones."); // Informa al usuario
            return; // Termina la ejecución de la función
        }

        // Imprime la cabecera dinámica uniendo los nombres de los campos seleccionados
        System.out.println(String.join(" | ", camposMostrar));
        System.out.println("--------------------------------------------------");

        // Recorre la lista de objetos filtrados y formateados
        for (Producto p : lista) {
            List<String> valoresFila = new ArrayList<>(); // Lista para almacenar los valores de la fila actual

            for (String campo : camposMostrar) { // Evalúa cada campo solicitado por el usuario
                switch (campo.toLowerCase().trim()) {
                    case "id":
                        valoresFila.add(String.valueOf(p.getId())); // Obtiene el id como String
                        break;
                    case "nombre":
                        valoresFila.add(p.getNombre()); // Obtiene el nombre
                        break;
                    case "precio":
                        valoresFila.add(String.format("%.2f", p.getPrecio())); // Obtiene el precio formateado
                        break;
                    case "stock":
                        valoresFila.add(String.valueOf(p.getStock())); // Obtiene el stock como String
                        break;
                    default:
                        valoresFila.add("N/A"); // Si ingresó un nombre de campo no válido
                }
            }
            // Imprime la fila con los datos formateados separados por barras
            System.out.println(String.join(" | ", valoresFila));
        }
    }
}