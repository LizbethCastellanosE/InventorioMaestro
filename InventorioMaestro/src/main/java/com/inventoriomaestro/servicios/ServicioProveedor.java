package com.inventoriomaestro.servicios;

import com.inventoriomaestro.dao.ProveedorDAO;
import com.inventoriomaestro.entidades.Proveedor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

public class ServicioProveedor {

    private final ProveedorDAO proveedorDAO;

    public ServicioProveedor(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    public void registrarProveedor(Scanner scanner) {
        System.out.println("Registrar nuevo proveedor:");
        System.out.println("Ingrese el nombre del proveedor:");
        String nombreProveedor = scanner.nextLine().trim();
        if (nombreProveedor.isEmpty()) {
            System.out.println("El nombre del proveedor no puede estar vacío.");
            return;
        }

        boolean existe = proveedorDAO.obtenerTodos().stream()
                .anyMatch(proveedor -> proveedor.getNombre() != null && proveedor.getNombre().equalsIgnoreCase(nombreProveedor));
        if (existe) {
            System.out.println("El proveedor ya existe.");
            return;
        }

        System.out.println("Ingrese la dirección del proveedor:");
        String direccion = scanner.nextLine().trim();
        if (direccion.isEmpty()) {
            System.out.println("La dirección del proveedor no puede estar vacía.");
            return;
        }

        System.out.println("Ingrese el teléfono del proveedor:");
        String telefono = scanner.nextLine().trim();
        if (telefono.isEmpty()) {
            System.out.println("El teléfono del proveedor no puede estar vacío.");
            return;
        }

        if (!esTelefonoValido(telefono)) {
            System.out.println("Formato de teléfono inválido. Debe contener solo números y tener entre 8 y 12 dígitos.");
            return;
        }

        System.out.println("Ingrese el email del proveedor:");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("El email del proveedor no puede estar vacío.");
            return;
        }

        if (!esEmailValido(email)) {
            System.out.println("Formato de email inválido. Asegúrate de incluir '@' y un dominio válido.");
            return;
        }

        try {
            Proveedor nuevoProveedor = new Proveedor(nombreProveedor, direccion, telefono, email);
            proveedorDAO.guardar(nuevoProveedor);
            System.out.println("Proveedor registrado con éxito.");
        } catch (Exception e) {
            System.out.println("Error al registrar el proveedor: " + e.getMessage());
        }
    }

    private boolean esTelefonoValido(String telefono) {
        if (telefono.length() < 8 || telefono.length() > 12) return false;
        for (char c : telefono.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean esEmailValido(String email) {
        if (!email.contains("@") || !email.contains(".")) return false;
        int indexArroba = email.indexOf('@');
        int indexUltimoPunto = email.lastIndexOf('.');
        return indexArroba > 0 && indexUltimoPunto > indexArroba && indexUltimoPunto < email.length() - 1;
    }

    public void consultarProveedores() {
        System.out.println("Lista de proveedores registrados:");


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

    public void modificarProveedor(Scanner scanner) {
        System.out.println("Ingrese el ID del proveedor a modificar:");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Proveedor proveedor = proveedorDAO.encontrarPorId(id);

        if (proveedor != null) {
            System.out.println("Proveedor encontrado: " + proveedor);

            System.out.println("Ingrese el nuevo nombre del proveedor (actual: " + proveedor.getNombre() + "):");
            String nuevoNombre = scanner.nextLine().trim();

            System.out.println("Ingrese la nueva dirección del proveedor (actual: " + proveedor.getDireccion() + "):");
            String nuevaDireccion = scanner.nextLine().trim();

            System.out.println("Ingrese el nuevo teléfono del proveedor (actual: " + proveedor.getTelefono() + "):");
            String nuevoTelefono = scanner.nextLine().trim();

            System.out.println("Ingrese el nuevo email del proveedor (actual: " + proveedor.getEmail() + "):");
            String nuevoEmail = scanner.nextLine().trim();


            if (!nuevoNombre.isEmpty()) proveedor.setNombre(nuevoNombre);
            if (!nuevaDireccion.isEmpty()) proveedor.setDireccion(nuevaDireccion);

            if (!nuevoTelefono.isEmpty()) {
                if (!esTelefonoValido(nuevoTelefono)) {
                    System.out.println("Formato de teléfono inválido. Debe contener solo números y tener entre 10 y 15 dígitos.");
                    return;
                }
                proveedor.setTelefono(nuevoTelefono);
            }

            if (!nuevoEmail.isEmpty()) {
                if (!esEmailValido(nuevoEmail)) {
                    System.out.println("Formato de email inválido. Asegúrate de incluir '@' y un dominio válido.");
                    return;
                }
                proveedor.setEmail(nuevoEmail);
            }

            proveedorDAO.actualizar(proveedor);
            System.out.println("Proveedor modificado con éxito.");
        } else {
            System.out.println("Proveedor con ID " + id + " no encontrado.");
        }
    }

    public void eliminarProveedor(Scanner scanner) {
        System.out.println("Ingrese el ID del proveedor a eliminar:");
        Long id = scanner.nextLong();
        scanner.nextLine();

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

    public void importarProveedoresDesdeCSV(String filePath) {
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
}
