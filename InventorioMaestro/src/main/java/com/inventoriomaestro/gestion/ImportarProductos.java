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

    private ProductoDAO productoDAO;
    private ProveedorDAO proveedorDAO;

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

            // Saltar el encabezado
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");

                // Validar número de columnas
                if (datos.length != 6) {
                    System.out.println("Línea inválida: " + linea);
                    continue;
                }

                Producto producto = new Producto();
                producto.setId(Long.parseLong(datos[0].trim()));
                producto.setNombre(datos[1].trim());
                producto.setCategoria(datos[2].trim());
                producto.setPrecio(Double.parseDouble(datos[3].trim()));
                producto.setStock(Integer.parseInt(datos[4].trim()));

                Proveedor proveedor = proveedorDAO.buscarPorNombre(datos[5].trim());
                if (proveedor == null) {
                    System.out.println("Proveedor no encontrado: " + datos[5].trim());
                    continue; // Salta este producto
                }
                producto.setProveedor(proveedor);

                productoDAO.guardar(producto);
            }

            System.out.println("Productos importados correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
