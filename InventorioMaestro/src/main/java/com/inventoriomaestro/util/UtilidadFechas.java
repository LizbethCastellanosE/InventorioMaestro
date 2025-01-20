package com.inventoriomaestro.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class UtilidadFechas {

    public static Date validarFecha(Scanner scanner) {
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

    public static Date parsearFecha(String fechaInput) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(fechaInput);
        } catch (ParseException e) {
            System.out.println("Error: Formato de fecha inválido.");
            return null; // Retorna null si la fecha no es válida
        }
    }

}
