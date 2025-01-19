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

    // Constructor para inicializar el EntityManager
    public ProductoDAO() {
        this.entityManager = emf.createEntityManager();
    }

    // Guardar un nuevo producto
    public void guardar(Producto producto) {
        try {
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

    // Buscar un producto por su ID
    public Producto encontrarPorId(long id) {
        return entityManager.find(Producto.class, id);  // Busca el producto por su ID
    }

    // Obtener todos los productos
    public List<Producto> obtenerTodos() {
        return entityManager.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();  // Obtiene todos los productos
    }

    // Actualizar un producto
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

    // Eliminar un producto por su ID
    public void eliminar(long id) {
        Producto producto = encontrarPorId(id);  // Busca el producto por ID
        if (producto != null) {
            try {
                entityManager.getTransaction().begin();  // Inicia la transacción
                entityManager.remove(producto);  // Elimina el producto
                entityManager.getTransaction().commit();  // Confirma la transacción
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw e;
            }
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
            producto.setStock(producto.getStock() + cantidad);  // Actualiza el stock (sumando o restando según corresponda)
            actualizar(producto);  // Actualiza el producto en la base de datos
        }
    }

    // Cerrar el EntityManager
    public void cerrar() {
        if (entityManager != null) {
            entityManager.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
}
