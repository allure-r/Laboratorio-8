package ejercicio1;

import java.sql.Connection;    //para manejar la conexión a la base de datos
import java.sql.DriverManager;  //  para establecer la conexión JDBC
import java.sql.PreparedStatement;   // para ejecutar consultas parametrizadas seguras
import java.sql.ResultSet;           //  para almacenar y recorrer los resultados de las consultas
import java.sql.SQLException;        // para manejar errores relacionados con SQL
import java.sql.Statement;           // para ejecutar sentencias SQL estáticas
import java.util.Scanner;            //  para leer la entrada del usuario por consola

public class GestionInventario {  

    private static final String URL = "jdbc:sqlite:./ejercicio1/inventario.db"; // Define la ruta de conexión al archivo SQLite local
    private static final String CLAVE_SECRETA = "admin123";  // Define la contraseña requerida para confirmar modificaciones

    public static void main(String[] args) { 
        inicializarBaseDatos();  // Llama al método que crea la base de datos y la tabla si no existen

        Scanner scanner = new Scanner(System.in); // Instancia el objeto Scanner para capturar datos de consola
        int opcion = 0;              // Variable para almacenar la opción elegida por el usuario en el menú

        try (Connection conn = DriverManager.getConnection(URL)) { // Abre la conexión a SQLite usando un bloque try-with-resources
            do {   // Inicia el bucle do-while para mantener el menú activo
                System.out.println("\n========================================"); 
                System.out.println("   MENÚ DE GESTIÓN DE INVENTARIO (SQLite)"); 
                System.out.println("========================================"); 
                System.out.println("1. Mostrar datos");     
                System.out.println("2. Ingresar registro"); 
                System.out.println("3. Actualizar registro");
                System.out.println("4. Borrar registro");   
                System.out.println("5. Salir");          
                System.out.print("Elija una opción: ");     // Solicita al usuario ingresar una opción
                
                if (scanner.hasNextInt()) { // Verifica si la entrada del usuario es un num entero válido
                    opcion = scanner.nextInt(); // Lee el número entero ingresado
                    scanner.nextLine();     // Limpia el salto de línea pendiente en el buffer
                } else {    // Si la entrada no es un número entero
                    System.out.println("Por favor, ingrese un número válido"); // Muestra mensaje de error
                    scanner.nextLine();     // Limpia la entrada incorrecta del buffer
                    continue;   // Salta a la siguiente iteración del bucle
                }

                switch (opcion) {   // Evalúa la opción seleccionada por el usuario
                    case 1:                 
                        mostrarDatos(conn); // Llama al método para listar los registros
                        break;              
                    case 2:               
                        ingresarDatos(conn, scanner); // Llama al método para insertar un nuevo registro
                        break;              
                    case 3:               
                        actualizarDatos(conn, scanner); // Llama al método para modificar un registro existente
                        break;              
                    case 4:                 
                        borrarDatos(conn, scanner); // Llama al método para eliminar un registro
                        break;             
                    case 5:                 
                        System.out.println("Saliendo del sistema..."); // Muestra mensaje de despedida
                        break;              // Sale del switch
                    default:     // Si ingresa un número fuera del rango de opciones
                        System.out.println("Opción no válida. Intente nuevamente."); // Muestra mensaje de opción incorrecta
                }                           
            } while (opcion != 5);   // El bucle se repite mientras la opción sea diferente de 5

        } catch (SQLException e) {  // Captura cualquier error de SQL durante la conexión
            System.out.println("Error en la conexión a la base de datos: " + e.getMessage()); // Imprime el error detallado
        } finally {     // Bloque que se ejecuta al salir del try principal
            scanner.close();    // Cierra el objeto Scanner para liberar recursos
        }                                   
    }

    private static void inicializarBaseDatos() { // Método para crear la tabla si no existe previamente
        try (Connection conn = DriverManager.getConnection(URL); // Conecta a la base de datos local
             Statement stmt = conn.createStatement()) { // Crea un objeto Statement para ejecutar SQL estricto
            
            String sql = "CREATE TABLE IF NOT EXISTS productos (" + // Sentencia SQL para crear la tabla productos
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // Campo 1: Identificador único autoincremental
                         "nombre TEXT NOT NULL, " +                 // Campo 2: Nombre del producto (Obligatorio)
                         "precio REAL NOT NULL, " +                 // Campo 3: Precio unitario del producto (Obligatorio)
                         "stock INTEGER NOT NULL)";                 // Campo 4: Cantidad disponible en stock (Obligatorio)
            stmt.execute(sql);              // Ejecuta la sentencia SQL en la base de datos
        } catch (SQLException e) {          // Captura errores de SQL al inicializar la base de datos
            System.out.println("Error al crear la tabla: " + e.getMessage()); // Muestra el mensaje del error
        }                                   
    }

