package com.inventoriomaestro.gestion;

import com.inventoriomaestro.dao.ProductoDAO;
import com.inventoriomaestro.entidades.Producto;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ExportarInventario {

    private final ProductoDAO productoDAO;

    public ExportarInventario(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public void exportar(String rutaArchivo) {
        List<Producto> productos = productoDAO.obtenerTodos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados en el inventario.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {

            writer.println("ID,Nombre,Categoría,Precio,Stock,Proveedor");


            for (Producto producto : productos) {
                String proveedorNombre = producto.getProveedor() != null ? producto.getProveedor().getNombre() : "Sin proveedor";
                writer.printf("%d,%s,%s,%.2f,%d,%s%n",
                        producto.getId(),
                        producto.getNombre(),
                        producto.getCategoria(),
                        producto.getPrecio(),
                        producto.getStock(),
                        proveedorNombre);
            }

            System.out.println("Inventario exportado correctamente a: " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al exportar el inventario: " + e.getMessage());
        }
    }
}
