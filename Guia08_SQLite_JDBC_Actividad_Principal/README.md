# Guía 08 — Actividad principal: SQLite y JDBC

Este proyecto corresponde únicamente a la sección **Experiencia de práctica** de la Guía 08. Integra en una sola aplicación: CRUD (insertar, recuperar, actualizar y borrar), `PreparedStatement` y transacciones JDBC. La tabla tiene tres campos: `id`, `nombre` y `precio`.

## Requisitos
- JDK 17 o superior
- Visual Studio Code con Extension Pack for Java
- Apache Maven

## Ejecutar
1. Abre esta carpeta en Visual Studio Code.
2. Abre una terminal en la carpeta donde está `pom.xml`.
3. Ejecuta: `mvn clean compile exec:java`

La dependencia SQLite JDBC se descargará al compilar por primera vez si tienes conexión a internet. La aplicación crea automáticamente el archivo `actividad1_productos.db` y la tabla `productos`.

## Menú
1. Insertar producto
2. Mostrar productos
3. Actualizar producto
4. Eliminar producto
5. Probar transacción (`COMMIT` / `ROLLBACK`)
0. Salir

La opción 5 pide un nombre y un precio temporal, y después solicita confirmación. Responder `S` confirma el registro; responder `N` revierte la operación.

Los ejercicios propuestos de las páginas posteriores de la guía no están incluidos porque son ejercicios adicionales y no forman parte de la sección principal citada.
