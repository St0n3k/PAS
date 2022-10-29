package pl.lodz.pas.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pl.lodz.pas.model.user.ClientTypes.ClientType;
import pl.lodz.pas.repository.Repository;

import java.util.List;
import java.util.Optional;

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
    public void remove(ClientType clientType) {
        try {
            em.remove(em.merge(clientType));
        } catch (Exception e) {
        }
    }

    @Override
    public Optional<ClientType> getById(Long id) {
        return Optional.ofNullable(em.find(ClientType.class, id));
    }

    @Override
    public Optional<ClientType> update(ClientType clientType) {
        return Optional.ofNullable(em.merge(clientType));
    }

    @Override
    public List<ClientType> getAll() {
        return em.createNamedQuery("ClientType.getAll", ClientType.class).getResultList();
    }

    public Optional<ClientType> getByType(Class type) {
        List<ClientType> result = em.createNamedQuery("ClientType.getByType", ClientType.class)
                .setParameter("type", type.getSimpleName())
                .getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }
}
