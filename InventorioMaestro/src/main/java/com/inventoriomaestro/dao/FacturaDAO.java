package com.inventoriomaestro.dao;

import com.inventoriomaestro.entidades.Factura;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class FacturaDAO {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventorioMaestroPU");
    private EntityManager entityManager;

    public FacturaDAO() {
        this.entityManager = emf.createEntityManager();
    }

    public void guardar(Factura factura) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(factura);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    public Factura encontrarPorId(long id) {
        return entityManager.find(Factura.class, id);
    }

    public List<Factura> obtenerTodos() {
        return entityManager.createQuery("SELECT f FROM Factura f", Factura.class).getResultList();
    }

    public Factura actualizar(Factura factura) {
        try {
            entityManager.getTransaction().begin();
            Factura facturaActualizada = entityManager.merge(factura);
            entityManager.getTransaction().commit();
            return facturaActualizada;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    public List<Factura> obtenerFacturasPorTipo(String tipo) {
        TypedQuery<Factura> query = entityManager.createQuery(
                "SELECT f FROM Factura f WHERE f.tipoDeFactura = :tipo", Factura.class);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }

    public List<Factura> obtenerFacturasPorTipoYFechas(String tipo, Date inicio, Date fin) {
        return entityManager.createQuery(
                        "SELECT f FROM Factura f WHERE f.tipoDeFactura = :tipo AND f.fecha BETWEEN :inicio AND :fin",
                        Factura.class)
                .setParameter("tipo", tipo)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    public void eliminar(long id) {
        Factura factura = encontrarPorId(id);
        if (factura != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(factura);
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
