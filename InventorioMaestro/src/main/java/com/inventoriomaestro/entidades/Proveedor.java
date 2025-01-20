package com.inventoriomaestro.entidades;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedores")
public class Proveedor {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private long id;

    @Column(name = "nombre_proveedor")
    private String nombre;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    public Proveedor() {
    }

    public Proveedor(String nombre, String direccion, String telefono, String email) {
        setNombre(nombre);
        setDireccion(direccion);
        setTelefono(telefono);
        setEmail(email);
    }

    public Proveedor(long id, String nombre, String direccion, String telefono, String email) {
        this.id = id;
        setNombre(nombre);
        setDireccion(direccion);
        setTelefono(telefono);
        setEmail(email);
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        this.direccion = direccion.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío.");
        }

        String telefonoLimpio = telefono.trim().replaceAll("\\s+", "");
        if (!esTelefonoValido(telefonoLimpio)) {
            throw new IllegalArgumentException("Formato de teléfono inválido. Debe contener entre 8 y 12 dígitos.");
        }
        this.telefono = telefonoLimpio;
    }

    private boolean esTelefonoValido(String telefono) {
        if (telefono.length() < 8 || telefono.length() > 12) {
            return false;
        }
        for (char c : telefono.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }
        if (!esEmailValido(email)) {
            throw new IllegalArgumentException("Formato de correo inválido. Asegúrate de incluir '@' y un dominio válido.");
        }
        this.email = email.trim();
    }

    //Vslidcion del email con el @ y el punto
    private boolean esEmailValido(String email) {
        int indexArroba = email.indexOf('@');
        int indexUltimoPunto = email.lastIndexOf('.');
        return indexArroba > 0 && indexUltimoPunto > indexArroba && indexUltimoPunto < email.length() - 1;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
        producto.setProveedor(this);
    }

    public void eliminarProducto(Producto producto) {
        productos.remove(producto);
        producto.setProveedor(null);
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