    private static void mostrarDatos(Connection conn) { // Método para consultar y mostrar todos los registros
        String sql = "SELECT * FROM productos"; // Consulta SQL para seleccionar todos los registros de la tabla
        try (Statement stmt = conn.createStatement(); // Crea el Statement para la consulta
             ResultSet rs = stmt.executeQuery(sql)) { // Ejecuta la consulta y guarda los resultados en un ResultSet
            
            System.out.println("\n--- LISTADO DE PRODUCTOS ---"); 
            System.out.printf("%-5s | %-20s | %-10s | %-5s\n", "ID", "NOMBRE", "PRECIO", "STOCK"); // Imprime cabeceras de columnas formateadas
            System.out.println("--------------------------------------------------"); 
            
            boolean hayRegistros = false;// Bandera para verificar si existen registros en la tabla
            while (rs.next()) { // Recorre fila por fila los resultados de la consulta
                hayRegistros = true;    // Cambia la bandera a verdadero porque sí hay al menos un registro
                System.out.printf("%-5d | %-20s | %-10.2f | %-5d\n", // Imprime los datos formateados de la fila actual
                        rs.getInt("id"),    // Obtiene el valor del campo id
                        rs.getString("nombre"), // Obtiene el valor del campo nombre
                        rs.getDouble("precio"), // Obtiene el valor del campo precio
                        rs.getInt("stock")); // Obtiene el valor del campo stock
            }                               
            if (!hayRegistros) {   // Si la bandera sigue en falso (no hay datos)
                System.out.println("No hay registros en la tabla."); // Informa al usuario que la tabla está vacía
            }                               
        } catch (SQLException e) {   // Captura errores durante la consulta
            System.out.println("Error al consultar los datos: " + e.getMessage()); // Imprime el error
        }                                   
    }

    private static boolean confirmarClave(Scanner scanner) { // Método auxiliar para validar la contraseña de seguridad
        System.out.print("Ingrese la clave de seguridad para confirmar la operación: "); // Pide la clave al usuario
        String claveIngresada = scanner.nextLine(); // Lee la clave ingresada por consola
        
        if (CLAVE_SECRETA.equals(claveIngresada)) { // Compara la clave ingresada con la constante secreta
            return true;    // Retorna verdadero si coinciden
        } else {   // Si la clave es incorrecta
            System.out.println("Clave incorrecta."); // Muestra mensaje de fallo
            return false;                   // Retorna falso
        }                                   
    }                                       

