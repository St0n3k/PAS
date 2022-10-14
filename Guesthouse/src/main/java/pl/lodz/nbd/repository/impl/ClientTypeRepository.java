package pl.lodz.nbd.repository.impl;

import jakarta.persistence.EntityManager;
import pl.lodz.nbd.common.EntityManagerCreator;
import pl.lodz.nbd.model.ClientTypes.ClientType;
import pl.lodz.nbd.repository.Repository;

import java.util.List;

public class ClientTypeRepository implements Repository<ClientType> {
    @Override
    public ClientType add(ClientType clientType) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(clientType);
            em.getTransaction().commit();
            return clientType;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(ClientType clientType) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            em.getTransaction().begin();
            em.remove(em.merge(clientType));
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ClientType getById(Long id) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            return em.find(ClientType.class, id);
        }
    }

    @Override
    public ClientType update(ClientType clientType) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            em.getTransaction().begin();
            ClientType newClientType = em.find(ClientType.class, clientType.getId());
            em.getTransaction().commit();
            return newClientType;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ClientType> getAll() {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            return em.createNamedQuery("ClientType.getAll", ClientType.class).getResultList();
        }
    }

    public ClientType getByType(Class type) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            List<ClientType> result = em.createNamedQuery("ClientType.getByType", ClientType.class).setParameter("type", type.getSimpleName()).getResultList();

            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
