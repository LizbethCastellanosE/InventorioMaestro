package com.inventoriomaestro.principal;

import com.inventoriomaestro.dao.*;
import com.inventoriomaestro.entidades.*;
import com.inventoriomaestro.generador.ExportadorFacturas;
import com.inventoriomaestro.generador.GeneradorPDF;
import com.inventoriomaestro.gestion.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static final ProductoDAO productoDAO = new ProductoDAO();
    private static final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private static final FacturaDAO facturaDAO = new FacturaDAO();
    private static final DetalleFacturaDAO detalleFacturaDAO = new DetalleFacturaDAO();

    // Crear instancias de importación/exportación
    private static final ImportarProductos importarProductos = new ImportarProductos(productoDAO, proveedorDAO);
    private static final ImportarProveedores importarProveedores = new ImportarProveedores(proveedorDAO);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            // Mostrar menú principal
            System.out.println("Seleccione una opción:");
            System.out.println("1. Gestión de productos");
            System.out.println("2. Gestión de proveedores");
            System.out.println("3. Gestión de facturas");
            System.out.println("4. Importar/exportar datos");
            System.out.println("5. Generar informes");
            System.out.println("6. Exportar facturación");
            System.out.println("7. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            switch (opcion) {
                case 1:
                    gestionProductos(scanner);
                    break;
                case 2:
                    gestionProveedores(scanner);
                    break;
                case 3:
                    gestionFacturas(scanner);
                    break;
                case 4:
                    importarExportarDatos(scanner);
                    break;
                case 5:
                    generarInformes(scanner);
                    break;
                case 6:
                    exportarFacturacion(scanner); // Nueva opción
                    break;
                case 7:
                    continuar = false;
                    cerrarConexiones();
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    private static void exportarFacturacion(Scanner scanner) {
        System.out.println("Exportar facturación:");
        System.out.println("1. Exportar a JSON");
        System.out.println("2. Exportar a XML");

        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir salto de línea

        System.out.println("Ingrese el nombre del archivo de salida (sin extensión):");
        String nombreArchivo = scanner.nextLine();

        System.out.println("Ingrese la fecha de inicio (formato: yyyy-MM-dd):");
        String fechaInicio = scanner.nextLine();
        System.out.println("Ingrese la fecha de fin (formato: yyyy-MM-dd):");
        String fechaFin = scanner.nextLine();

        try {
            // Convertir las fechas
            Date inicio = new SimpleDateFormat("yyyy-MM-dd").parse(fechaInicio);
            Date fin = new SimpleDateFormat("yyyy-MM-dd").parse(fechaFin);

            // Obtener facturas
            List<Factura> facturas = facturaDAO.obtenerFacturasPorTipoYFechas("VENTA", inicio, fin);

            // Instanciar el exportador
            ExportadorFacturas exportador = new ExportadorFacturas();

            // Exportar según la opción seleccionada
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


    private static void gestionProductos(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("Gestión de productos:");
            System.out.println("1. Registrar producto");
            System.out.println("2. Consultar productos");
            System.out.println("3. Modificar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("5. Volver al menú principal");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            switch (opcion) {
                case 1:
                    registrarProducto(scanner);
                    break;
                case 2:
                    consultarProductos();
                    break;
                case 3:
                    modificarProducto(scanner);
                    break;
                case 4:
                    eliminarProducto(scanner);
                    break;
                case 5:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    private static void gestionProveedores(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("Gestión de proveedores:");
            System.out.println("1. Registrar proveedor");
            System.out.println("2. Consultar proveedores");
            System.out.println("3. Modificar proveedor");
            System.out.println("4. Eliminar proveedor");
            System.out.println("5. Volver al menú principal");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            switch (opcion) {
                case 1:
                    registrarProveedor(scanner);
                    break;
                case 2:
                    consultarProveedores();
                    break;
                case 3:
                    modificarProveedor(scanner);
                    break;
                case 4:
                    eliminarProveedor(scanner);
                    break;
                case 5:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    private static void gestionFacturas(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("Gestión de facturas:");
            System.out.println("1. Registrar compra");
            System.out.println("2. Registrar venta");
            System.out.println("3. Volver al menú principal");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            switch (opcion) {
                case 1:
                    registrarCompra(scanner);
                    break;
                case 2:
                    registrarVenta(scanner);
                    break;
                case 3:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    private static void importarExportarDatos(Scanner scanner) {
        System.out.println("Importar/exportar datos:");
        System.out.println("1. Importar productos desde CSV");
        System.out.println("2. Importar proveedores desde CSV");
        System.out.println("3. Exportar inventario a CSV");
        System.out.println("4. Volver al menú principal");

        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir salto de línea

        switch (opcion) {
            case 1:
                System.out.println("Ingrese la ruta del archivo CSV de productos:");
                String rutaProductos = scanner.nextLine();
                importarProductosDesdeCSV(rutaProductos);
                break;
            case 2:
                System.out.println("Ingrese la ruta del archivo CSV de proveedores:");
                String rutaProveedores = scanner.nextLine();
                importarProveedoresDesdeCSV(rutaProveedores);
                break;
            case 3:
                exportarInventario();
                break;
            case 4:
                System.out.println("Volviendo al menú principal...");
                break;
            default:
                System.out.println("Opción no válida.");
                break;
        }
    }


    private static void generarInformes(Scanner scanner) {
        System.out.println("Generar informes:");
        System.out.println("1. Generar informe de ventas (PDF)");
        System.out.println("2. Generar informe de inventario (Word)");
        System.out.println("3. Volver al menú principal");

        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir salto de línea

        switch (opcion) {
            case 1:
                generarInformeVentas();
                break;
            case 2:
                generarInformeInventario();
                break;
            case 3:
                generarInformeInventarioWord();
                break;
            case 4:
                System.out.println("Volviendo al menú principal...");
                break;
            default:
                System.out.println("Opción no válida.");
                break;
        }
    }

    private static void registrarProducto(Scanner scanner) {
        scanner.useLocale(Locale.US); // Configurar para aceptar punto decimal
        try {
            System.out.println("Registrar nuevo producto:");
            System.out.println("Ingrese el nombre del producto:");
            String nombre = scanner.nextLine();

            System.out.println("Ingrese la categoría del producto:");
            String categoria = scanner.nextLine();

            System.out.println("Ingrese el precio del producto:");
            double precio = scanner.nextDouble(); // Captura el precio
            scanner.nextLine(); // Consumir salto de línea

            System.out.println("Ingrese el stock inicial del producto:");
            int stock = scanner.nextInt(); // Captura el stock
            scanner.nextLine(); // Consumir salto de línea

            Producto nuevoProducto = new Producto(nombre, categoria, precio, stock, null); // Proveedor puede ser nulo aquí
            productoDAO.guardar(nuevoProducto);
            System.out.println("Producto registrado con éxito.");
        } catch (InputMismatchException e) {
            System.out.println("Error: Entrada inválida. Asegúrese de ingresar el formato correcto para los números (use un punto como separador decimal).");
            scanner.nextLine(); // Consumir entrada inválida
        } catch (Exception e) {
            System.out.println("Error al registrar el producto: " + e.getMessage());
        }
    }

    private static void registrarProveedor(Scanner scanner) {
        System.out.println("Registrar nuevo proveedor:");

        System.out.println("Ingrese el nombre del proveedor:");
        String nombre = scanner.nextLine();

        System.out.println("Ingrese la dirección del proveedor:");
        String direccion = scanner.nextLine();

        System.out.println("Ingrese el teléfono del proveedor:");
        String telefono = scanner.nextLine();

        System.out.println("Ingrese el email del proveedor:");
        String email = scanner.nextLine();

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre);
        proveedor.setDireccion(direccion);
        proveedor.setTelefono(telefono);
        proveedor.setEmail(email);

        proveedorDAO.guardar(proveedor);
        System.out.println("Proveedor registrado con éxito.");
    }

    private static void consultarProductos() {
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

    private static void consultarProveedores() {
        System.out.println("Lista de proveedores registrados:");

        // Consultar todos los proveedores usando el DAO
        proveedorDAO.obtenerTodos().forEach(proveedor -> {
            System.out.println("ID: " + proveedor.getId());
            System.out.println("Nombre: " + proveedor.getNombre());
            System.out.println("Dirección: " + proveedor.getDireccion());
            System.out.println("Teléfono: " + proveedor.getTelefono());
            System.out.println("Email: " + proveedor.getEmail());
            System.out.println("-----");
        });

        System.out.println("Fin de la lista de proveedores.");
    }

    private static void modificarProducto(Scanner scanner) {
        System.out.println("Ingrese el ID del producto a modificar:");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consumir salto de línea
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

    private static void modificarProveedor(Scanner scanner) {
        System.out.println("Ingrese el ID del proveedor a modificar:");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consumir el salto de línea

        // Buscar el proveedor por ID
        Proveedor proveedor = proveedorDAO.encontrarPorId(id);

        if (proveedor != null) {
            System.out.println("Proveedor encontrado: " + proveedor);

            // Pedir nuevos valores para el proveedor
            System.out.println("Ingrese el nuevo nombre del proveedor (actual: " + proveedor.getNombre() + "):");
            String nuevoNombre = scanner.nextLine();
            System.out.println("Ingrese la nueva dirección del proveedor (actual: " + proveedor.getDireccion() + "):");
            String nuevaDireccion = scanner.nextLine();
            System.out.println("Ingrese el nuevo teléfono del proveedor (actual: " + proveedor.getTelefono() + "):");
            String nuevoTelefono = scanner.nextLine();
            System.out.println("Ingrese el nuevo email del proveedor (actual: " + proveedor.getEmail() + "):");
            String nuevoEmail = scanner.nextLine();

            // Actualizar los valores del proveedor
            proveedor.setNombre(nuevoNombre.isEmpty() ? proveedor.getNombre() : nuevoNombre);
            proveedor.setDireccion(nuevaDireccion.isEmpty() ? proveedor.getDireccion() : nuevaDireccion);
            proveedor.setTelefono(nuevoTelefono.isEmpty() ? proveedor.getTelefono() : nuevoTelefono);
            proveedor.setEmail(nuevoEmail.isEmpty() ? proveedor.getEmail() : nuevoEmail);

            // Guardar los cambios en la base de datos
            proveedorDAO.actualizar(proveedor);

            System.out.println("Proveedor modificado con éxito.");
        } else {
            System.out.println("Proveedor con ID " + id + " no encontrado.");
        }
    }

    private static void eliminarProducto(Scanner scanner) {
        System.out.println("Ingrese el ID del producto a eliminar:");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consumir salto de línea

        Producto producto = productoDAO.encontrarPorId(id);
        if (producto != null) {
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

    private static void eliminarProveedor(Scanner scanner) {
        System.out.println("Ingrese el ID del proveedor a eliminar:");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consumir salto de línea

        // Buscar el proveedor por ID
        Proveedor proveedor = proveedorDAO.encontrarPorId(id);

        if (proveedor != null) {
            System.out.println("Proveedor encontrado: " + proveedor);
            System.out.println("¿Está seguro de que desea eliminar este proveedor? (S/N)");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("S")) {
                proveedorDAO.eliminar(id);
                System.out.println("Proveedor eliminado con éxito.");
            } else {
                System.out.println("Operación cancelada.");
            }
        } else {
            System.out.println("Proveedor con ID " + id + " no encontrado.");
        }
    }

    private static void registrarCompra(Scanner scanner) {
        try {
            System.out.println("Registro de compra de productos:");

            // Solicitar fecha de la factura
            System.out.println("Ingrese la fecha de la factura (formato: yyyy-MM-dd):");
            String fechaInput = scanner.nextLine();
            Date fecha = validarFecha(scanner);

            // Mostrar proveedores disponibles y seleccionar uno
            System.out.println("Seleccione el ID del proveedor:");
            List<Proveedor> proveedores = proveedorDAO.obtenerTodos();
            proveedores.forEach(proveedor -> System.out.println(proveedor.getId() + " - " + proveedor.getNombre()));

            long proveedorId = scanner.nextLong();
            scanner.nextLine(); // Consumir salto de línea
            Proveedor proveedor = proveedorDAO.encontrarPorId(proveedorId);

            if (proveedor == null) {
                System.out.println("Proveedor no encontrado.");
                return;
            }

            // Crear la factura
            Factura factura = new Factura(fecha, "COMPRA", proveedor);

            boolean agregarProductos = true;
            while (agregarProductos) {
                // Mostrar productos disponibles y seleccionar uno
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

                // Solicitar cantidad comprada
                System.out.println("Ingrese la cantidad comprada:");
                int cantidad = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                if (cantidad <= 0) {
                    System.out.println("La cantidad debe ser mayor que cero.");
                    continue;
                }

                // Crear línea de factura
                DetalleFactura detalle = new DetalleFactura(factura, producto, cantidad, producto.getPrecio());
                factura.getLineas().add(detalle);

                // Actualizar stock del producto
                producto.setStock(producto.getStock() + cantidad);
                productoDAO.actualizar(producto);

                // Preguntar si desea agregar más productos
                System.out.println("¿Desea agregar otro producto? (s/n):");
                String respuesta = scanner.nextLine();
                agregarProductos = respuesta.equalsIgnoreCase("s");
            }

            // Guardar la factura
            facturaDAO.guardar(factura);
            System.out.println("Factura de compra registrada exitosamente.");

        } catch (Exception e) {
            System.out.println("Error al registrar la compra: " + e.getMessage());
        }
    }

    private static void registrarVenta(Scanner scanner) {
        try {
            System.out.println("Registro de venta de productos:");

            // Solicitar datos de la cabecera
            System.out.println("Ingrese la fecha de la factura (formato: yyyy-MM-dd):");
            String fechaInput = scanner.nextLine();
            Date fecha = validarFecha(scanner);

            System.out.println("Seleccione el ID del cliente:");
            // Asumimos que no hay un cliente en tu modelo actual, pero podríamos adaptarlo.

            // Crear la factura
            Factura factura = new Factura(fecha, "VENTA", null); // Proveedor es nulo en este caso.

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

                // Crear línea de factura
                DetalleFactura detalle = new DetalleFactura(producto, factura, cantidad, producto.getPrecio());
                factura.getLineas().add(detalle);

                // Actualizar el stock del producto
                producto.setStock(producto.getStock() - cantidad);
                productoDAO.actualizar(producto);

                System.out.println("¿Desea agregar otro producto? (s/n):");
                String respuesta = scanner.nextLine();
                agregarProductos = respuesta.equalsIgnoreCase("s");
            }

            // Guardar la factura
            facturaDAO.guardar(factura);
            System.out.println("Factura de venta registrada exitosamente.");

        } catch (Exception e) {
            System.out.println("Error al registrar la venta: " + e.getMessage());
        }
    }

    private static Date validarFecha(Scanner scanner) {
        while (true) {
            System.out.println("Ingrese la fecha (formato: yyyy-MM-dd):");
            String fechaInput = scanner.nextLine();
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(fechaInput);
            } catch (ParseException e) {
                System.out.println("Error: Formato de fecha inválido. Intente de nuevo.");
            }
        }
    }

    private static void exportarInventario() {
        System.out.println("Exportando inventario a inventario.csv...");

        try (PrintWriter writer = new PrintWriter(new File("inventario.csv"))) {
            // Escribir encabezados
            writer.println("ID,Nombre,Stock,Precio,Proveedor");

            // Obtener productos y escribir en el archivo
            for (Producto producto : productoDAO.obtenerTodos()) {
                String proveedor = (producto.getProveedor() != null) ? producto.getProveedor().getNombre() : "N/A";
                writer.printf("%d,%s,%d,%.2f,%s%n",
                        producto.getId(),
                        producto.getNombre(),
                        producto.getStock(),
                        producto.getPrecio(),
                        proveedor);
            }

            System.out.println("Inventario exportado correctamente a inventario.csv.");
        } catch (IOException e) {
            System.out.println("Error al exportar el inventario: " + e.getMessage());
        }
    }

    private static void generarInformeVentas() {

        GeneradorPDF generador = new GeneradorPDF();

        // Configurar los parámetros
        String archivo = "informe_ventas.pdf";
        String titulo = "Informe de Ventas";

        // Generar contenido dinámico
        StringBuilder contenido = new StringBuilder();
        contenido.append("Ventas realizadas:\n\n");

        List<Factura> facturas = facturaDAO.obtenerFacturasPorTipo("VENTA");
        double totalGlobal = 0;

        for (Factura factura : facturas) {
            contenido.append("Factura ID: ").append(factura.getId()).append("\n");
            contenido.append("Fecha: ").append(factura.getFecha()).append("\n");
            contenido.append("Proveedor: ")
                    .append(factura.getProveedor() != null ? factura.getProveedor().getNombre() : "N/A").append("\n");

            for (DetalleFactura detalle : factura.getLineas()) {
                Producto producto = detalle.getProducto();
                int cantidad = detalle.getCantidad();
                double total = cantidad * detalle.getPrecioUnitario();

                contenido.append("    Producto: ").append(producto.getNombre()).append(", Cantidad: ")
                        .append(cantidad).append(", Total: €").append(String.format("%.2f", total)).append("\n");

                totalGlobal += total;
            }
            contenido.append("\n");
        }

        contenido.append("Total global de ventas: €").append(String.format("%.2f", totalGlobal));

        generador.generarInformeVentas(archivo, titulo, contenido.toString());
    }

    private static void generarInformeInventario() {
        System.out.println("Generando informe de inventario en Word...");

        // Umbral de stock para resaltar productos
        int umbralStock = 10;

        // Nombre del archivo de salida
        String archivo = "informe_inventario.docx";

        try {
            // Crear documento Word
            XWPFDocument documento = new XWPFDocument();

            // Título del informe
            XWPFParagraph titulo = documento.createParagraph();
            titulo.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runTitulo = titulo.createRun();
            runTitulo.setText("Informe de Inventario");
            runTitulo.setBold(true);
            runTitulo.setFontSize(16);

            // Espacio entre título y tabla
            XWPFParagraph espacio = documento.createParagraph();
            espacio.createRun().addBreak();

            // Crear tabla
            XWPFTable tabla = documento.createTable();
            XWPFTableRow encabezado = tabla.getRow(0); // Primera fila (encabezado)
            encabezado.getCell(0).setText("ID Producto");
            encabezado.addNewTableCell().setText("Nombre");
            encabezado.addNewTableCell().setText("Categoría");
            encabezado.addNewTableCell().setText("Precio (€)");
            encabezado.addNewTableCell().setText("Stock");
            encabezado.addNewTableCell().setText("Proveedor");

            // Obtener productos del inventario
            List<Producto> productos = productoDAO.obtenerTodos();
            System.out.println("Productos recuperados para el informe: " + productos);

            // Agregar filas a la tabla
            for (Producto producto : productos) {
                XWPFTableRow fila = tabla.createRow();
                fila.getCell(0).setText(String.valueOf(producto.getId()));
                fila.getCell(1).setText(producto.getNombre());
                fila.getCell(2).setText(producto.getCategoria());
                fila.getCell(3).setText(String.format("%.2f", producto.getPrecio()));
                fila.getCell(4).setText(String.valueOf(producto.getStock()));
                fila.getCell(5).setText(producto.getProveedor() != null ? producto.getProveedor().getNombre() : "N/A");

                // Resaltar productos con stock bajo
                if (producto.getStock() < umbralStock) {
                    for (XWPFTableCell celda : fila.getTableCells()) {
                        celda.setColor("FFCCCC"); // Color de fondo rojo claro
                    }
                }
            }

            // Guardar documento
            try (FileOutputStream out = new FileOutputStream(archivo)) {
                documento.write(out);
            }

            System.out.println("Informe de inventario generado correctamente: " + archivo);
        } catch (Exception e) {
            System.out.println("Error al generar el informe de inventario: " + e.getMessage());
        }
    }

    private static void generarInformeInventarioWord() {
        System.out.println("Generando informe de inventario en Word...");

        try {
            // Crear el documento Word
            XWPFDocument document = new XWPFDocument();

            // Título del documento
            XWPFParagraph titulo = document.createParagraph();
            titulo.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runTitulo = titulo.createRun();
            runTitulo.setText("Informe de Inventario");
            runTitulo.setBold(true);
            runTitulo.setFontSize(16);

            // Espaciado
            document.createParagraph();

            // Crear tabla
            XWPFTable table = document.createTable();

            // Encabezados de la tabla
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("ID Producto");
            headerRow.addNewTableCell().setText("Nombre");
            headerRow.addNewTableCell().setText("Categoría");
            headerRow.addNewTableCell().setText("Stock");

            // Consultar productos con bajo stock
            List<Producto> productos = productoDAO.obtenerTodos();
            int umbralStock = 10; // Umbral definido para stock bajo

            for (Producto producto : productos) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(String.valueOf(producto.getId()));
                row.getCell(1).setText(producto.getNombre());
                row.getCell(2).setText(producto.getCategoria());
                row.getCell(3).setText(String.valueOf(producto.getStock()));

                // Resaltar productos con stock bajo
                if (producto.getStock() < umbralStock) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        CTTc ctTc = cell.getCTTc();
                        CTTcPr tcPr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
                        CTShd shd = tcPr.isSetShd() ? tcPr.getShd() : tcPr.addNewShd();
                        shd.setFill("FF9999"); // Color rojo claro
                    }
                }
            }

            // Guardar el documento
            String archivo = "informe_inventario.docx";
            try (FileOutputStream out = new FileOutputStream(archivo)) {
                document.write(out);
            }
            document.close();

            System.out.println("Informe de inventario generado correctamente: " + archivo);
        } catch (Exception e) {
            System.out.println("Error al generar el informe de inventario: " + e.getMessage());
        }
    }

    private static void importarProveedoresDesdeCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Proveedor proveedor = new Proveedor(
                        values[1], // Nombre
                        values[2], // Dirección
                        values[3], // Teléfono
                        values[4]  // Email
                );
                proveedorDAO.guardar(proveedor);
            }
            System.out.println("Proveedores importados correctamente.");
        } catch (Exception e) {
            System.out.println("Error al importar proveedores: " + e.getMessage());
        }
    }

    private static void importarProductosDesdeCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int contador = 0; // Para contar productos importados
            System.out.println("Iniciando la importación de productos desde el archivo: " + filePath);

            // Leer cada línea del archivo CSV
            while ((line = br.readLine()) != null) {
                // Saltar encabezados si existen
                if (contador == 0 && line.contains("ID")) {
                    contador++;
                    continue;
                }

                // Dividir la línea en columnas usando la coma como separador
                String[] values = line.split(",");

                // Asegurar que la línea tiene el número correcto de columnas
                if (values.length < 5) {
                    System.out.println("Línea inválida: " + line);
                    continue;
                }

                // Crear un producto basado en los datos
                Producto producto = new Producto();
                producto.setNombre(values[1].trim());
                producto.setCategoria(values[2].trim());
                producto.setPrecio(Double.parseDouble(values[3].trim()));
                producto.setStock(Integer.parseInt(values[4].trim()));

                // Buscar proveedor si se incluye (opcional)

                if (values.length > 5 && !values[5].trim().isEmpty()) {
                    String proveedorNombre = values[5].trim();
                    Proveedor proveedor = proveedorDAO.buscarPorNombre(proveedorNombre);

                    if (proveedor != null) {
                        producto.setProveedor(proveedor);
                    } else {
                        System.out.println("Proveedor no encontrado: " + proveedorNombre + ". Producto: " + values[1]);
                        // Opcional: registrar un nuevo proveedor automáticamente
                        // Proveedor nuevoProveedor = new Proveedor(proveedorNombre, "Dirección genérica", "Teléfono genérico", "Email genérico");
                        // proveedorDAO.guardar(nuevoProveedor);
                        // producto.setProveedor(nuevoProveedor);
                    }
                }

                // Guardar el producto en la base de datos
                productoDAO.guardar(producto);
                contador++;
            }

            System.out.println("Importación completada. Total de productos importados: " + contador);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Archivo no encontrado: " + filePath);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error en el formato de los datos del archivo CSV: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error durante la importación de productos: " + e.getMessage());
        }
    }




    private static void cerrarConexiones() {
        productoDAO.cerrar();
        proveedorDAO.cerrar();
        facturaDAO.cerrar();
        detalleFacturaDAO.cerrar();
    }
}
