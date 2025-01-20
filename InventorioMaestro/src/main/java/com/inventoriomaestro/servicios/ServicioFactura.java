package com.inventoriomaestro.servicios;

import com.inventoriomaestro.dao.FacturaDAO;
import com.inventoriomaestro.dao.ProductoDAO;
import com.inventoriomaestro.entidades.DetalleFactura;
import com.inventoriomaestro.entidades.Factura;
import com.inventoriomaestro.entidades.Producto;
import com.inventoriomaestro.entidades.Proveedor;
import com.inventoriomaestro.generador.ExportadorFacturas;
import com.inventoriomaestro.util.UtilidadFechas;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static com.inventoriomaestro.principal.Main.proveedorDAO;
import static com.inventoriomaestro.util.UtilidadFechas.validarFecha;

public class ServicioFactura {
    // Los servicios de Factura se encargan de lo  relacionado con las facturas

    private final FacturaDAO facturaDAO;
    private final ProductoDAO productoDAO;

    public ServicioFactura(FacturaDAO facturaDAO, ProductoDAO productoDAO) {
        this.facturaDAO = facturaDAO;
        this.productoDAO = productoDAO;
    }

    public void registrarCompra(Scanner scanner) {
        try {
            System.out.println("Registro de compra de productos:");

            // Usamos validarFecha de UtilidadFechas(clase) para obtener la fecha
            Date fecha = UtilidadFechas.validarFecha(scanner);

            System.out.println("Seleccione el ID del proveedor:");
            List<Proveedor> proveedores = proveedorDAO.obtenerTodos();
            proveedores.forEach(proveedor -> System.out.println(proveedor.getId() + " - " + proveedor.getNombre()));

            long proveedorId = scanner.nextLong();
            scanner.nextLine();
            Proveedor proveedor = proveedorDAO.encontrarPorId(proveedorId);

            if (proveedor == null) {
                System.out.println("Proveedor no encontrado.");
                return;
            }

            Factura factura = new Factura(fecha, "COMPRA", proveedor);

            boolean agregarProductos = true;
            while (agregarProductos) {
                System.out.println("Seleccione el ID del producto:");
                List<Producto> productos = productoDAO.obtenerTodos();
                productos.forEach(producto -> System.out.println(producto.getId() + " - " + producto.getNombre()));

                long productoId = scanner.nextLong();
                scanner.nextLine(); // Consumir salto de línea
                Producto producto = productoDAO.encontrarPorId(productoId);

                if (producto == null) {
                    System.out.println("Producto no encontrado.");
                    continue;
                }

                System.out.println("Ingrese la cantidad comprada:");
                int cantidad = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                if (cantidad <= 0) {
                    System.out.println("La cantidad debe ser mayor que cero.");
                    continue;
                }

                DetalleFactura detalle = new DetalleFactura(factura, producto, cantidad, producto.getPrecio());
                factura.getLineas().add(detalle);

                producto.setStock(producto.getStock() + cantidad);
                productoDAO.actualizar(producto);

                System.out.println("¿Desea agregar otro producto? (s/n):");
                String respuesta = scanner.nextLine();
                agregarProductos = respuesta.equalsIgnoreCase("s");
            }

            facturaDAO.guardar(factura);
            System.out.println("Factura de compra registrada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al registrar la compra: " + e.getMessage());
        }
    }

    public void registrarVenta(Scanner scanner) {
        try {
            System.out.println("Registro de venta de productos:");

            System.out.println("Ingrese la fecha de la factura (formato: yyyy-MM-dd):");
            Date fecha = validarFecha(scanner);

            System.out.println("Seleccione el ID del cliente:");
            Factura factura = new Factura(fecha, "VENTA", null);

            boolean agregarProductos = true;
            while (agregarProductos) {
                System.out.println("Seleccione el ID del producto:");
                List<Producto> productos = productoDAO.obtenerTodos();
                productos.forEach(producto -> System.out.println(producto.getId() + " - " + producto.getNombre()));

                long productoId = scanner.nextLong();
                scanner.nextLine(); // Consumir salto de línea
                Producto producto = productoDAO.encontrarPorId(productoId);

                if (producto == null) {
                    System.out.println("Producto no encontrado.");
                    continue;
                }

                System.out.println("Ingrese la cantidad vendida:");
                int cantidad = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                if (cantidad <= 0 || cantidad > producto.getStock()) {
                    System.out.println("Cantidad inválida. Verifique el stock disponible.");
                    continue;
                }

                DetalleFactura detalle = new DetalleFactura(producto, factura, cantidad, producto.getPrecio());
                factura.getLineas().add(detalle);

                producto.setStock(producto.getStock() - cantidad);
                productoDAO.actualizar(producto);

                System.out.println("¿Desea agregar otro producto? (s/n):");
                String respuesta = scanner.nextLine();
                agregarProductos = respuesta.equalsIgnoreCase("s");
            }

            facturaDAO.guardar(factura);
            System.out.println("Factura de venta registrada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al registrar la venta: " + e.getMessage());
        }
    }

    public void exportarFacturacion(Scanner scanner) {
        System.out.println("Exportar facturación:");
        System.out.println("1. Exportar a JSON");
        System.out.println("2. Exportar a XML");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese el nombre del archivo de salida (sin extensión):");
        String nombreArchivo = scanner.nextLine();

        System.out.println("Ingrese la fecha de inicio (formato: yyyy-MM-dd):");
        String fechaInicio = scanner.nextLine();
        System.out.println("Ingrese la fecha de fin (formato: yyyy-MM-dd):");
        String fechaFin = scanner.nextLine();

        try {
            Date inicio = new SimpleDateFormat("yyyy-MM-dd").parse(fechaInicio);
            Date fin = new SimpleDateFormat("yyyy-MM-dd").parse(fechaFin);

            if (inicio.after(fin)) {
                System.out.println("La fecha de inicio no puede ser posterior a la fecha de fin.");
                return;
            }

            List<Factura> facturas = facturaDAO.obtenerFacturasPorTipoYFechas("VENTA", inicio, fin);

            ExportadorFacturas exportador = new ExportadorFacturas();

            if (opcion == 1) {
                exportador.exportarFacturacionAJSON(facturas, nombreArchivo + ".json");
            } else if (opcion == 2) {
                exportador.exportarFacturacionAXML(facturas, nombreArchivo + ".xml");
            } else {
                System.out.println("Opción no válida.");
            }
        } catch (Exception e) {
            System.out.println("Error al exportar facturación: " + e.getMessage());
        }
    }
}
