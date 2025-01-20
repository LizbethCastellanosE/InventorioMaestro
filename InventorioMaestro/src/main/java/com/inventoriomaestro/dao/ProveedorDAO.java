package com.inventoriomaestro.dao;

import com.inventoriomaestro.entidades.Proveedor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.List;

public class ProveedorDAO {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventorioMaestroPU");
    private EntityManager entityManager;

    public ProveedorDAO() {
        this.entityManager = emf.createEntityManager();
    }

    public void guardar(Proveedor proveedor) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(proveedor);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    public Proveedor encontrarPorId(long id) {
        return entityManager.find(Proveedor.class, id);  
    }


    public List<Proveedor> obtenerTodos() {
        return entityManager.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();
    }


    public Proveedor actualizar(Proveedor proveedor) {
        try {
            entityManager.getTransaction().begin();
            Proveedor proveedorActualizado = entityManager.merge(proveedor);
            entityManager.getTransaction().commit();
            return proveedorActualizado;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    public Proveedor buscarPorNombre(String nombre) {
        try {
            List<Proveedor> resultados = entityManager.createQuery(
                            "SELECT p FROM Proveedor p WHERE LOWER(TRIM(p.nombre)) = LOWER(TRIM(:nombre))", Proveedor.class)
                    .setParameter("nombre", nombre)
                    .getResultList();

            if (resultados.isEmpty()) {
                return null;
            } else if (resultados.size() == 1) {
                return resultados.get(0);
            } else {
                System.out.println("Advertencia: múltiples proveedores encontrados para el nombre: " + nombre);
                return resultados.get(0);
            }
        } catch (Exception e) {
            System.out.println("Error buscando proveedor por nombre: " + e.getMessage());
            return null;
        }
    }


    public void eliminar(long id) {
        Proveedor proveedor = encontrarPorId(id);
        if (proveedor != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(proveedor);
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
