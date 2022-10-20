package pl.lodz.pas.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pl.lodz.pas.model.ClientTypes.ClientType;
import pl.lodz.pas.repository.Repository;

import java.util.List;

@ApplicationScoped
@Transactional
public class ClientTypeRepository implements Repository<ClientType> {

    @PersistenceContext
    EntityManager em;

    @Override
    public ClientType add(ClientType clientType) {
        try {
            em.persist(clientType);
            return clientType;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(ClientType clientType) {
        try {
            em.remove(em.merge(clientType));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ClientType getById(Long id) {
        try {
            return em.find(ClientType.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ClientType update(ClientType clientType) {
        try {
            ClientType newClientType = em.find(ClientType.class, clientType.getId());
            return newClientType;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ClientType> getAll() {
        try {
            return em.createNamedQuery("ClientType.getAll", ClientType.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ClientType getByType(Class type) {
        try {
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
