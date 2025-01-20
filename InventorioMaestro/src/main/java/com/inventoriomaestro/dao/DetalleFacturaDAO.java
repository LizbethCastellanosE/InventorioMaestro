package com.inventoriomaestro.dao;

import com.inventoriomaestro.entidades.DetalleFactura;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DetalleFacturaDAO {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventorioMaestroPU");
    private EntityManager entityManager;

    public DetalleFacturaDAO() {
        this.entityManager = emf.createEntityManager();
    }

    public void guardar(DetalleFactura DetalleFactura) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(DetalleFactura);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    public DetalleFactura encontrarPorId(long id) {
        return entityManager.find(DetalleFactura.class, id);
    }

    public List<DetalleFactura> obtenerTodos() {
        return entityManager.createQuery("SELECT l FROM DetalleFactura l", DetalleFactura.class).getResultList();
    }

    public DetalleFactura actualizar(DetalleFactura DetalleFactura) {
        try {
            entityManager.getTransaction().begin();
            DetalleFactura DetalleFacturaActualizada = entityManager.merge(DetalleFactura);
            return DetalleFacturaActualizada;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    public void eliminar(long id) {
        DetalleFactura DetalleFactura = encontrarPorId(id);
        if (DetalleFactura != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(DetalleFactura );
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw e;
            }
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
