package com.inventoriomaestro.servicios;

import com.inventoriomaestro.dao.FacturaDAO;
import com.inventoriomaestro.dao.ProductoDAO;
import com.inventoriomaestro.entidades.DetalleFactura;
import com.inventoriomaestro.entidades.Factura;
import com.inventoriomaestro.entidades.Producto;
import com.inventoriomaestro.generador.GeneradorPDF;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;

import java.io.FileOutputStream;
import java.util.List;

public class ServicioInforme {

    private final FacturaDAO facturaDAO;
    private final ProductoDAO productoDAO;

    public ServicioInforme(FacturaDAO facturaDAO, ProductoDAO productoDAO) {
        this.facturaDAO = facturaDAO;
        this.productoDAO = productoDAO;
    }

    public void generarInformeVentas() {
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

    public void generarInformeInventario() {
        System.out.println("Generando informe de inventario en Word...");

        int umbralStock = 10;
        String archivo = "informe_inventario.docx";

        try {
            XWPFDocument documento = new XWPFDocument();

            XWPFParagraph titulo = documento.createParagraph();
            titulo.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runTitulo = titulo.createRun();
            runTitulo.setText("Informe de Inventario");
            runTitulo.setBold(true);
            runTitulo.setFontSize(16);

            XWPFParagraph espacio = documento.createParagraph();
            espacio.createRun().addBreak();

            XWPFTable tabla = documento.createTable();
            XWPFTableRow encabezado = tabla.getRow(0);
            encabezado.getCell(0).setText("ID Producto");
            encabezado.addNewTableCell().setText("Nombre");
            encabezado.addNewTableCell().setText("Categoría");
            encabezado.addNewTableCell().setText("Precio (€)");
            encabezado.addNewTableCell().setText("Stock");
            encabezado.addNewTableCell().setText("Proveedor");

            List<Producto> productos = productoDAO.obtenerTodos();
            for (Producto producto : productos) {
                XWPFTableRow fila = tabla.createRow();
                fila.getCell(0).setText(String.valueOf(producto.getId()));
                fila.getCell(1).setText(producto.getNombre());
                fila.getCell(2).setText(producto.getCategoria());
                fila.getCell(3).setText(String.format("%.2f", producto.getPrecio()));
                fila.getCell(4).setText(String.valueOf(producto.getStock()));
                fila.getCell(5).setText(producto.getProveedor() != null ? producto.getProveedor().getNombre() : "N/A");

                if (producto.getStock() < umbralStock) {
                    for (XWPFTableCell celda : fila.getTableCells()) {
                        celda.setColor("FFCCCC");
                    }
                }
            }

            try (FileOutputStream out = new FileOutputStream(archivo)) {
                documento.write(out);
            }

            System.out.println("Informe de inventario generado correctamente: " + archivo);
        } catch (Exception e) {
            System.out.println("Error al generar el informe de inventario: " + e.getMessage());
        }
    }

    public void generarInformeInventarioWord(String archivoWord, int umbralStock) {
        List<Producto> productos = productoDAO.obtenerTodos();
        List<Producto> productosBajoStock = productos.stream()
                .filter(producto -> producto.getStock() < umbralStock)
                .toList();

        try {
            XWPFDocument document = new XWPFDocument();

            XWPFParagraph titulo = document.createParagraph();
            titulo.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runTitulo = titulo.createRun();
            runTitulo.setText("Informe de Inventario");
            runTitulo.setBold(true);
            runTitulo.setFontSize(16);

            XWPFTable table = document.createTable();

            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("ID Producto");
            headerRow.addNewTableCell().setText("Nombre");
            headerRow.addNewTableCell().setText("Categoría");
            headerRow.addNewTableCell().setText("Stock");

            for (Producto producto : productosBajoStock) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(String.valueOf(producto.getId()));
                row.getCell(1).setText(producto.getNombre());
                row.getCell(2).setText(producto.getCategoria());
                row.getCell(3).setText(String.valueOf(producto.getStock()));
            }

            try (FileOutputStream out = new FileOutputStream(archivoWord)) {
                document.write(out);
            }
            document.close();

            System.out.println("Informe de inventario generado correctamente: " + archivoWord);
        } catch (Exception e) {
            System.out.println("Error al generar el informe de inventario: " + e.getMessage());
        }
    }
}
