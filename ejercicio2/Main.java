package ejercicio2;

import java.util.ArrayList; // Para construir la lista de campos
import java.util.Arrays;    // Para convertir la entrada del usuario a lista
import java.util.List;      // Para manejar listas de campos
import java.util.Scanner;   // Para leer las instrucciones del usuario en tiempo de ejecución
import java.util.function.Predicate; // Para pasar la condición lógica al gestor

public class Main {

    public static void main(String[] args) {
        GestorProductos gestor = new GestorProductos(); // Instancia el gestor y carga el arreglo con los datos de SQLite
        Scanner scanner = new Scanner(System.in);        // Instancia el Scanner para capturar datos por consola

        System.out.println("\n==================================================");
        System.out.println("   CONSULTAS EN TIEMPO DE EJECUCIÓN");
        System.out.println("==================================================");

        // a) Elección de campos a mostrar
        System.out.print("a) Ingrese los campos a mostrar separados por comas (id, nombre, precio, stock): ");
        String camposInput = scanner.nextLine(); // Lee los campos ingresados por consola
        List<String> camposMostrar = Arrays.asList(camposInput.split(",")); // Convierte el texto separado por comas en una lista

        // b) Condición para filtrar los registros
        System.out.println("\nb) ¿Desea aplicar una condición de filtro sobre el PRECIO?");
        System.out.println("1. Precio mayor que ( > )");
        System.out.println("2. Precio menor que ( < )");
        System.out.println("3. Sin condición (mostrar todos)");
        System.out.print("Elija una opción: ");
        int opcionFiltro = Integer.parseInt(scanner.nextLine()); // Lee la opción del filtro

        Predicate<Producto> condicion = null; // Inicializa el predicado en nulo (sin filtro por defecto)

        if (opcionFiltro == 1) { // Si elige filtro de precio mayor
            System.out.print("Ingrese el precio mínimo: ");
            double min = Double.parseDouble(scanner.nextLine()); // Lee el valor mínimo
            condicion = p -> p.getPrecio() > min; // Expresión Lambda que evalúa si el precio del objeto es mayor al mínimo
        } else if (opcionFiltro == 2) { // Si elige filtro de precio menor
            System.out.print("Ingrese el precio máximo: ");
            double max = Double.parseDouble(scanner.nextLine()); // Lee el valor máximo
            condicion = p -> p.getPrecio() < max; // Expresión Lambda que evalúa si el precio del objeto es menor al máximo
        }

        // c) Definición del ordenamiento
        System.out.print("\nc) Campo para ordenar (id, nombre, precio, stock) o presione Enter para omitir: ");
        String campoOrden = scanner.nextLine(); // Lee el campo por el que se ordenará

        boolean ascendente = true; // Por defecto el orden es ascendente
        if (!campoOrden.trim().isEmpty()) { // Si el usuario escribió un campo para ordenar
            System.out.print("¿Desea orden ASCENDENTE (1) o DESCENDENTE (2)?: ");
            int opcionOrden = Integer.parseInt(scanner.nextLine()); // Lee el tipo de orden
            ascendente = (opcionOrden != 2); // Si elige 2, ascendente pasa a ser false (descendente)
        }

        // d) Límite de registros a mostrar
        System.out.print("\nd) Ingrese la cantidad máxima de registros a mostrar (0 para sin límite): ");
        int limite = Integer.parseInt(scanner.nextLine()); // Lee el límite de registros

        // Llama al método del gestor pasando los 4 parámetros configurados en tiempo de ejecución
        gestor.consultar(camposMostrar, condicion, campoOrden, ascendente, limite);

        scanner.close(); // Cierra el objeto Scanner
    }
}
    
