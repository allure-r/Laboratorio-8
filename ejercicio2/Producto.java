package ejercicio2; 

public class Producto { // Clase Producto

    // Atributos privados que representan los 4 campos de la tabla en SQLite
    private int id;         // Campo 1: Identificador único del producto
    private String nombre;  // Campo 2: Nombre del producto
    private double precio;  // Campo 3: Precio unitario
    private int stock;      // Campo 4: Cantidad en stock

    // Constructor para inicializar los atributos de la clase Producto
    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;               // Asigna el valor del ID
        this.nombre = nombre;       // Asigna el nombre
        this.precio = precio;       // Asigna el precio
        this.stock = stock;         // Asigna el stock
    }

    // Métodos Getter para acceder a las propiedades privadas desde la clase gestora
    public int getId() { 
        return id; // Retorna el ID
    }

    public String getNombre() { 
        return nombre; // Retorna el nombre
    }

    public double getPrecio() { 
        return precio; // Retorna el precio
    }

    public int getStock() { 
        return stock; // Retorna el stock
    }
}
