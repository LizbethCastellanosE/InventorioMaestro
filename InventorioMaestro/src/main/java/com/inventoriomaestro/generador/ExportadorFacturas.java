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
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File(archivo), facturas);
            System.out.println("Facturación exportada a JSON correctamente: " + archivo);
        } catch (Exception e) {
            System.out.println("Error al exportar facturación a JSON: " + e.getMessage());
        }
    }

    // Exportar facturación a XML
    public void exportarFacturacionAXML(List<Factura> facturas, String archivo) {
        try {
            Element root = new Element("Facturas");
            Document doc = new Document(root);

            for (Factura factura : facturas) {
                Element facturaElement = new Element("Factura");

                facturaElement.addContent(new Element("ID").setText(String.valueOf(factura.getId())));
                facturaElement.addContent(new Element("Fecha").setText(factura.getFecha().toString()));
                facturaElement.addContent(new Element("Tipo").setText(factura.getTipoDeFactura()));
                if (factura.getProveedor() != null) {
                    facturaElement.addContent(new Element("Proveedor").setText(factura.getProveedor().getNombre()));
                }

                Element lineasElement = new Element("Lineas");
                factura.getLineas().forEach(detalle -> {
                    Element detalleElement = new Element("Detalle");
                    detalleElement.addContent(new Element("Producto").setText(detalle.getProducto().getNombre()));
                    detalleElement.addContent(new Element("Cantidad").setText(String.valueOf(detalle.getCantidad())));
                    detalleElement.addContent(new Element("PrecioUnitario").setText(String.valueOf(detalle.getPrecioUnitario())));
                    lineasElement.addContent(detalleElement);
                });

                facturaElement.addContent(lineasElement);
                root.addContent(facturaElement);
            }

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            FileWriter writer = new FileWriter(archivo);
            outputter.output(doc, writer);
            writer.close();
            System.out.println("Facturación exportada a XML correctamente: " + archivo);
        } catch (Exception e) {
            System.out.println("Error al exportar facturación a XML: " + e.getMessage());
        }
    }
}
