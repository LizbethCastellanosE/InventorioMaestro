package com.inventoriomaestro.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class UtilidadFechas {

    //Cree esta clase paar poder tener mas organizado el codigo
    //y poder reutilizarlo en otras clasees . Es para la validacon d las fechas y el parsearFecha lo agregue para poder
    //usarlo en la clase de inventario para poder validar la fecha de caducidad de los productos

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
            return null; //si la fecha no es válida(null)
        }
    }

}