    private static void ingresarDatos(Connection conn, Scanner scanner) { // Método para insertar un nuevo registro con transacción
        System.out.print("Ingrese el nombre del producto: "); // Solicita el nombre
        String nombre = scanner.nextLine(); // Lee el nombre ingresado
        System.out.print("Ingrese el precio: "); // Solicita el precio
        double precio = scanner.nextDouble(); // Lee el precio ingresado
        System.out.print("Ingrese el stock: "); // Solicita el stock
        int stock = scanner.nextInt();      // Lee el stock ingresado
        scanner.nextLine();   // Limpia el buffer de entrada

        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)"; // Sentencia SQL de inserción parametrizada
        try {   // Bloque try para control de transacciones y excepciones
            conn.setAutoCommit(false);      // Desactiva el auto-commit para iniciar transacción manual
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara la sentencia SQL de forma segura
                pstmt.setString(1, nombre); // Asigna el valor del primer parámetro (nombre)
                pstmt.setDouble(2, precio); // Asigna el valor del segundo parámetro (precio)
                pstmt.setInt(3, stock); // Asigna el valor del tercer parámetro (stock)
                pstmt.executeUpdate(); // Ejecuta la inserción temporalmente en la base de datos

                if (confirmarClave(scanner)) { // Llama a confirmarClave; si es true...
                    conn.commit();  // Confirma los cambios permanentemente en la base de datos
                    System.out.println("¡Operación confirmada! Registro guardado con éxito."); // Mensaje de éxito
                } else { // Si la clave es incorrecta (false)
                    conn.rollback(); // Revierte los cambios hechos en esta transacción
                    System.out.println("Operación revertida (Rollback ejecutado)."); // Mensaje de reversión
                }                          
            }                               
        } catch (SQLException e) {  // Captura errores SQL en la inserción
            try {   // Intenta hacer rollback en caso de excepción imprevista
                conn.rollback();  // Revierte la transacción
            } catch (SQLException ex) {  // Captura error si falla el rollback
                ex.printStackTrace(); // Imprime la traza del error secundario
            }                               
            System.out.println("Error en la inserción: " + e.getMessage()); // Muestra el mensaje del error principal
        } finally { // Bloque que siempre se ejecuta al finalizar la operación
            try {   // Intenta restaurar el estado de auto-commit
                conn.setAutoCommit(true);   // Restaura el auto-commit a true por defecto
            } catch (SQLException e) {  // Captura error si falla la restauración
                e.printStackTrace();  // Imprime la traza del error
            }                               
        }                                   
    }                                       

    private static void actualizarDatos(Connection conn, Scanner scanner) { // Método para actualizar un registro con transacción
        System.out.print("Ingrese el ID del producto a actualizar: "); // Solicita el ID del producto
        int id = scanner.nextInt();  // Lee el ID ingresado
        scanner.nextLine();  // Limpia el buffer

        System.out.print("Ingrese el nuevo nombre: "); // Solicita el nuevo nombre
        String nombre = scanner.nextLine(); // Lee el nuevo nombre
        System.out.print("Ingrese el nuevo precio: "); // Solicita el nuevo precio
        double precio = scanner.nextDouble(); // Lee el nuevo precio
        System.out.print("Ingrese el nuevo stock: "); // Solicita el nuevo stock
        int stock = scanner.nextInt();   // Lee el nuevo stock
        scanner.nextLine();   // Limpia el buffer

        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ? WHERE id = ?"; // Sentencia SQL de actualización
        try {    // Inicia bloque try para transacciones
            conn.setAutoCommit(false); // Desactiva el auto-commit para control manual
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara la sentencia SQL
                pstmt.setString(1, nombre); // Asigna el nuevo nombre al parámetro 1
                pstmt.setDouble(2, precio); // Asigna el nuevo precio al parámetro 2
                pstmt.setInt(3, stock);  // Asigna el nuevo stock al parámetro 3
                pstmt.setInt(4, id);  // Asigna el ID del producto al parámetro 4
                int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la actualización y guarda cuántas filas cambiaron

                if (filasAfectadas > 0) { // Verifica si el registro con ese ID realmente existe
                    if (confirmarClave(scanner)) { // Pide la clave de seguridad; si es correcta...
                        conn.commit(); // Confirma y guarda los cambios permanentemente
                        System.out.println("¡Operación confirmada! Actualización realizada con éxito."); // Mensaje de éxito
                    } else { // Si la clave es incorrecta...
                        conn.rollback();    // Revierte la actualización (rollback)
                        System.out.println("Operación revertida (Rollback ejecutado)."); // Mensaje de reversión
                    }                       
                } else {     // Si no se encontró el ID especificado
                    System.out.println("No se encontró un producto con el ID especificado."); // Mensaje de aviso
                    conn.rollback();  // Revierte la operación vacía
                }                           
            }                               
        } catch (SQLException e) {   // Captura errores SQL
            try {    // Intenta revertir cambios ante excepciones
                conn.rollback();  // Ejecuta rollback
            } catch (SQLException ex) { // Captura error del rollback
                ex.printStackTrace(); // Imprime la traza
            }                               
            System.out.println("Error en la actualización: " + e.getMessage()); // Muestra error
        } finally {   // Bloque final
            try {      // Intenta restaurar auto-commit
                conn.setAutoCommit(true);   // Restaura a true
            } catch (SQLException e) {      // Captura error
                e.printStackTrace();       // Imprime traza
            }                               
        }                                   
    }                                       

    private static void borrarDatos(Connection conn, Scanner scanner) { // Método para eliminar un registro con transacción
        System.out.print("Ingrese el ID del producto a borrar: "); // Solicita el ID a eliminar
        int id = scanner.nextInt(); // Lee el ID ingresado
        scanner.nextLine();   // Limpia el buffer

        String sql = "DELETE FROM productos WHERE id = ?"; // Sentencia SQL para borrar por ID
        try {     // Inicia bloque try para transacciones
            conn.setAutoCommit(false);      // Desactiva el auto-commit para control manual
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepara la sentencia SQL
                pstmt.setInt(1, id);    // Asigna el ID del producto al parámetro 1
                int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la eliminación y guarda filas afectadas

                if (filasAfectadas > 0) {  // Si se encontró y eliminó el registro...
                    if (confirmarClave(scanner)) { // Pide clave de seguridad; si es correcta...
                        conn.commit();    // Confirma la eliminación permanentemente
                        System.out.println("¡Operación confirmada! Registro eliminado."); // Mensaje de éxito
                    } else {    // Si la clave es incorrecta...
                        conn.rollback();    // Revierte la eliminación (rollback)
                        System.out.println("Operación revertida (Rollback ejecutado)."); // Mensaje de reversión
                    }                       
                } else {    // Si no existe el ID
                    System.out.println("No se encontró un producto con el ID especificado."); // Mensaje de aviso
                    conn.rollback(); // Revierte la transacción
                }                           
            }                             
        } catch (SQLException e) {   // Captura errores SQL
            try {     // Intenta rollback en caso de error
                conn.rollback();  // Ejecuta rollback
            } catch (SQLException ex) {   // Captura error
                ex.printStackTrace();// Imprime traza
            }                               
            System.out.println("Error en la eliminación: " + e.getMessage()); // Muestra error
        } finally {      // Bloque final
            try { // Intenta restaurar auto-commit
                conn.setAutoCommit(true);   // Restaura a true
            } catch (SQLException e) {      // Captura error
                e.printStackTrace();       // Imprime traza
            }                               
        }
    }                                       
}
