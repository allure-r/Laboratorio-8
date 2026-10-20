package pe.ucsm.lp3.actividad1;

public class Producto {
    private final int id;
    private final String nombre;
    private final double precio;

    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }

    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %s | Precio: S/ %.2f",
                id, nombre, precio); // Formato de salida
    }
}
