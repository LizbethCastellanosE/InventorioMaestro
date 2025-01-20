package com.inventoriomaestro.generador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoriomaestro.entidades.Factura;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ExportadorFacturas {

    // Exportar facturación a JSON
    public void exportarFacturacionAJSON(List<Factura> facturas, String archivo) {
        if (facturas == null || facturas.isEmpty()) {
            System.out.println("No hay facturas para exportar a JSON.");
            return;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(archivo), facturas);
            System.out.println("Facturación exportada a JSON correctamente: " + archivo);
        } catch (Exception e) {
            System.out.println("Error al exportar facturación a JSON: " + e.getMessage());
        }
    }

    // Exportar facturación a XML
    public void exportarFacturacionAXML(List<Factura> facturas, String archivo) {
        if (facturas == null || facturas.isEmpty()) {
            System.out.println("No hay facturas para exportar a XML.");
            return;
        }
        try {
            // Crear elemento raíz
            Element root = new Element("Facturas");
            Document doc = new Document(root);

            // Iterar sobre facturas y construir XML
            for (Factura factura : facturas) {
                Element facturaElement = new Element("Factura");
                facturaElement.setAttribute("ID", String.valueOf(factura.getId()));
                facturaElement.setAttribute("Fecha", factura.getFecha().toString());
                facturaElement.setAttribute("Tipo", factura.getTipoDeFactura());

                if (factura.getProveedor() != null) {
                    Element proveedorElement = new Element("Proveedor");
                    proveedorElement.setText(factura.getProveedor().getNombre());
                    facturaElement.addContent(proveedorElement);
                }

                Element lineasElement = new Element("Lineas");
                factura.getLineas().forEach(detalle -> {
                    Element detalleElement = new Element("Detalle");
                    detalleElement.setAttribute("Producto", detalle.getProducto().getNombre());
                    detalleElement.setAttribute("Cantidad", String.valueOf(detalle.getCantidad()));
                    detalleElement.setAttribute("PrecioUnitario", String.valueOf(detalle.getPrecioUnitario()));
                    lineasElement.addContent(detalleElement);
                });

                facturaElement.addContent(lineasElement);
                root.addContent(facturaElement);
            }

            // Escribir archivo XML
            try (FileWriter writer = new FileWriter(archivo)) {
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputter.output(doc, writer);
                System.out.println("Facturación exportada a XML correctamente: " + archivo);
            }
        } catch (Exception e) {
            System.out.println("Error al exportar facturación a XML: " + e.getMessage());
        }
    }
}
