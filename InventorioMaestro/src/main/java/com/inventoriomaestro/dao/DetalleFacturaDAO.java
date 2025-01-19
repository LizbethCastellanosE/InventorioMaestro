package com.inventoriomaestro.dao;

import com.inventoriomaestro.entidades.DetalleFactura;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DetalleFacturaDAO {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventorioMaestroPU");
    private EntityManager entityManager;

    // Constructor para inicializar el EntityManager
    public DetalleFacturaDAO() {
        this.entityManager = emf.createEntityManager();
    }

    // Guardar una nueva línea de factura
    public void guardar(DetalleFactura DetalleFactura) {
        try {
            entityManager.getTransaction().begin();  // Inicia la transacción
            entityManager.persist(DetalleFactura);  // Guarda la línea de factura
            entityManager.getTransaction().commit();  // Confirma la transacción
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;  // Lanza la excepción para manejarla externamente
        }
    }

    // Buscar una línea de factura por su ID
    public DetalleFactura encontrarPorId(long id) {
        return entityManager.find(DetalleFactura.class, id);  // Busca la línea de factura por su ID
    }

    // Obtener todas las líneas de factura
    public List<DetalleFactura> obtenerTodos() {
        return entityManager.createQuery("SELECT l FROM DetalleFactura l", DetalleFactura.class).getResultList();  // Obtiene todas las líneas de factura
    }

    // Actualizar una línea de factura
    public DetalleFactura actualizar(DetalleFactura DetalleFactura) {
        try {
            entityManager.getTransaction().begin();  // Inicia la transacción
            DetalleFactura DetalleFacturaActualizada = entityManager.merge(DetalleFactura);  // Actualiza la línea de factura
            entityManager.getTransaction().commit();  // Confirma la transacción
            return DetalleFacturaActualizada;  // Devuelve la línea de factura actualizada
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    // Eliminar una línea de factura por su ID
    public void eliminar(long id) {
        DetalleFactura DetalleFactura = encontrarPorId(id);  // Busca la línea de factura por ID
        if (DetalleFactura != null) {
            try {
                entityManager.getTransaction().begin();  // Inicia la transacción
                entityManager.remove(DetalleFactura );  // Elimina la línea de factura
                entityManager.getTransaction().commit();  // Confirma la transacción
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw e;
            }
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
