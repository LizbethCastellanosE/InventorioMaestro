package com.inventoriomaestro.gestion;

import com.inventoriomaestro.entidades.Proveedor;
import com.inventoriomaestro.dao.ProveedorDAO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class ImportarProveedores {

    private ProveedorDAO proveedorDAO;

    public ImportarProveedores(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    public void importar() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("proveedores.csv");

            if (inputStream == null) {
                System.out.println("Archivo proveedores.csv no encontrado en resources.");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String linea;

            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");

                if (datos.length != 5) {
                    System.out.println("Error en la línea: " + linea + ". Se esperaban 5 campos.");
                    continue;
                }

                try {
                    Proveedor proveedor = new Proveedor();
                    proveedor.setId(Long.parseLong(datos[0].trim()));
                    proveedor.setNombre(datos[1].trim());
                    proveedor.setDireccion(datos[2].trim());
                    proveedor.setTelefono(datos[3].trim());
                    proveedor.setEmail(datos[4].trim());

                    proveedorDAO.guardar(proveedor);
                } catch (Exception e) {
                    System.out.println("Error al guardar el proveedor: " + linea);
                    e.printStackTrace();
                }
            }

            System.out.println("Proveedores importados correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
