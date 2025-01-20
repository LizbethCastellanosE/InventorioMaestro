package com.inventoriomaestro.servicios;

import com.inventoriomaestro.dao.ProductoDAO;
import com.inventoriomaestro.entidades.Producto;
import com.inventoriomaestro.entidades.Proveedor;

import java.io.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static com.inventoriomaestro.principal.Main.proveedorDAO;

public class ServicioProducto {

    private static ProductoDAO productoDAO = null;

    public ServicioProducto(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public void registrarProducto(String nombre, String categoria, double precio, int stock, Proveedor proveedor) {
        if (productoDAO.obtenerTodos().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre))) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre '" + nombre + "'.");
        }

        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        if (proveedor == null) {
            throw new IllegalArgumentException("El producto debe estar asociado a un proveedor.");
        }

        Producto producto = new Producto(nombre, categoria, precio, stock, proveedor);
        productoDAO.guardar(producto);
    }

    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }

    public void modificarProducto(Long id, String nuevoNombre, String nuevaCategoria, Double nuevoPrecio, Integer nuevoStock, Proveedor nuevoProveedor) {
        Producto producto = productoDAO.encontrarPorId(id);

        if (producto == null) {
            throw new IllegalArgumentException("El producto con ID " + id + " no existe.");
        }

        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            producto.setNombre(nuevoNombre);
        }

        if (nuevaCategoria != null && !nuevaCategoria.isBlank()) {
            producto.setCategoria(nuevaCategoria);
        }

        if (nuevoPrecio != null && nuevoPrecio > 0) {
            producto.setPrecio(nuevoPrecio);
        }

        if (nuevoStock != null && nuevoStock >= 0) {
            producto.setStock(nuevoStock);
        }

        if (nuevoProveedor != null) {
            producto.setProveedor(nuevoProveedor);
        }

        productoDAO.actualizar(producto);
    }

    public void eliminarProducto(Scanner scanner) {
        System.out.println("Ingrese el ID del producto a eliminar:");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Producto producto = productoDAO.encontrarPorId(id);
        if (producto != null) {

            if (producto.getFacturas() != null && !producto.getFacturas().isEmpty()) {
                System.out.println("No se puede eliminar el producto porque está asociado a facturas.");
                return;
            }

            System.out.println("¿Está seguro de que desea eliminar el producto? (sí/no)");
            String confirmacion = scanner.nextLine();
            if (confirmacion.equalsIgnoreCase("sí")) {
                productoDAO.eliminar(id);
                System.out.println("Producto eliminado con éxito.");
            } else {
                System.out.println("Eliminación cancelada.");
            }
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    public void registrarProducto(Scanner scanner) {
        scanner.useLocale(Locale.US);
        try {
            System.out.println("Registrar nuevo producto:");
            System.out.println("Ingrese el nombre del producto:");
            String nombre = scanner.nextLine();

            if (productoDAO.obtenerTodos().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre))) {
                System.out.println("Error: Ya existe un producto con el nombre '" + nombre + "'.");
                return;
            }

            System.out.println("Ingrese la categoría del producto:");
            String categoria = scanner.nextLine();

            System.out.println("Ingrese el precio del producto:");
            double precio = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Ingrese el stock inicial del producto:");
            int stock = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Ingrese el nombre del proveedor:");
            String nombreProveedor = scanner.nextLine();
            Proveedor proveedor = proveedorDAO.buscarPorNombre(nombreProveedor);
            if (proveedor == null) {
                System.out.println("Proveedor no encontrado.");
                return;
            }

            Producto nuevoProducto = new Producto(nombre, categoria, precio, stock, proveedor);
            productoDAO.guardar(nuevoProducto);
            System.out.println("Producto registrado con éxito.");
        } catch (Exception e) {
            System.out.println("Error al registrar el producto: " + e.getMessage());
        }
    }

    public static void consultarProductos() {
        System.out.println("Lista de productos en inventario:");

        // Consultar todos los productos usando el DAO
        productoDAO.obtenerTodos().forEach(producto -> {
            System.out.println("ID: " + producto.getId());
            System.out.println("Nombre: " + producto.getNombre());
            System.out.println("Categoría: " + producto.getCategoria());
            System.out.println("Precio: " + producto.getPrecio());
            System.out.println("Stock: " + producto.getStock());
            System.out.println("Proveedor: " + (producto.getProveedor() != null ? producto.getProveedor().getNombre() : "Sin proveedor"));
            System.out.println("-----");
        });

        System.out.println("Fin de la lista de productos.");
    }

    public static void modificarProducto(Scanner scanner) {
        System.out.println("Ingrese el ID del producto a modificar:");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Producto producto = productoDAO.encontrarPorId(id);

        if (producto != null) {
            System.out.println("Producto encontrado: " + producto);

            System.out.println("Ingrese el nuevo nombre del producto (actual: " + producto.getNombre() + "):");
            String nuevoNombre = scanner.nextLine();
            if (!nuevoNombre.isBlank()) {
                producto.setNombre(nuevoNombre);
            }

            System.out.println("Ingrese la nueva categoría del producto (actual: " + producto.getCategoria() + "):");
            String nuevaCategoria = scanner.nextLine();
            if (!nuevaCategoria.isBlank()) {
                producto.setCategoria(nuevaCategoria);
            }

            System.out.println("Ingrese el nuevo precio del producto (actual: " + producto.getPrecio() + "):");
            String nuevoPrecio = scanner.nextLine();
            if (!nuevoPrecio.isBlank()) {
                producto.setPrecio(Double.parseDouble(nuevoPrecio));
            }

            System.out.println("Ingrese el nuevo stock del producto (actual: " + producto.getStock() + "):");
            String nuevoStock = scanner.nextLine();
            if (!nuevoStock.isBlank()) {
                producto.setStock(Integer.parseInt(nuevoStock));
            }

            productoDAO.actualizar(producto);
            System.out.println("Producto modificado con éxito.");
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    public void exportarInventario(String rutaArchivo) {
        System.out.println("Exportando inventario a " + rutaArchivo + "...");

        try (PrintWriter writer = new PrintWriter(new File(rutaArchivo))) {
            // Escribir encabezados
            writer.println("ID,Nombre,Categoría,Stock,Precio,Proveedor");

            // Obtener productos y escribir en el archivo
            List<Producto> productos = productoDAO.obtenerTodos();

            if (productos.isEmpty()) {
                System.out.println("No hay productos registrados en el inventario.");
                return;
            }

            for (Producto producto : productos) {
                // Manejar el nombre del proveedor si no está asociado
                String proveedor = (producto.getProveedor() != null) ? producto.getProveedor().getNombre() : "N/A";

                // Escribir el producto en el archivo CSV
                writer.printf("%d,%s,%s,%d,%.2f,%s%n",
                        producto.getId(),
                        producto.getNombre(),
                        producto.getCategoria(),
                        producto.getStock(),
                        producto.getPrecio(),
                        proveedor);
            }

            System.out.println("Inventario exportado correctamente a " + rutaArchivo + ".");
        } catch (IOException e) {
            System.out.println("Error al exportar el inventario: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocurrió un error inesperado durante la exportación: " + e.getMessage());
        }
    }

    public void importarProductosDesdeCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int contador = 0;

            while ((line = br.readLine()) != null) {
                if (contador == 0 && line.toLowerCase().contains("id")) {
                    contador++;
                    continue;
                }

                String[] values = line.split(",");
                if (values.length < 5) {
                    System.out.println("Línea inválida: " + line);
                    continue;
                }

                String nombreProducto = values[1].trim();
                if (productoDAO.obtenerTodos().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombreProducto))) {
                    System.out.println("Producto duplicado: " + nombreProducto);
                    continue;
                }

                Producto producto = new Producto();
                producto.setNombre(nombreProducto);
                producto.setCategoria(values[2].trim());
                producto.setPrecio(Double.parseDouble(values[3].trim()));
                producto.setStock(Integer.parseInt(values[4].trim()));

                if (values.length > 5 && !values[5].trim().isEmpty()) {
                    Proveedor proveedor = proveedorDAO.buscarPorNombre(values[5].trim());
                    if (proveedor != null) {
                        producto.setProveedor(proveedor);
                    } else {
                        System.out.println("Proveedor no encontrado: " + values[5].trim());
                    }
                }

                productoDAO.guardar(producto);
                contador++;
            }

            System.out.println("Importación completada. Total de productos importados: " + contador);
        } catch (Exception e) {
            System.out.println("Error durante la importación: " + e.getMessage());
        }
    }

    public void exportarInventarioACSV(String archivo) {
        try (PrintWriter writer = new PrintWriter(new File(archivo))) {
            writer.println("ID,Nombre,Categoría,Stock,Precio,Proveedor");
            List<Producto> productos = productoDAO.obtenerTodos();
            for (Producto producto : productos) {
                String proveedor = (producto.getProveedor() != null) ? producto.getProveedor().getNombre() : "N/A";
                writer.printf("%d,%s,%s,%d,%.2f,%s%n",
                        producto.getId(),
                        producto.getNombre(),
                        producto.getCategoria(),
                        producto.getStock(),
                        producto.getPrecio(),
                        proveedor);
            }
            System.out.println("Inventario exportado correctamente a " + archivo);
        } catch (IOException e) {
            System.out.println("Error al exportar el inventario: " + e.getMessage());
        }
    }

}
