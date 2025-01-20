package com.inventoriomaestro.principal;

import com.inventoriomaestro.dao.*;
import com.inventoriomaestro.entidades.*;
import com.inventoriomaestro.generador.ExportadorFacturas;
import com.inventoriomaestro.generador.GeneradorPDF;
import com.inventoriomaestro.gestion.*;
import com.inventoriomaestro.servicios.ServicioFactura;
import com.inventoriomaestro.servicios.ServicioInforme;
import com.inventoriomaestro.servicios.ServicioProducto;
import com.inventoriomaestro.servicios.ServicioProveedor;
import com.inventoriomaestro.util.UtilidadFechas;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static final ProductoDAO productoDAO = new ProductoDAO();
    public static final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private static final FacturaDAO facturaDAO = new FacturaDAO();
    private static final DetalleFacturaDAO detalleFacturaDAO = new DetalleFacturaDAO();

    // Instancias de servicios
    private static final ServicioProveedor servicioProveedor = new ServicioProveedor(proveedorDAO);
    private static final ServicioFactura servicioFactura = new ServicioFactura(facturaDAO, productoDAO);
    private static final ServicioProducto servicioProducto = new ServicioProducto(productoDAO);
    private static final ServicioInforme servicioInforme = new ServicioInforme(facturaDAO, productoDAO);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            // Menú general que lleva a otros menus
            System.out.println("Seleccione una opción:");
            System.out.println("1. Gestión de productos");
            System.out.println("2. Gestión de proveedores");
            System.out.println("3. Gestión de facturas");
            System.out.println("4. Importar/exportar datos");
            System.out.println("5. Generar informes");
            System.out.println("6. Exportar facturación");
            System.out.println("7. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine();

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
                    servicioFactura.exportarFacturacion(scanner);
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

    //Menu que gestiona los productos y su información
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
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    servicioProducto.registrarProducto(scanner);
                    break;
                case 2:
                    servicioProducto.consultarProductos();
                    break;
                case 3:
                    servicioProducto.modificarProducto(scanner);
                    break;
                case 4:
                    servicioProducto.eliminarProducto(scanner);
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

    // Menu que gestiona los proveedores y su información
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
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    servicioProveedor.registrarProveedor(scanner);
                    break;
                case 2:
                    servicioProveedor.consultarProveedores();
                    break;
                case 3:
                    servicioProveedor.modificarProveedor(scanner);
                    break;
                case 4:
                    servicioProveedor.eliminarProveedor(scanner);
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

    // Menu que gestiona las facturas de compra y venta del negocio
    private static void gestionFacturas(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("Gestión de facturas:");
            System.out.println("1. Registrar compra");
            System.out.println("2. Registrar venta");
            System.out.println("3. Volver al menú principal");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    servicioFactura.registrarCompra(scanner);
                    break;
                case 2:
                    servicioFactura.registrarVenta(scanner);
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

    // Menu que importa y exporta datos de productos, proveedores y facturación en csv, xml y json
    private static void importarExportarDatos(Scanner scanner) {
        System.out.println("Importar/exportar datos:");
        System.out.println("1. Importar productos desde CSV");
        System.out.println("2. Importar proveedores desde CSV");
        System.out.println("3. Exportar facturación a JSON");
        System.out.println("4. Exportar facturación a XML");
        System.out.println("5. Volver al menú principal");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                System.out.println("Ingrese la ruta del archivo CSV de productos:");
                String rutaProductos = scanner.nextLine();
                servicioProducto.importarProductosDesdeCSV(rutaProductos);
                break;
            case 2:
                System.out.println("Ingrese la ruta del archivo CSV de proveedores:");
                String rutaProveedores = scanner.nextLine();
                servicioProveedor.importarProveedoresDesdeCSV(rutaProveedores);
                break;
            case 3:
            case 4:
                System.out.println("Ingrese la fecha de inicio (formato: yyyy-MM-dd) o presione Enter para todas las fechas:");
                String fechaInicioInput = scanner.nextLine();
                System.out.println("Ingrese la fecha de fin (formato: yyyy-MM-dd) o presione Enter para todas las fechas:");
                String fechaFinInput = scanner.nextLine();

                Date fechaInicio = fechaInicioInput.isBlank() ? null : UtilidadFechas.parsearFecha(fechaInicioInput);
                Date fechaFin = fechaFinInput.isBlank() ? null : UtilidadFechas.parsearFecha(fechaFinInput);

                if (fechaInicio == null && !fechaInicioInput.isBlank() || fechaFin == null && !fechaFinInput.isBlank()) {
                    System.out.println("Fechas inválidas. Operación cancelada.");
                    return;
                }

                if (opcion == 3) {
                    System.out.println("Ingrese la ruta para exportar facturación a JSON:");
                    String rutaJSON = scanner.nextLine();
                    List<Factura> facturasJSON = facturaDAO.obtenerFacturasPorTipoYFechas("VENTA", fechaInicio, fechaFin);
                    new ExportadorFacturas().exportarFacturacionAJSON(facturasJSON, rutaJSON);
                } else if (opcion == 4) {
                    System.out.println("Ingrese la ruta para exportar facturación a XML:");
                    String rutaXML = scanner.nextLine();
                    List<Factura> facturasXML = facturaDAO.obtenerFacturasPorTipoYFechas("VENTA", fechaInicio, fechaFin);
                    new ExportadorFacturas().exportarFacturacionAXML(facturasXML, rutaXML);
                }
                break;
            case 5:
                System.out.println("Volviendo al menú principal...");
                break;
            default:
                System.out.println("Opción no válida.");
                break;
        }
    }

    // Menu que genera informes de invntario y ventas en PDF y Word
    private static void generarInformes(Scanner scanner) {
        System.out.println("Generar informes:");
        System.out.println("1. Generar informe de ventas (PDF)");
        System.out.println("2. Generar informe de inventario (Word)");
        System.out.println("3. Volver al menú principal");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                servicioInforme.generarInformeVentas();
                break;
            case 2:
                System.out.println("Ingrese la ruta del archivo Word:");
                String archivoWord = scanner.nextLine();
                System.out.println("Ingrese el umbral de stock (por ejemplo, 10):");
                int umbralStock = scanner.nextInt();
                scanner.nextLine();
                servicioInforme.generarInformeInventarioWord(archivoWord, umbralStock);
                break;
            case 3:
                System.out.println("Volviendo al menú principal...");
                break;
            default:
                System.out.println("Opción no válida.");
                break;
        }
    }

    // Cierra las conexiones a la base de datos
    private static void cerrarConexiones() {
        productoDAO.cerrar();
        proveedorDAO.cerrar();
        facturaDAO.cerrar();
        detalleFacturaDAO.cerrar();
    }
}
