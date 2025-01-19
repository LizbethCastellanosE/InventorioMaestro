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

    // Constructor para inicializar el EntityManager
    public ProveedorDAO() {
        this.entityManager = emf.createEntityManager();
    }

    // Guardar un nuevo proveedor
    public void guardar(Proveedor proveedor) {
        try {
            entityManager.getTransaction().begin();  // Inicia la transacción
            entityManager.persist(proveedor);  // Guarda el proveedor
            entityManager.getTransaction().commit();  // Confirma la transacción
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;  // Lanza la excepción para manejarla externamente
        }
    }

    // Buscar un proveedor por su ID
    public Proveedor encontrarPorId(long id) {
        return entityManager.find(Proveedor.class, id);  // Busca el proveedor por su ID
    }

    // Obtener todos los proveedores
    public List<Proveedor> obtenerTodos() {
        return entityManager.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();  // Obtiene todos los proveedores
    }

    // Actualizar un proveedor
    public Proveedor actualizar(Proveedor proveedor) {
        try {
            entityManager.getTransaction().begin();  // Inicia la transacción
            Proveedor proveedorActualizado = entityManager.merge(proveedor);  // Actualiza el proveedor
            entityManager.getTransaction().commit();  // Confirma la transacción
            return proveedorActualizado;  // Devuelve el proveedor actualizado
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
                return null; // Si no se encuentra el proveedor
            } else if (resultados.size() == 1) {
                return resultados.get(0); // Si hay un único resultado
            } else {
                System.out.println("Advertencia: múltiples proveedores encontrados para el nombre: " + nombre);
                return resultados.get(0); // Seleccionar el primero como solución temporal
            }
        } catch (Exception e) {
            System.out.println("Error buscando proveedor por nombre: " + e.getMessage());
            return null;
        }
    }


    // Eliminar un proveedor por su ID
    public void eliminar(long id) {
        Proveedor proveedor = encontrarPorId(id);  // Busca el proveedor por ID
        if (proveedor != null) {
            try {
                entityManager.getTransaction().begin();  // Inicia la transacción
                entityManager.remove(proveedor);  // Elimina el proveedor
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
