package com.inventoriomaestro.dao;

import com.inventoriomaestro.entidades.Producto;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.List;

public class ProductoDAO {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventorioMaestroPU");
    private EntityManager entityManager;

    public ProductoDAO() {
        this.entityManager = emf.createEntityManager();
    }

    public void guardar(Producto producto) {
        try {
            // Validar si ya existe un producto con el mismo nombre y categoría
            Producto existente = buscarPorNombreYCategoria(producto.getNombre(), producto.getCategoria());
            if (existente != null) {
                throw new IllegalArgumentException("Ya existe un producto con el mismo nombre y categoría.");
            }

            entityManager.getTransaction().begin();  // Inicia la transacción
            entityManager.persist(producto);  // Guarda el producto
            entityManager.getTransaction().commit();  // Confirma la transacción
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;  // Lanza la excepción para manejarla externamente
        }
    }

    public Producto encontrarPorId(long id) {
        return entityManager.find(Producto.class, id);  // Busca el producto por su ID
    }

    public List<Producto> obtenerTodos() {
        return entityManager.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();  // Obtiene todos los productos
    }

    public Producto actualizar(Producto producto) {
        try {
            entityManager.getTransaction().begin();  // Inicia la transacción
            Producto productoActualizado = entityManager.merge(producto);  // Actualiza el producto
            entityManager.getTransaction().commit();  // Confirma la transacción
            return productoActualizado;  // Devuelve el producto actualizado
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    public void eliminar(long id) {
        Producto producto = encontrarPorId(id);  // Busca el producto por ID
        if (producto != null) {
            try {
                // Verificar relaciones (ejemplo: facturas asociadas)
                if (producto.getFacturas() != null && !producto.getFacturas().isEmpty()) {
                    throw new IllegalArgumentException("No se puede eliminar el producto porque está asociado a facturas.");
                }

                entityManager.getTransaction().begin();  // Inicia la transacción
                entityManager.remove(producto);  // Elimina el producto
                entityManager.getTransaction().commit();  // Confirma la transacción
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
    }


    public Producto buscarPorNombreYCategoria(String nombre, String categoria) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM Producto p WHERE LOWER(TRIM(p.nombre)) = LOWER(TRIM(:nombre)) AND LOWER(TRIM(p.categoria)) = LOWER(TRIM(:categoria))", Producto.class)
                    .setParameter("nombre", nombre)
                    .setParameter("categoria", categoria)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // No existe el producto
        }
    }


    public void actualizarStock(long productoId, int cantidad) {
        Producto producto = encontrarPorId(productoId);  // Busca el producto por ID
        if (producto != null) {
            int nuevoStock = producto.getStock() + cantidad;
            if (nuevoStock < 0) {
                throw new IllegalArgumentException("El stock resultante no puede ser negativo.");
            }
            producto.setStock(nuevoStock);  // Actualiza el stock
            actualizar(producto);  // Actualiza el producto en la base de datos
        } else {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
        }
    }

    public void cerrar() {
        if (entityManager != null) {
            entityManager.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
}
