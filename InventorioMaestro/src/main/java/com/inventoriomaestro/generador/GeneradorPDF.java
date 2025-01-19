package com.inventoriomaestro.generador;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileOutputStream;
import java.io.IOException;

public class GeneradorPDF {

    public void generarInformeVentas(String archivo, String titulo, String contenido) {
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Añadir título
            document.add(new Paragraph(titulo)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(16));

            // Añadir contenido
            document.add(new Paragraph(contenido)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(12));

            document.close();
            System.out.println("Informe generado correctamente: " + archivo);
        } catch (IOException e) {
            System.out.println("Error al generar el archivo PDF: " + e.getMessage());
        }
    }
}
