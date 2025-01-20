package com.inventoriomaestro.gestion;

import com.inventoriomaestro.dao.ProveedorDAO;
import com.inventoriomaestro.entidades.Producto;
import com.inventoriomaestro.dao.ProductoDAO;
import com.inventoriomaestro.entidades.Proveedor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class ImportarProductos {

    private final ProductoDAO productoDAO;
    private final ProveedorDAO proveedorDAO;

    public ImportarProductos(ProductoDAO productoDAO, ProveedorDAO proveedorDAO) {
        this.productoDAO = productoDAO;
        this.proveedorDAO = proveedorDAO;
    }

    public void importar() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("productos.csv");

            if (inputStream == null) {
                System.out.println("Archivo productos.csv no encontrado en resources.");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String linea;

            br.readLine();

            int contador = 0; // Contar productos importados
            int errores = 0;  // Contar errores

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");

                if (datos.length != 6) {
                    System.out.println("Línea inválida (número de columnas incorrecto): " + linea);
                    errores++;
                    continue;
                }

                try {
                    String nombreProducto = datos[1].trim();
                    String categoria = datos[2].trim();
                    double precio = Double.parseDouble(datos[3].trim());
                    int stock = Integer.parseInt(datos[4].trim());
                    String nombreProveedor = datos[5].trim();

                    if (productoDAO.obtenerTodos().stream()
                            .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombreProducto))) {
                        System.out.println("Producto duplicado encontrado (omitido): " + nombreProducto);
                        continue;
                    }

                    Proveedor proveedor = proveedorDAO.buscarPorNombre(nombreProveedor);
                    if (proveedor == null) {
                        // Crear proveedor genérico si no existe
                        proveedor = new Proveedor(nombreProveedor, "Dirección genérica", "Teléfono genérico", "Email genérico");
                        proveedorDAO.guardar(proveedor);
                    }

                    Producto producto = new Producto(nombreProducto, categoria, precio, stock, proveedor);
                    productoDAO.guardar(producto);
                    contador++;

                } catch (NumberFormatException e) {
                    System.out.println("Error al convertir datos numéricos en línea: " + linea);
                    errores++;
                } catch (Exception e) {
                    System.out.println("Error inesperado en línea: " + linea);
                    e.printStackTrace();
                    errores++;
                }
            }

            System.out.printf("Importación completada. Productos importados: %d, Errores: %d%n", contador, errores);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado durante la importación: " + e.getMessage());
        }
    }
}
